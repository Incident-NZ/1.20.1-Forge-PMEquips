package net.pm_equips.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.config.CommonConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Server-authoritative hitscan and particle trail for ranged weapons. */
public final class ParticleBullets {
    private ParticleBullets() {}

    public record Shot(Vec3 impact, List<LivingEntity> targets) {}

    public static Shot fire(Player shooter, double range, float damage, ParticleOptions tracer) {
        return fire(shooter, range, damage, tracer, false, false);
    }

    public static Shot fire(Player shooter, double range, float damage, ParticleOptions tracer,
                            boolean pierceEntities, boolean ignoreInvulnerability) {
        return fire(shooter, shooter.getLookAngle(), range, damage, tracer, pierceEntities,
                ignoreInvulnerability, false);
    }

    public static Shot fire(Player shooter, Vec3 direction, double range, float damage, ParticleOptions tracer,
                            boolean pierceEntities, boolean ignoreInvulnerability, boolean pierceBlocks) {
        Shot shot = trace(shooter, direction, range, tracer, pierceEntities, pierceBlocks);
        for (LivingEntity target : shot.targets()) {
            if (ignoreInvulnerability) {
                target.invulnerableTime = 0;
                target.hurtTime = 0;
            }
            target.hurt(shooter.damageSources().playerAttack(shooter), damage);
        }
        return shot;
    }

    public static Shot fireExplosion(Player shooter, double range, float damage, double radius,
                                     ParticleOptions tracer, boolean terrainDamage) {
        Shot shot = trace(shooter, shooter.getLookAngle(), range, tracer, false, false);
        if (!(shooter.level() instanceof ServerLevel level)) return shot;

        Vec3 center = shot.impact();
        level.playSound(null, center.x, center.y, center.z,
                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.PLAYERS,
                1.2F, 0.8F);
        level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0, 0, 0, 0);

        double radiusSqr = radius * radius;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(radius), entity -> entity.isAlive() && entity != shooter)) {
            if (target.position().distanceToSqr(center) > radiusSqr
                    || (!CommonConfig.ALLOW_FRIENDLY_FIRE.get() && shooter.isAlliedTo(target))) continue;
            target.invulnerableTime = 0;
            target.hurt(shooter.damageSources().playerAttack(shooter), damage);
        }

        if (terrainDamage && CommonConfig.ALLOW_TERRAIN_DAMAGE.get()) {
            int blockRadius = (int) Math.ceil(radius);
            BlockPos centerPos = BlockPos.containing(center);
            for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-blockRadius, -blockRadius, -blockRadius),
                    centerPos.offset(blockRadius, blockRadius, blockRadius))) {
                if (pos.getCenter().distanceTo(center) <= radius
                        && !level.getBlockState(pos).isAir()
                        && !level.getBlockState(pos).is(Blocks.BEDROCK)) {
                    level.destroyBlock(pos, true);
                }
            }
        }
        return shot;
    }

    public static Shot trace(Player shooter, Vec3 direction, double range, ParticleOptions particle,
                             boolean pierceEntities) {
        return trace(shooter, direction, range, particle, pierceEntities, false);
    }

    public static Shot trace(Player shooter, Vec3 direction, double range, ParticleOptions particle,
                             boolean pierceEntities, boolean pierceBlocks) {
        if (!(shooter.level() instanceof ServerLevel level)) {
            return new Shot(shooter.getEyePosition().add(direction.scale(range)), List.of());
        }

        Vec3 start = shooter.getEyePosition();
        Vec3 requestedEnd = start.add(direction.normalize().scale(range));
        BlockHitResult blockHit = pierceBlocks ? null : level.clip(new ClipContext(start, requestedEnd,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        Vec3 end = blockHit != null && blockHit.getType() == HitResult.Type.BLOCK
                ? blockHit.getLocation() : requestedEnd;

        List<LivingEntity> targets = new ArrayList<>();
        List<HitAt> intersections = new ArrayList<>();
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(start, end).inflate(1.0D), entity -> entity.isAlive() && entity != shooter
                        && (CommonConfig.ALLOW_FRIENDLY_FIRE.get() || !shooter.isAlliedTo(entity)))) {
            target.getBoundingBox().inflate(0.25D).clip(start, end)
                    .ifPresent(point -> intersections.add(new HitAt(target, point, start.distanceToSqr(point))));
        }
        intersections.sort(Comparator.comparingDouble(HitAt::distanceSqr));
        for (HitAt hit : intersections) {
            targets.add(hit.target());
            if (!pierceEntities) {
                end = hit.point();
                break;
            }
        }

        sendTracer(level, start, end, particle);
        return new Shot(end, List.copyOf(targets));
    }

    private static void sendTracer(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions particle) {
        Vec3 delta = end.subtract(start);
        double distance = delta.length();
        int steps = Math.max(1, Math.min(32, (int) Math.ceil(distance / 4.0D)));
        for (int i = 1; i <= steps; i++) {
            Vec3 point = start.add(delta.scale((double) i / steps));
            level.sendParticles(particle, point.x, point.y, point.z, 1, 0, 0, 0, 0);
        }
        level.sendParticles(ParticleTypes.CRIT, end.x, end.y, end.z, 3, 0.08D, 0.08D, 0.08D, 0.01D);
    }

    private record HitAt(LivingEntity target, Vec3 point, double distanceSqr) {}
}
