// node scripts/generate-epic-fight-armor.cjs [--check]
// Convert the GeckoLib armor assets to meshes driven by Epic Fight's player armature.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const project = path.resolve(__dirname, '..');
const java = path.join(project, 'src/main/java/net/pm_equips');
const assets = path.join(project, 'src/main/resources/assets/pm_equips');
const check = process.argv.includes('--check');
const readJava = file => fs.readFileSync(path.join(java, file), 'utf8');

// Joint indices and bend heights in pixels are shared by both Epic Fight player armatures.
const regions = {
    armorHead: { part: 'head', joint: 9 },
    armorBody: { part: 'body', joints: [7, 8], heights: [14, 22] },
    armorRightArm: { part: 'rightArm', joints: [12, 14, 11], heights: [16, 18, 20] },
    armorLeftArm: { part: 'leftArm', joints: [17, 19, 16], heights: [16, 18, 20] },
    armorRightLeg: { part: 'rightLeg', joints: [2, 3, 1], heights: [4, 6, 8] },
    armorLeftLeg: { part: 'leftLeg', joints: [5, 6, 4], heights: [4, 6, 8] },
    armorRightBoot: { part: 'rightLeg', joint: 2 },
    armorLeftBoot: { part: 'leftLeg', joint: 5 }
};

const faces = {
    west: [3, 2, 0, 1], east: [4, 5, 7, 6],
    north: [2, 4, 6, 0], south: [5, 3, 1, 7],
    up: [3, 5, 4, 2], down: [0, 6, 7, 1]
};
const opposite = { west: 'east', east: 'west', up: 'down', down: 'up' };
const zero = [0, 0, 0];

function boxUvs([u, v], [width, height, depth]) {
    const [w, h, d] = [width, height, depth].map(Math.floor);
    return {
        west: { uv: [u + d + w, v + d], uv_size: [d, h] },
        east: { uv: [u, v + d], uv_size: [d, h] },
        north: { uv: [u + d, v + d], uv_size: [w, h] },
        south: { uv: [u + 2 * d + w, v + d], uv_size: [w, h] },
        up: { uv: [u + d, v], uv_size: [w, d] },
        down: { uv: [u + d + w, v + d], uv_size: [w, -d] }
    };
}

function rotateAround([x, y, z], [pivotX, pivotY, pivotZ], rotation) {
    if (!rotation.some(Boolean)) return [x, y, z];
    // GeckoLib flips the geometry's X axis, which changes the signs of X/Y rotations.
    const [rx, ry, rz] = [-rotation[0], -rotation[1], rotation[2]].map(angle => angle * Math.PI / 180);
    x += pivotX;
    y -= pivotY;
    z -= pivotZ;
    [y, z] = [y * Math.cos(rx) - z * Math.sin(rx), y * Math.sin(rx) + z * Math.cos(rx)];
    [x, z] = [x * Math.cos(ry) + z * Math.sin(ry), -x * Math.sin(ry) + z * Math.cos(ry)];
    [x, y] = [x * Math.cos(rz) - y * Math.sin(rz), x * Math.sin(rz) + y * Math.cos(rz)];
    return [x - pivotX, y + pivotY, z + pivotZ];
}

function cubeCorners({ origin, size }, inflation) {
    const [x, y, z] = origin;
    const [w, h, d] = size;
    const x0 = -x - w - inflation, x1 = -x + inflation;
    const y0 = y - inflation, y1 = y + h + inflation;
    const z0 = z - inflation, z1 = z + d + inflation;
    return [
        [x0, y0, z0], [x0, y0, z1], [x0, y1, z0], [x0, y1, z1],
        [x1, y1, z0], [x1, y1, z1], [x1, y0, z0], [x1, y0, z1]
    ];
}

function splitAtHeight(polygon, height) {
    const below = [], above = [];
    for (let i = 0; i < polygon.length; i++) {
        const a = polygon[i], b = polygon[(i + 1) % polygon.length];
        (a[1] <= height ? below : above).push(a);
        if ((a[1] <= height) === (b[1] <= height)) continue;
        const fraction = (height - a[1]) / (b[1] - a[1]);
        const intersection = a.map((value, axis) => value + (b[axis] - value) * fraction);
        intersection[1] = height;
        below.push(intersection);
        above.push(intersection);
    }
    return [below, above].filter(side => side.length >= 3);
}

