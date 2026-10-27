package net.pm_equips.entity;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.pm_equips.network.ForgeFeaturePackets;
import net.pm_equips.EntityInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.util.FingerEquipment;
import net.pm_equips.config.CommonConfig;
import net.pm_equips.items.LobEGOWeapon4Aroma;
import net.pm_equips.items.LobEGOWeapon4Heaven;
import net.pm_equips.items.LobEGOWeapon5Star;
import net.pm_equips.items.LobEGOWeapon5WhiteNight;
import net.pm_equips.items.WingRCorp4thRabbitWeapon1;
import net.pm_equips.util.ItemStackData;

/** Shared NPC services; no client class is loaded by a dedicated server. */
public final class MobNPC3 {
    private MobNPC3() {}
    private static final int MAX_SKINS = 48, MAX_PNG_BYTES = 65536, MAX_CATALOG_BYTES = 786432;
    private static final String[] DEFAULT_SKINS = {"builtin:default_1", "builtin:default_2.slim"};
    private static final ConcurrentLinkedQueue<Villager> CONVERSIONS = new ConcurrentLinkedQueue<>();
    private static final Map<ServerLevel, Map<BlockPos, CachedHome>> HOME_CACHE = new WeakHashMap<>();
    private static final Map<Item, Item> AMMO = new HashMap<>();
    private static Map<String, byte[]> skinFiles = Map.of();
    private static MobNPC1 activeShooter;

    public static Path skinDirectory() { return FMLPaths.CONFIGDIR.get().resolve("pm_equips").resolve("npc_skins"); }
    public static void prepareSkinDirectory() {
        try { Files.createDirectories(skinDirectory()); }
        catch (IOException error) { PMEquipsMain.LOGGER.error("Cannot create NPC skin directory", error); }
    }
    public static void createAttributes(EntityAttributeCreationEvent event) { event.put(EntityInit.MOB_NPC_1.get(), MobNPC1.attributes().build()); }
    public record SkinCatalog(Map<String, byte[]> files) {}

    private static boolean validFilename(String name) {
        return !name.isBlank() && name.length() <= 128 && !name.contains("/") && !name.contains("\\")
                && !name.contains("..") && name.toLowerCase(Locale.ROOT).endsWith(".png");
    }
    public static boolean validPNG(byte[] bytes) {
        if (bytes.length < 33 || bytes.length > MAX_PNG_BYTES) return false;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        if (buffer.getLong(0) != 0x89504e470d0a1a0aL || buffer.getInt(12) != 0x49484452) return false;
        return buffer.getInt(16) == 64 && (buffer.getInt(20) == 64 || buffer.getInt(20) == 32);
    }

    public static void reloadSkins() {
        prepareSkinDirectory();
        Map<String, byte[]> loaded = new LinkedHashMap<>();
        int total = 0;
        try (var stream = Files.list(skinDirectory())) {
            for (Path file : stream.sorted().toList()) {
                String name = file.getFileName().toString();
                if (!validFilename(name) || !Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) continue;
                if (loaded.size() >= MAX_SKINS || Files.size(file) > MAX_PNG_BYTES) { PMEquipsMain.LOGGER.warn("NPC skin limit exceeded: {}", name); continue; }
                byte[] bytes = Files.readAllBytes(file);
                if (!validPNG(bytes) || total + bytes.length > MAX_CATALOG_BYTES) { PMEquipsMain.LOGGER.warn("Ignoring invalid/oversized NPC skin: {}", name); continue; }
                total += bytes.length; loaded.put(name, bytes);
            }
        } catch (IOException error) { PMEquipsMain.LOGGER.error("Cannot load NPC skins", error); }
        skinFiles = Map.copyOf(loaded);
        PMEquipsMain.LOGGER.info("Loaded {} external NPC skins", skinFiles.size());
    }

    public static void ensureSkin(MobNPC1 npc) {
        String fixed = CommonConfig.NPC_FIXED_SKIN.get().trim();
        if (!fixed.isEmpty() && skinFiles.containsKey(fixed)) { npc.setSkinName(fixed); return; }
        List<String> choices = new ArrayList<>(skinFiles.keySet().stream().sorted().toList());
        if (CommonConfig.NPC_DEFAULT_SKINS.get()) choices.addAll(List.of(DEFAULT_SKINS));
        if (choices.isEmpty()) { npc.setSkinName("vanilla"); return; }
        if (!choices.contains(npc.getSkinName())) npc.setSkinName(choices.get(npc.getRandom().nextInt(choices.size())));
    }

