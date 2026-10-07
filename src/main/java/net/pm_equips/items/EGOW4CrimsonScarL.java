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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.BlockInit;
import net.pm_equips.ItemInit;
import net.pm_equips.SoundInit;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class EGOW4CrimsonScarL extends ProjectileWeaponItem {
    private static final float DAMAGE = 13.0F;
    private static final int RANGE = 16;
    private static final int COOLDOWN_TICKS = 40;

    public EGOW4CrimsonScarL(Properties properties) {
        super(properties.durability(3000));
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ItemInit.PISTOL_BULLET_AMMO.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return RANGE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);

        // 弾薬チェック
        if (!hasAmmo(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("弾薬切れ / No Ammo"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return InteractionResultHolder.fail(gun);
        }

        // クライアント側は音だけ（実際の処理はサーバー）
        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundInit.GUN_SEMI.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.consume(gun);
        }

        // ===== サーバー側処理 =====
        shootHitscan(level, player);
        consumeAmmo(player);

        gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.consume(gun);
    }

    private void shootHitscan(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 endPos = eyePos.add(look.scale(RANGE));

        // エンティティヒット判定
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
            // エンティティに命中
            hitPos = entityHit.getLocation();
            if (entityHit.getEntity() instanceof LivingEntity target) {
                target.hurt(level.damageSources().playerAttack(player), DAMAGE);
                level.playSound(null, target.blockPosition(), SoundEvents.GENERIC_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        } else {
            // ブロックヒット判定（ブロックに当たった場合の位置を取得）
            HitResult blockHit = level.clip(new net.minecraft.world.level.ClipContext(
                    eyePos, endPos,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    player
            ));
            hitPos = blockHit.getLocation();
        }

        // パーティクルの軌跡を描画
        spawnBulletTrail((ServerLevel) level, eyePos, hitPos);
        // 着弾パーティクル
        spawnImpactParticles((ServerLevel) level, hitPos);
    }

    /** 弾の軌跡パーティクルを生成 */
    private void spawnBulletTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();
        Vec3 step = direction.normalize().scale(0.4); // 0.4ブロックごとに1個

        Vec3 current = start;
        int count = (int) (distance / 0.4);

        for (int i = 0; i < count; i++) {
            current = current.add(step);

            // 火花風のパーティクル
            level.sendParticles(
                    ParticleTypes.CRIT,
                    current.x, current.y, current.z,
                    1,          // 個数
                    0.02, 0.02, 0.02, // 拡散
                    0.0         // 速度
            );

            // 少し煙も混ぜる（任意）
            if (i % 3 == 0) {
                level.sendParticles(
                        ParticleTypes.SMOKE,
                        current.x, current.y, current.z,
                        1,
                        0.01, 0.01, 0.01,
                        0.0
                );
            }
        }
    }

    /** 着弾時のパーティクル */
    private void spawnImpactParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(
                ParticleTypes.CRIT,
                pos.x, pos.y, pos.z,
                12,
                0.25, 0.25, 0.25,
                0.15
        );
        level.sendParticles(
                ParticleTypes.SMOKE,
                pos.x, pos.y, pos.z,
                6,
                0.2, 0.2, 0.2,
                0.05
        );
    }

    private boolean hasAmmo(Player player) {
        return player.getInventory().contains(new ItemStack(ItemInit.PISTOL_BULLET_AMMO.get()));
    }

    private void consumeAmmo(Player player) {
        if (!player.getAbilities().instabuild) {
            player.getInventory().clearOrCountMatchingItems(
                    stack -> stack.is(ItemInit.PISTOL_BULLET_AMMO.get()),
                    1,
                    player.inventoryMenu.getCraftSlots()
            );
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.hurt(attacker.level().damageSources().generic(), DAMAGE);
        stack.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(BlockInit.BlockItems.WAW_PE_BOX.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.CRIMSON_SCAR_GUN_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