function skin(region, height) {
    if (region.joint !== undefined) return [[region.joint, 1]];
    const { joints, heights } = region;
    if (height <= heights[0]) return [[joints[0], 1]];
    for (let i = 1; i < heights.length; i++) {
        if (height > heights[i]) continue;
        const upper = (height - heights[i - 1]) / (heights[i] - heights[i - 1]);
        return [[joints[i - 1], 1 - upper], [joints[i], upper]].filter(([, weight]) => weight > 0);
    }
    return [[joints.at(-1), 1]];
}

function normalOf([a, b, c]) {
    const [x, y, z] = b.slice(0, 3).map((value, i) => value - a[i]);
    const [u, v, w] = c.slice(0, 3).map((value, i) => value - a[i]);
    const vector = [y * w - z * v, z * u - x * w, x * v - y * u];
    const length = Math.hypot(...vector);
    return length > 1e-7 ? vector.map(value => value / length) : null;
}

// Epic Fight reads Blender coordinates and rotates them -90 degrees about X.
function toAssetAxes([x, y, z], scale = 1) {
    return [x * scale, -z * scale, y * scale];
}

function makeMesh(file) {
    const geometries = JSON.parse(fs.readFileSync(path.join(assets, file), 'utf8'))['minecraft:geometry'];
    assert.equal(geometries.length, 1, file);
    const { description, bones } = geometries[0];
    const indexedBones = new Map(bones.map(bone => [bone.name, bone]));
    const positions = [], uvs = [], normals = [], weights = [], vcounts = [], vindices = [];
    const parts = new Map(), positionIds = new Map(), uvIds = new Map(), normalIds = new Map(), weightIds = new Map();

    function intern(values, width, ids, entry) {
        const rounded = entry.map(value => Number(value.toFixed(7)));
        const key = rounded.join(',');
        if (!ids.has(key)) {
            ids.set(key, values.length / width);
            values.push(...rounded);
        }
        return ids.get(key);
    }

    function appendVertex([x, y, z, u, v], region, normal, indices) {
        const influences = skin(region, y);
        // Two vertices at the same point can belong to different body regions.
        const key = JSON.stringify([x, y, z, influences]);
        if (!positionIds.has(key)) {
            positionIds.set(key, positions.length / 3);
            positions.push(...toAssetAxes([x, y, z], 1 / 16));
            vcounts.push(influences.length);
            for (const [joint, weight] of influences)
                vindices.push(joint, intern(weights, 1, weightIds, [weight]));
        }
        indices.push(positionIds.get(key), intern(uvs, 2, uvIds, [u / description.texture_width, v / description.texture_height]),
            intern(normals, 3, normalIds, toAssetAxes(normal)));
    }

    for (const bone of bones) {
        if (!bone.cubes?.length) continue;
        const ancestry = [bone];
        while (ancestry.at(-1).parent) {
            const parent = indexedBones.get(ancestry.at(-1).parent);
            assert(parent && !ancestry.includes(parent), file + ' invalid bone parent');
            ancestry.push(parent);
        }
        const attachment = ancestry.find(ancestor => ancestor.name in regions);
        assert(attachment, file + ' has no player joint for ' + bone.name);
        const region = regions[attachment.name];
        if (!parts.has(region.part)) parts.set(region.part, []);
        const indices = parts.get(region.part);
        for (const cube of bone.cubes) {
            const corners = cubeCorners(cube, cube.inflate ?? bone.inflate ?? 0).map(point => {
                let vertex = rotateAround(point, cube.pivot ?? zero, cube.rotation ?? zero);
                for (const ancestor of ancestry)
                    vertex = rotateAround(vertex, ancestor.pivot ?? zero, ancestor.rotation ?? zero);
                return vertex;
            });
            const box = Array.isArray(cube.uv);
            const uvByFace = box ? boxUvs(cube.uv, cube.size) : cube.uv;
            for (const [face, coords] of Object.entries(uvByFace)) {
                const mirror = cube.mirror === true;
                const flipFace = mirror && (face === 'east' || face === 'west' || !box);
                const cornersForFace = faces[(flipFace && opposite[face]) || face];
                assert(cornersForFace, file + ' unknown face ' + face);
                const [u, v] = coords.uv, [w, h] = coords.uv_size;
                const left = mirror ? u : u + w, right = mirror ? u + w : u;
                const uv = [[left, v], [right, v], [right, v + h], [left, v + h]];
                const rotation = ((coords.uv_rotation ?? 0) / 90 + 4) % 4;
                const polygon = cornersForFace.map((corner, i) => [...corners[corner], ...uv[(i + rotation) % 4]]);
                const normal = normalOf(polygon);
                if (!normal) continue;
                // Preserve the UVs while adding vertices across elbows, knees and the waist.
                const sections = (region.heights ?? []).reduce(
                    (polygons, height) => polygons.flatMap(poly => splitAtHeight(poly, height)), [polygon]);
                for (const section of sections) {
                    for (let i = 1; i + 1 < section.length; i++) {
                        const triangle = [section[0], section[i], section[i + 1]];
                        if (!normalOf(triangle)) continue;
                        for (const vertex of triangle) appendVertex(vertex, region, normal, indices);
                    }
                }
            }
        }
    }

    const array = (values, stride) => ({ stride, count: values.length / stride, array: values });
    assert(positions.length && vcounts.length === positions.length / 3, file + ' invalid vertex count');
    assert.equal(vindices.length, 2 * vcounts.reduce((sum, count) => sum + count, 0), file);
    return {
        vertices: {
            positions: array(positions, 3), uvs: array(uvs, 2), normals: array(normals, 3),
            vcounts: array(vcounts, 1), weights: array(weights, 1), vindices: array(vindices, 1),
            parts: Object.fromEntries([...parts].filter(([, indices]) => indices.length)
                .map(([part, indices]) => [part, array(indices, 3)]))
        }
    };
}