    @SubscribeEvent public static void onStarting(ServerAboutToStartEvent event) { reloadSkins(); }
    @SubscribeEvent public static void onStopped(ServerStoppedEvent event) {
        CONVERSIONS.clear(); HOME_CACHE.clear(); AMMO.clear(); skinFiles = Map.of(); activeShooter = null;
    }
    @SubscribeEvent public static void onDataSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) reloadSkins();
        SkinCatalog packet = new SkinCatalog(skinFiles);
        if (event.getPlayer() != null) ForgeFeaturePackets.sendTo(event.getPlayer(), packet);
        else for (var player : event.getPlayerList().getPlayers()) ForgeFeaturePackets.sendTo(player, packet);
    }

    @SubscribeEvent public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Villager villager) CONVERSIONS.offer(villager);
        if (event.getEntity() instanceof MobNPC1 npc) ensureSkin(npc);
        if (activeShooter != null && event.getEntity() instanceof Projectile projectile) {
            projectile.setOwner(activeShooter);
            if (projectile instanceof AbstractArrow arrow) arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        }
    }
    @SubscribeEvent public static void afterServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        // EntityJoinLevelEvent can run during chunk IO; defer world queries until the server tick.
        for (int i = 0; i < 128; i++) {
            Villager villager = CONVERSIONS.poll();
            if (villager == null) break;
            if (!(villager.level() instanceof ServerLevel level) || !villager.isAlive() || villager.isRemoved()
                    || villager.getVillagerData().getProfession() != VillagerProfession.NITWIT) continue;
            if (!level.hasChunkAt(villager.blockPosition())) continue;
            if (level.isVillage(villager.blockPosition()) || level.structureManager()
                    .getStructureWithPieceAt(villager.blockPosition(), StructureTags.VILLAGE).isValid()) replaceVillager(villager);
        }
    }
    public static boolean replaceVillager(Villager villager) {
        if (!(villager.level() instanceof ServerLevel level) || villager.getVillagerData().getProfession() != VillagerProfession.NITWIT) return false;
        MobNPC1 npc = EntityInit.MOB_NPC_1.get().create(level);
        if (npc == null) return false;
        npc.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
        npc.setBaby(villager.isBaby()); npc.setCustomName(villager.getCustomName());
        npc.setCustomNameVisible(villager.isCustomNameVisible()); npc.setNoAi(villager.isNoAi());
        npc.setInvulnerable(villager.isInvulnerable());
        npc.setHealth(npc.getMaxHealth() * villager.getHealth() / villager.getMaxHealth());
        npc.setItemSlot(EquipmentSlot.MAINHAND, villager.getMainHandItem().copy());
        npc.setItemSlot(EquipmentSlot.OFFHAND, villager.getOffhandItem().copy());
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            if (!villager.getItemBySlot(slot).isEmpty()) { npc.setItemSlot(slot, villager.getItemBySlot(slot).copy()); break; }
        }
        if (!level.addFreshEntity(npc)) return false;
        villager.releasePoi(MemoryModuleType.HOME); villager.releasePoi(MemoryModuleType.JOB_SITE);
        villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE); villager.releasePoi(MemoryModuleType.MEETING_POINT);
        if (villager.isSleeping()) villager.stopSleeping();
        villager.discard();
        return true;
    }

    public static boolean isUnprovokedEnemy(MobNPC1 npc, LivingEntity target) {
        return CommonConfig.NPC_PVP_DISPOSITION.get() == CommonConfig.NPCDisposition.HOSTILE && differentOwner(npc, target);
    }
    private static boolean differentOwner(MobNPC1 npc, LivingEntity target) {
        if (!npc.isTame() || npc.getServer() == null || !npc.getServer().isPvpAllowed()) return false;
        UUID owner = npc.getOwnerUUID();
        if (target instanceof Player player) return !(player instanceof FakePlayer) && !player.isCreative() && !player.isSpectator() && !player.getUUID().equals(owner);
        return target instanceof MobNPC1 other && other.isTame() && !Objects.equals(owner, other.getOwnerUUID());
    }
    public static boolean canFight(MobNPC1 npc, LivingEntity target) {
        if (target == npc || target.isAlliedTo(npc)) return false;
        if (target instanceof Player || target instanceof MobNPC1) {
            return differentOwner(npc, target) && (isUnprovokedEnemy(npc, target) || npc.getLastHurtByMob() == target
                    || npc.getOwner() != null && (npc.getOwner().getLastHurtByMob() == target || npc.getOwner().getLastHurtMob() == target));
        }
        return target instanceof Enemy || npc.getLastHurtByMob() == target
                || npc.getOwner() != null && npc.getOwner().getLastHurtMob() == target;
    }
    private static MobNPC1 sourceNPC(DamageSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof MobNPC1 npc) return npc;
        if (entity instanceof FakePlayer fake && fake.getPersistentData().hasUUID("PMENPC")) {
            Entity npc = fake.serverLevel().getEntity(fake.getPersistentData().getUUID("PMENPC"));
            return npc instanceof MobNPC1 companion ? companion : null;
        }
        return null;
    }
    public static boolean preventDamage(MobNPC1 target, DamageSource source) {
        MobNPC1 attacker = sourceNPC(source);
        if (attacker != null) return !canFight(attacker, target);
        if (source.getEntity() instanceof Player player) return !differentOwner(target, player);
        return false;
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getTarget() instanceof MobNPC1 npc) {
            Player player = event.getEntity();
            if (!npc.level().isClientSide && npc.isOwnedBy(player)) npc.cycleOrder(player);
            if (npc.isOwnedBy(player) || !differentOwner(npc, player)) event.setCanceled(true);
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onDamage(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getEntity() instanceof MobNPC1 npc && preventDamage(npc, event.getSource())) { event.setCanceled(true); return; }
        MobNPC1 npc = sourceNPC(event.getSource());
        if (npc == null) return;
        if (!canFight(npc, event.getEntity())) { event.setCanceled(true); return; }
        if (event.getSource().getDirectEntity() != npc && !FingerEquipment.isPiercingDamage(event.getSource()))
            event.setAmount(event.getAmount() + npc.getNPCLevel() - 1);
    }
    @SubscribeEvent public static void onLooting(LootingLevelEvent event) {
        DamageSource source = event.getDamageSource();
        if (source == null || !(source.getDirectEntity() instanceof MobNPC1 npc)) return;
        int selected = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, npc.getWeaponItem());
        int main = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, npc.getMainHandItem());
        event.setLootingLevel(Math.max(0, event.getLootingLevel() + selected - main));
    }
    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player || event.getEntity() instanceof MobNPC1) return;
        MobNPC1 npc = sourceNPC(event.getSource());
        if (npc != null && !npc.level().isClientSide) npc.recordKill();
    }

    public static boolean isSpecialRanged(ItemStack stack) {
        return stack.getItem() instanceof LobEGOWeapon5Star || stack.getItem() instanceof LobEGOWeapon5WhiteNight
                || stack.getItem() instanceof LobEGOWeapon4Heaven;
    }

    /** Reuse each item's own firing code. Virtual ammunition never enters the world or its drops. */
    public static void shoot(MobNPC1 npc, LivingEntity target, InteractionHand hand) {
        if (!(npc.level() instanceof ServerLevel level) || !target.isAlive() || !npc.canAttack(target)) return;
        ItemStack weapon = npc.getItemInHand(hand);
        if (weapon.isEmpty()) return;
        if (npc.getWeaponUser(hand) == null) npc.setWeaponUser(hand, new FakePlayer(level,
                new GameProfile(UUID.nameUUIDFromBytes(("pm_equips:npc:" + npc.getUUID() + ":" + hand).getBytes(StandardCharsets.UTF_8)), "[PMENPC]")));
        FakePlayer user = npc.getWeaponUser(hand);
        if (user.getCooldowns().isOnCooldown(weapon.getItem())) return;
        user.getInventory().clearContent();
        user.setItemSlot(EquipmentSlot.MAINHAND, npc.getMainHandItem());
        user.setItemSlot(EquipmentSlot.OFFHAND, npc.getOffhandItem());
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) user.setItemSlot(slot, npc.getItemBySlot(slot).copy());
        user.getPersistentData().putUUID("PMENPC", npc.getUUID());
        Vec3 delta = target.getEyePosition().subtract(npc.getEyePosition()).normalize();
        Vec3 eye = npc.getEyePosition().add(delta.scale(1.25));
        user.setPos(eye.x, eye.y - user.getEyeHeight(), eye.z);
        user.setYRot((float)(Math.atan2(delta.z, delta.x) * 180 / Math.PI) - 90);
        user.setXRot((float)(-Math.asin(delta.y) * 180 / Math.PI));
        user.yHeadRot = user.getYRot(); user.yBodyRot = user.getYRot();
        user.getAbilities().instabuild = false;
        Item ammoItem = FingerEquipment.isThumbRifle(weapon) ? FingerEquipment.ammoItem(FingerEquipment.ammoMode(weapon)) : weapon.getItem() instanceof ProjectileWeaponItem ranged
                ? AMMO.computeIfAbsent(weapon.getItem(), item -> BuiltInRegistries.ITEM.stream()
                        .filter(candidate -> ranged.getAllSupportedProjectiles().test(new ItemStack(candidate))).findFirst().orElse(Items.ARROW)) : Items.ARROW;
        ItemStack ammo = new ItemStack(ammoItem, Math.min(64, ammoItem.getMaxStackSize()));
        user.getInventory().setItem(1, ammo);
        CompoundTag previousData = ItemStackData.copy(weapon);
        if (weapon.getItem() instanceof WingRCorp4thRabbitWeapon1) ItemStackData.update(weapon, tag -> {
            tag.putInt("Ammo", 50); tag.putInt("Reload", 0);
        });
        if (weapon.getItem() instanceof LobEGOWeapon4Aroma) ItemStackData.update(weapon, tag -> {
            tag.put("LoadedArrow", new ItemStack(Items.ARROW).save(new CompoundTag())); tag.putInt("Reload", 0);
        });
        MobNPC1 previous = activeShooter;
        activeShooter = npc;
        try {
            if (weapon.getItem() instanceof CrossbowItem crossbow) {
                ListTag projectiles = new ListTag(); projectiles.add(new ItemStack(Items.ARROW).save(new CompoundTag()));
                weapon.getOrCreateTag().put("ChargedProjectiles", projectiles); CrossbowItem.setCharged(weapon,true);
                CrossbowItem.performShooting(level, user, hand, weapon, 3.15F, 1);
                user.getCooldowns().addCooldown(weapon.getItem(), 25);
            } else {
                weapon.getItem().use(level, user, hand);
                if (user.isUsingItem()) {
                    int remaining = Math.max(0, weapon.getUseDuration() - 40);
                    weapon.getItem().onUseTick(level, user, weapon, remaining);
                    weapon.getItem().releaseUsing(weapon, level, user, remaining);
                    user.stopUsingItem();
                }
                if (weapon.getItem() instanceof BowItem || isSpecialRanged(weapon)) user.getCooldowns().addCooldown(weapon.getItem(), 20);
            }
            npc.setItemSlot(EquipmentSlot.MAINHAND, user.getMainHandItem());
            npc.setItemSlot(EquipmentSlot.OFFHAND, user.getOffhandItem());
            npc.swing(hand);
        } finally {
            activeShooter = previous;
            if (weapon.getItem() instanceof WingRCorp4thRabbitWeapon1 || weapon.getItem() instanceof LobEGOWeapon4Aroma) {
                ItemStackData.update(weapon, tag -> {
                    for (String key : List.of("Ammo", "LoadedArrow", "Reload")) {
                        if (previousData.contains(key)) tag.put(key, previousData.get(key).copy()); else tag.remove(key);
                    }
                });
            }
            // Clear temporary supplies without dropping them, including when a modded weapon throws.
            user.getInventory().clearContent();
        }
    }

    public record Home(BlockPos bed, Set<BlockPos> interior, boolean base) {}
    private record CachedHome(long until, Home home) {}

    /** Flood-fill only loaded blocks. Doors/glass bound the interior even when a door is open. */
    public static Home inspectHome(ServerLevel level, BlockPos bed) {
        Home room = inspectRoom(level, bed);
        return room != null && room.base ? room : null;
    }
    private static Home inspectRoom(ServerLevel level, BlockPos bed) {
        Map<BlockPos, CachedHome> cache = HOME_CACHE.computeIfAbsent(level, ignored -> new HashMap<>());
        CachedHome saved = cache.get(bed);
        if (saved != null && saved.until > level.getGameTime()) return saved.home;
        cache.entrySet().removeIf(entry -> entry.getValue().until <= level.getGameTime());
        Home home = scanHome(level, bed);
        int cells = (home == null ? 0 : home.interior.size()) + cache.values().stream()
                .mapToInt(value -> value.home == null ? 0 : value.home.interior.size()).sum();
        while (!cache.isEmpty() && (cache.size() >= 256 || cells > 262144)) {
            var oldest = cache.entrySet().stream().min(Comparator.comparingLong(entry -> entry.getValue().until)).orElseThrow();
            if (oldest.getValue().home != null) cells -= oldest.getValue().home.interior.size();
            cache.remove(oldest.getKey());
        }
        cache.put(bed.immutable(), new CachedHome(level.getGameTime() + 200, home));
        return home;
    }
    private static Home scanHome(ServerLevel level, BlockPos bed) {
        if (!level.hasChunkAt(bed) || !(level.getBlockState(bed).getBlock() instanceof BedBlock)) return null;
        Set<BlockPos> interior = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(bed.above());
        boolean door = false, window = false;
        while (!queue.isEmpty()) {
            BlockPos position = queue.removeFirst();
            if (interior.contains(position)) continue;
            if (interior.size() >= 65536 || Math.abs(position.getX() - bed.getX()) > 64
                    || Math.abs(position.getZ() - bed.getZ()) > 64 || Math.abs(position.getY() - bed.getY()) > 64
                    || position.getY() < level.getMinBuildHeight() || position.getY() >= level.getMaxBuildHeight()
                    || !level.hasChunkAt(position)) return null;
            BlockState state = level.getBlockState(position);
            if (state.getBlock() instanceof DoorBlock) { door = true; continue; }
            if (state.getBlock() instanceof StainedGlassBlock || state.getBlock() instanceof StainedGlassPaneBlock
                    || state.is(Blocks.GLASS) || state.is(Blocks.TINTED_GLASS) || state.is(Blocks.GLASS_PANE)) { window = true; continue; }
            if (!roomSpace(level, position, state)) continue;
            if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, position.getX(), position.getZ()) <= position.getY()) return null;
            interior.add(position.immutable());
            for (Direction direction : Direction.values()) queue.add(position.relative(direction));
        }
        if (!door) return null;
        boolean cube = false;
        for (BlockPos origin : interior) {
            boolean fits = true;
            for (int x = 0; x < 3 && fits; x++) for (int y = 0; y < 3 && fits; y++) for (int z = 0; z < 3; z++)
                if (!interior.contains(origin.offset(x, y, z))) { fits = false; break; }
            if (fits) { cube = true; break; }
        }
        return new Home(bed.immutable(), Set.copyOf(interior), window && cube);
    }
    private static boolean roomSpace(ServerLevel level, BlockPos position, BlockState state) {
        return state.getFluidState().isEmpty() && (state.isAir() || state.getCollisionShape(level, position).isEmpty()
                || state.getBlock() instanceof BedBlock || state.is(BlockTags.WOOL_CARPETS));
    }
    public static BlockPos findRestPlace(MobNPC1 npc) {
        ServerLevel level = (ServerLevel)npc.level();
        List<BlockPos> beds = level.getPoiManager().getInRange(holder -> holder.is(PoiTypes.HOME), npc.blockPosition(), 64, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos).filter(level::hasChunkAt).sorted(Comparator.comparingDouble(pos -> pos.distSqr(npc.blockPosition()))).toList();
        for (BlockPos bed : beds) {
            BlockState state = level.getBlockState(bed);
            if (!(state.getBlock() instanceof BedBlock) || state.getValue(BedBlock.PART) != BedPart.HEAD || state.getValue(BedBlock.OCCUPIED)) continue;
            Home home = inspectRoom(level, bed);
            if (npc.isTame() && (home == null || !home.base && bed.distSqr(npc.blockPosition()) > 16 * 16)) continue;
            if (home != null && !level.getEntitiesOfClass(LivingEntity.class, new AABB(bed).inflate(65),
                    entity -> entity instanceof Enemy && entity.isAlive() && home.interior.contains(entity.blockPosition())).isEmpty()) continue;
            return bed;
        }
        BlockPos wool = null;
        double nearest = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(npc.blockPosition().offset(-8, -3, -8), npc.blockPosition().offset(8, 3, 8))) {
            if (!level.hasChunkAt(candidate) || !level.hasChunkAt(candidate.above())) continue;
            BlockState state = level.getBlockState(candidate);
            if (!(state.is(BlockTags.WOOL) || state.is(BlockTags.WOOL_CARPETS))) continue;
            BlockPos standing = state.is(BlockTags.WOOL_CARPETS) ? candidate : candidate.above();
            if (!level.getBlockState(standing.above()).isAir() || !level.getFluidState(standing).isEmpty()) continue;
            double distance = standing.distSqr(npc.blockPosition());
            if (distance < nearest) { nearest = distance; wool = standing.immutable(); }
        }
        return wool == null ? npc.blockPosition() : wool;
    }
}
