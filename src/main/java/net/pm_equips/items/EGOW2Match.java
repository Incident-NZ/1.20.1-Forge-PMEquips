package net.pm_equips.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.BlockInit;
import net.pm_equips.ItemInit;

import java.util.List;
import java.util.function.Predicate;

/**
 * 爆発弾銃（エンティティなし）
 * - ヒットスキャン
 * - 着弾点で爆発パーティクル＋範囲ダメージ
 * - 地形破壊なし
 * - リロードなし（撃つたびにインベントリから弾薬消費）
 */
public class EGOW2Match extends ProjectileWeaponItem {

    private static final float MIN_DAMAGE = 20.0F;
    private static final int RANGE = 128;
    private static final int COOLDOWN_TICKS = 100;

    /** 爆発の有効半径（ブロック） */
    private static final double EXPLOSION_RADIUS = 5.0;

    public EGOW2Match(Properties properties) {
        super(properties.durability(1000));
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ItemInit.EXPLOSIVE_BULLET_AMMO.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return RANGE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);

        // インベントリに弾薬があるか
        if (!hasAmmo(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("弾薬切れ / No Ammo"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return InteractionResultHolder.fail(gun);
        }

        // クライアントは発射音のみ
        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.2F, 0.8F);
            return InteractionResultHolder.consume(gun);
        }

        // ===== サーバー =====
        shootExplosiveHitscan(level, player);
        consumeAmmo(player);

        gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.consume(gun);
    }

    private void shootExplosiveHitscan(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 endPos = eyePos.add(look.scale(RANGE));

        // エンティティ優先でヒット判定
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level,
                player,
                eyePos,
                endPos,
                player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0D),
                e -> e != player && e instanceof LivingEntity && e.isAlive()
        );

        Vec3 hitPos;
        if (entityHit != null) {
            hitPos = entityHit.getLocation();
        } else {
            HitResult blockHit = level.clip(new ClipContext(
                    eyePos, endPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            ));
            hitPos = blockHit.getLocation();
        }

        float damage = MIN_DAMAGE + level.random.nextInt(11);

        // 弾跡パーティクル
        spawnBulletTrail((ServerLevel) level, eyePos, hitPos);

        // 着弾点で爆発（地形破壊なし）
        applyExplosion((ServerLevel) level, player, hitPos, damage);

        // 発射音
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.2F, 0.8F);
    }

    /**
     * 地形を壊さない爆発：パーティクル＋範囲内 LivingEntity にダメージ
     */
    private void applyExplosion(ServerLevel level, Player shooter, Vec3 center, float baseDamage) {
        // 爆発パーティクル
        level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.SMOKE, center.x, center.y, center.z, 20, 0.6, 0.6, 0.6, 0.05);
        level.sendParticles(ParticleTypes.FLAME, center.x, center.y, center.z, 15, 0.5, 0.5, 0.5, 0.08);
        level.sendParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 25, 0.8, 0.8, 0.8, 0.2);

        // 爆発音
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.4F, 0.9F + level.random.nextFloat() * 0.2F);

        // 範囲ダメージ
        AABB box = new AABB(
                center.x - EXPLOSION_RADIUS, center.y - EXPLOSION_RADIUS, center.z - EXPLOSION_RADIUS,
                center.x + EXPLOSION_RADIUS, center.y + EXPLOSION_RADIUS, center.z + EXPLOSION_RADIUS
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e.isAlive() && e != shooter
        );

        for (LivingEntity target : targets) {
            double dist = target.position().distanceTo(center);
            if (dist > EXPLOSION_RADIUS) continue;

            // 距離で減衰（中心ほど強い）
            float falloff = (float) (1.0 - (dist / EXPLOSION_RADIUS));
            float dmg = baseDamage * Math.max(0.35F, falloff);

            // Iフレーム無視してダメージ
            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(level.damageSources().explosion(shooter, shooter), dmg);

            // 軽いノックバック
            Vec3 knock = target.position().subtract(center).normalize().scale(0.4 + falloff * 0.6);
            target.setDeltaMovement(target.getDeltaMovement().add(knock.x, 0.25 + falloff * 0.2, knock.z));
            target.hurtMarked = true;
        }
    }

    private void spawnBulletTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        if (distance < 0.1) return;

        Vec3 step = direction.normalize().scale(0.5);
        Vec3 current = start;
        int count = (int) (distance / 0.5);

        for (int i = 0; i < count; i++) {
            current = current.add(step);
            level.sendParticles(ParticleTypes.CRIT, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.SMOKE, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }
    }

    private boolean hasAmmo(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemInit.EXPLOSIVE_BULLET_AMMO.get()) && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void consumeAmmo(Player player) {
        if (player.getAbilities().instabuild) return;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemInit.EXPLOSIVE_BULLET_AMMO.get()) && !stack.isEmpty()) {
                stack.shrink(1);
                return;
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }
        target.hurt(attacker.level().damageSources().explosion(attacker, attacker), MIN_DAMAGE);
        stack.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(BlockInit.BlockItems.TETH_PE_BOX.get());
    }
}