function write(relative, value) {
    const file = path.join(assets, relative);
    const output = JSON.stringify(value) + '\n';
    if (check) {
        assert.equal(fs.readFileSync(file, 'utf8'), output, relative + ' needs regeneration');
    } else {
        fs.mkdirSync(path.dirname(file), { recursive: true });
        fs.writeFileSync(file, output);
    }
}

const armor = new Map();
for (const file of fs.readdirSync(path.join(java, 'client/renderer')).filter(file => file.endsWith('.java'))) {
    const renderer = readJava('client/renderer/' + file);
    const definition = /extends\s+(?:GeoArmorRenderer|FullBodyArmorRenderer)<(\w+)>[\s\S]*?super\(new (\w+)\(/.exec(renderer);
    if (!definition) continue;
    const [, itemClass, modelClass] = definition;
    const model = readJava('client/models/' + modelClass + '.java');
    const geometry = /"pm_equips",\s*"(geo\/[^"]+\.geo\.json)"/.exec(model)?.[1];
    const texture = /"pm_equips",\s*"(textures\/[^"]+\.png)"/.exec(model)?.[1];
    assert(geometry && texture, modelClass + ' must declare the geometry and texture');
    assert(fs.existsSync(path.join(assets, texture)), texture);
    assert(!armor.has(itemClass), itemClass + ' has multiple armor renderers');
    armor.set(itemClass, { geometry, texture });
}

const used = new Set();
const geometries = new Set();
let items = 0;
for (const [, item, reference, constructor] of readJava('ItemInit.java').matchAll(
    /ITEMS\.register\("([^"]+)",\s*(?:(\w+)::new|\(\)\s*->\s*new\s+(\w+)\()/g)) {
    const itemClass = reference ?? constructor;
    let rendererClass = itemClass;
    while (!armor.has(rendererClass) && fs.existsSync(path.join(java, rendererClass + '.java'))) {
        const parent = /class\s+\w+\s+extends\s+(\w+)/.exec(readJava(rendererClass + '.java'))?.[1];
        if (!parent || parent === rendererClass) break;
        rendererClass = parent;
    }
    const model = armor.get(rendererClass);
    if (!model) continue;
    const meshName = path.basename(model.geometry, '.geo.json');
    if (!geometries.has(meshName)) {
        write('animmodels/pm_equips_armor/' + meshName + '.json', makeMesh(model.geometry));
        geometries.add(meshName);
    }
    write('animmodels/armor/' + item + '.json', {
        parent: 'pm_equips:pm_equips_armor/' + meshName,
        render_properties: { texture_path: 'pm_equips:' + model.texture }
    });
    used.add(rendererClass);
    items++;
}
assert.equal(used.size, armor.size, 'A GeoArmorRenderer has no registered item');
console.log(`${check ? 'Verified' : 'Generated'} ${items} Epic Fight armor items, ${used.size} renderers and ${geometries.size} distinct meshes.`);
