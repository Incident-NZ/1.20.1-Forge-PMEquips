package net.pm_equips.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;
import net.pm_equips.SoundInit;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * 魔法弾銃（エンティティなし）
 * - ブロック貫通ヒットスキャン
 * - 軌跡はパーティクルのみ
 * - リロードなし（撃つたびにインベントリから弾薬消費）
 * - EGOP3MagicBullet 装備中は攻撃力 +3
 */
public class EGOW4MagicBullet extends ProjectileWeaponItem {

    private static final int COOLDOWN_TICKS = 40;
    private static final double RANGE = 128.0;
    private static final double HIT_RADIUS = 0.75;
    private static final int MAX_PIERCE = 0;
    /** EGOP3MagicBullet 同時装備時の攻撃力ボーナス */
    private static final float SET_BONUS_DAMAGE = 3.0F;

    public EGOW4MagicBullet(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(gun);
        }

        if (!hasAmmo(player)) {
            level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.fail(gun);
        }

        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundInit.EGO_MAGIC_BULLET.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.success(gun);
        }

        shootPiercingHitscan(level, player);
        consumeAmmo(player);

        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

        return InteractionResultHolder.success(gun);
    }

    private void shootPiercingHitscan(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = start.add(look.scale(RANGE));

        float damage = 20 + level.random.nextInt(2); // 20-22

        // EGOP3MagicBullet を装備していれば攻撃力 +3
        if (isWearingMagicBulletPage(player)) {
            damage += SET_BONUS_DAMAGE;
        }

        AABB searchBox = new AABB(start, end).inflate(HIT_RADIUS + 1.0);
        List<LivingEntity> candidates = level.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                e -> e != player && e.isAlive() && e.isPickable()
        );

        candidates.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(start)));

        int hitCount = 0;
        for (LivingEntity target : candidates) {
            if (!rayIntersectsEntity(start, end, target)) {
                continue;
            }

            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(level.damageSources().playerAttack(player), damage);
            hitCount++;

            if (level instanceof ServerLevel server) {
                Vec3 p = target.getBoundingBox().getCenter();
                server.sendParticles(ParticleTypes.ENCHANTED_HIT, p.x, p.y, p.z, 8, 0.2, 0.2, 0.2, 0.05);
                server.sendParticles(ParticleTypes.WITCH, p.x, p.y, p.z, 6, 0.15, 0.15, 0.15, 0.1);
            }

            if (MAX_PIERCE > 0 && hitCount >= MAX_PIERCE) {
                break;
            }
        }

        if (level instanceof ServerLevel server) {
            spawnMagicTrail(server, start, end);
        }
    }

    /** EGOP3MagicBullet を1部位でも装備しているか */
    private static boolean isWearingMagicBulletPage(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof EGOP3MagicBullet) {
                return true;
            }
        }
        return false;
    }

    private boolean rayIntersectsEntity(Vec3 start, Vec3 end, LivingEntity entity) {
        AABB box = entity.getBoundingBox().inflate(HIT_RADIUS);
        return box.clip(start, end).isPresent() || box.contains(start);
    }

    private void spawnMagicTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        if (distance < 0.1) return;

        Vec3 step = direction.normalize().scale(0.55);
        Vec3 current = start;
        int count = (int) (distance / 0.55);

        for (int i = 0; i < count; i++) {
            current = current.add(step);
            level.sendParticles(ParticleTypes.ENCHANTED_HIT, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.WITCH, current.x, current.y, current.z, 1, 0.03, 0.03, 0.03, 0.0);
            }
        }
    }

    private boolean hasAmmo(Player player) {
        if (player.getAbilities().instabuild) return true;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemInit.MAGIC_BULLET_AMMO.get()) && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void consumeAmmo(Player player) {
        if (player.getAbilities().instabuild) return;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemInit.MAGIC_BULLET_AMMO.get()) && !stack.isEmpty()) {
                stack.shrink(1);
                return;
            }
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ItemInit.MAGIC_BULLET_AMMO.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return (int) RANGE;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.MAGIC_BULLET_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}