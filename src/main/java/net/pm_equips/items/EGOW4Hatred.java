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
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.BlockInit;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * - 近接: 攻撃力8 / 速度1.2 / 耐久3000
 * - 右クリック: パーティクル式魔法弾（耐久消費あり）
 *   - 敵: 魔法ダメージ 5〜8
 *   - 味方（プレイヤー・ペット等）: HP 5〜8 回復
 */
public class EGOW4Hatred extends SwordItem {

    private static final double RANGE = 64.0;
    private static final double HIT_RADIUS = 0.8;
    private static final int COOLDOWN_TICKS = 15;

    public EGOW4Hatred() {
        // 攻撃力: 1（基本）+ 7 = 8 / 攻撃速度: 4.0 + (-2.8) = 1.2
        super(new CustomTier(), 7, -2.8f, new Properties());
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
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.2F);
            return InteractionResultHolder.success(stack);
        }

        // パーティクル式魔法弾
        shootMagicBolt(level, player);

        // 右クリックでも耐久消費
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.2F);

        return InteractionResultHolder.success(stack);
    }

    private void shootMagicBolt(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = start.add(look.scale(RANGE));

        int amount = 5 + level.random.nextInt(4); // 5〜8

        AABB searchBox = new AABB(start, end).inflate(HIT_RADIUS + 1.0);
        List<LivingEntity> candidates = level.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                e -> e != player && e.isAlive() && e.isPickable()
        );

        candidates.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(start)));

        LivingEntity hit = null;
        for (LivingEntity target : candidates) {
            if (rayIntersectsEntity(start, end, target)) {
                hit = target;
                break; // 最初に当たった1体のみ
            }
        }

        if (hit != null) {
            if (isAlly(player, hit)) {
                // 味方: 回復
                hit.heal(amount);
                if (level instanceof ServerLevel server) {
                    Vec3 p = hit.getBoundingBox().getCenter();
                    server.sendParticles(ParticleTypes.HEART, p.x, p.y, p.z, 6, 0.3, 0.3, 0.3, 0.05);
                    server.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 8, 0.2, 0.2, 0.2, 0.02);
                }
                level.playSound(null, hit.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.4F);
            } else {
                // 敵: 魔法ダメージ
                hit.invulnerableTime = 0;
                hit.hurtTime = 0;
                hit.hurt(level.damageSources().magic(), amount);
                if (level instanceof ServerLevel server) {
                    Vec3 p = hit.getBoundingBox().getCenter();
                    server.sendParticles(ParticleTypes.WITCH, p.x, p.y, p.z, 10, 0.25, 0.25, 0.25, 0.05);
                    server.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 6, 0.15, 0.15, 0.15, 0.1);
                }
                level.playSound(null, hit.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0F, 0.8F);
            }
            // 着弾位置まで軌跡
            if (level instanceof ServerLevel server) {
                spawnTrail(server, start, hit.getBoundingBox().getCenter(), isAlly(player, hit));
            }
        } else {
            // 空振り: 最大射程まで軌跡
            if (level instanceof ServerLevel server) {
                spawnTrail(server, start, end, false);
            }
        }
    }

    /** プレイヤー・同チーム・飼いならしペット等を味方と判定 */
    private boolean isAlly(Player shooter, LivingEntity target) {
        if (target == shooter) {
            return true;
        }
        if (target.isAlliedTo(shooter)) {
            return true;
        }
        if (target instanceof TamableAnimal tamable && tamable.isOwnedBy(shooter)) {
            return true;
        }
        if (target instanceof OwnableEntity ownable) {
            return shooter.getUUID().equals(ownable.getOwnerUUID());
        }
        // 他プレイヤー（チーム未所属）は敵扱い。チーム所属は isAlliedTo でカバー
        return false;
    }

    private boolean rayIntersectsEntity(Vec3 start, Vec3 end, LivingEntity entity) {
        AABB box = entity.getBoundingBox().inflate(HIT_RADIUS);
        return box.clip(start, end).isPresent() || box.contains(start);
    }

    private void spawnTrail(ServerLevel level, Vec3 start, Vec3 end, boolean healTrail) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        if (distance < 0.1) return;

        Vec3 step = direction.normalize().scale(0.45);
        Vec3 current = start;
        int count = (int) (distance / 0.45);

        for (int i = 0; i < count; i++) {
            current = current.add(step);
            if (healTrail) {
                level.sendParticles(ParticleTypes.END_ROD, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
                if (i % 3 == 0) {
                    level.sendParticles(ParticleTypes.WITCH, current.x, current.y, current.z, 1, 0.05, 0.05, 0.05, 0.0);
                }
            } else {
                level.sendParticles(ParticleTypes.END_ROD, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
                if (i % 2 == 0) {
                    level.sendParticles(ParticleTypes.WITCH, current.x, current.y, current.z, 1, 0.03, 0.03, 0.03, 0.0);
                }
            }
        }
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {return Ingredient.of(BlockInit.BlockItems.WAW_PE_BOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.HATRED_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}