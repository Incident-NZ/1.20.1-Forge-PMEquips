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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;
import net.pm_equips.SoundInit;

import java.util.function.Predicate;

/**
 * 単発・二丁両対応ピストル
 * 両手にこの銃を持っているときだけ二丁発射
 */
public class EGOW4LamentR extends ProjectileWeaponItem {

    private static final float DAMAGE = 2.0f;
    private static final float DUAL_DAMAGE = 4.0f;     // 二丁時の1発ダメージ
    private static final int COOLDOWN_TICKS = 20;
    private static final int DUAL_COOLDOWN_TICKS = 10;
    private static final double RANGE = 128.0;
    private static final double DUAL_SPREAD_ANGLE = 3.5; // 二丁時の左右角度（度）

    public EGOW4LamentR(Properties properties) {
        super(properties.durability(3000).setNoRepair());
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() == ItemInit.PISTOL_BULLET_AMMO.get();
    }

    @Override
    public int getDefaultProjectileRange() {
        return (int) RANGE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);

        // 両手にこの銃を持っているか判定
        boolean dual = isDualWielding(player);

        int ammoCost = dual ? 2 : 1;

        if (!hasAmmo(player, ammoCost)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal(dual ? "弾薬不足（2発必要） / Need 2 ammo" : "弾薬切れ / No Ammo"),
                        true
                );
                level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return InteractionResultHolder.fail(gun);
        }

        // クライアントは音だけ
        if (level.isClientSide) {
            float pitch = 1.0F;
            float volume = 1.0F;
            level.playSound(player, player.blockPosition(), SoundInit.EGO_LAMENT.get(), SoundSource.PLAYERS, volume, pitch);
            return InteractionResultHolder.consume(gun);
        }

        // ===== サーバー側 =====
        if (dual) {
            shootDual(level, player);
            // 両方の銃の耐久を減らす
            hurtBothGuns(player);
            player.getCooldowns().addCooldown(this, DUAL_COOLDOWN_TICKS);
        } else {
            shootSingle(level, player);
            gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        consumeAmmo(player, ammoCost);
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.consume(gun);
    }

    /** メインハンドとオフハンドの両方にこの銃があるか */
    private boolean isDualWielding(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off  = player.getOffhandItem();
        // 例: 同じシリーズなら二丁扱いにする
        return main.getItem() instanceof EGOW4LamentR && off.getItem() instanceof EGOW4LamentR;
    }

    /** 単発発射 */
    private void shootSingle(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        fireOneShot(level, player, eyePos, look, DAMAGE);
    }

    /** 二丁発射（左右に少しずらす） */
    private void shootDual(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        Vec3 rightDir = rotateYaw(look, DUAL_SPREAD_ANGLE);
        Vec3 leftDir  = rotateYaw(look, -DUAL_SPREAD_ANGLE);

        fireOneShot(level, player, eyePos, rightDir, DUAL_DAMAGE);
        fireOneShot(level, player, eyePos, leftDir, DUAL_DAMAGE);
    }

    private void fireOneShot(Level level, Player player, Vec3 eyePos, Vec3 direction, float damage) {
        Vec3 endPos = eyePos.add(direction.scale(RANGE));

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                level,
                player,
                eyePos,
                endPos,
                player.getBoundingBox().expandTowards(direction.scale(RANGE)).inflate(1.0D),
                e -> e != player && e instanceof LivingEntity && e.isAlive()
        );

        Vec3 hitPos;
        if (entityHit != null) {
            hitPos = entityHit.getLocation();
            if (entityHit.getEntity() instanceof LivingEntity target) {
                target.hurt(level.damageSources().playerAttack(player), damage);
            }
        } else {
            HitResult blockHit = level.clip(new ClipContext(
                    eyePos, endPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            ));
            hitPos = blockHit.getLocation();
        }

        spawnBulletTrail((ServerLevel) level, eyePos, hitPos);
        spawnImpactParticles((ServerLevel) level, hitPos);
    }

    /** 両手の銃の耐久を減らす */
    private void hurtBothGuns(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off  = player.getOffhandItem();

        if (main.getItem() == this) {
            main.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        if (off.getItem() == this) {
            off.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
        }
    }

    private Vec3 rotateYaw(Vec3 vec, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;
        return new Vec3(x, vec.y, z).normalize();
    }

    private void spawnBulletTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        if (distance < 0.1) return;

        Vec3 step = direction.normalize().scale(0.4);
        Vec3 current = start;
        int count = (int) (distance / 0.4);

        for (int i = 0; i < count; i++) {
            current = current.add(step);
            level.sendParticles(ParticleTypes.ASH, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.WHITE_ASH, current.x, current.y, current.z, 1, 0.01, 0.01, 0.01, 0.0);
            }
        }
    }

    private void spawnImpactParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.ASH, pos.x, pos.y, pos.z, 10, 0.22, 0.22, 0.22, 0.13);
        level.sendParticles(ParticleTypes.WHITE_ASH, pos.x, pos.y, pos.z, 5, 0.18, 0.18, 0.18, 0.04);
    }

    private boolean hasAmmo(Player player, int amount) {
        if (player.getAbilities().instabuild) return true;

        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemInit.PISTOL_BULLET_AMMO.get())) {
                count += stack.getCount();
                if (count >= amount) return true;
            }
        }
        return false;
    }

    private void consumeAmmo(Player player, int amount) {
        if (player.getAbilities().instabuild) return;

        int remaining = amount;
        for (int i = 0; i < player.getInventory().items.size() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.is(ItemInit.PISTOL_BULLET_AMMO.get())) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.hurt(attacker.level().damageSources().generic(), DAMAGE);
        stack.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }
}
