package net.pm_equips.items;

import net.minecraft.world.item.Item;

import net.pm_equips.util.ItemStackData;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.PMERegistryEntity;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMERegistrySound;
import net.pm_equips.entity.EGOMagicP;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stats;

public class LCorpEGOR4MagicBullet extends net.minecraft.world.item.ProjectileWeaponItem {

    private static final int MAX_AMMO = 7;
    private static final int RELOAD_TICKS = 100;
    private static final int COOLDOWN_TICKS = 40;

    public LCorpEGOR4MagicBullet(Properties properties) {
        super(properties);
    }
    @Override
    protected void shootProjectile(net.minecraft.world.entity.LivingEntity shooter,
                                   net.minecraft.world.entity.projectile.Projectile projectile,
                                   int index, float velocity, float inaccuracy, float angle,
                                   net.minecraft.world.entity.LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), angle, velocity, inaccuracy);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack gun = player.getItemInHand(hand);

        // セミオート：クールダウン中は発射できない
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(gun);
        }

        CompoundTag tag = ItemStackData.copy(gun);

        int ammo = tag.getInt("Ammo");
        int reload = tag.getInt("Reload");

        // リロード中
        if (reload > 0) {
            return InteractionResultHolder.fail(gun);
        }

        // 残弾なし
        if (ammo <= 0) {

            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.DISPENSER_FAIL,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );

            return InteractionResultHolder.fail(gun);
        }

        // 発射
        if (!level.isClientSide) {

            EGOMagicP bullet = new EGOMagicP(
                    PMERegistryEntity.MAGIC_BULLET.get(),
                    level,
                    player
            );

            // ダメージを銃側で決めて渡す（22..26）
            float damage = 22 + level.random.nextInt(5);
            bullet.setDamage(damage);
            // 弾速を速く（32.0F）に設定
            bullet.setSpeed(32.0D);

            level.addFreshEntity(bullet);

            tag.putInt("Ammo", ammo - 1);

            // サーバ側でクールダウン、統計、耐久を処理
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            gun.hurtAndBreak(1, player, hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        }

        level.playSound(
                null,
                player.blockPosition(),
                PMERegistrySound.EGO_MAGIC_BULLET.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        return InteractionResultHolder.success(gun);
    }

    // =========================
    // Rキーリロード開始
    // =========================

    public void startReload(ItemStack stack, Player player) {

        CompoundTag tag = ItemStackData.copy(stack);

        int reload = tag.getInt("Reload");

        if (reload > 0) {
            return;
        }

        int ammo = tag.getInt("Ammo");

        // フル装填なら不要
        if (ammo >= MAX_AMMO) {
            return;
        }

        // 弾薬確認
        boolean hasAmmo = false;

        for (ItemStack invStack : player.getInventory().items) {

            if (invStack.is(PMERegistryItem.MAGIC_BULLET_AMMO.get())) {

                hasAmmo = true;
                break;
            }
        }

        if (!hasAmmo) {
            return;
        }

        tag.putInt("Reload", RELOAD_TICKS);
        ItemStackData.set(stack, tag);

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.IRON_TRAPDOOR_CLOSE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {

        CompoundTag tag = ItemStackData.copy(stack);

        if (!tag.contains("Ammo")) {
            tag.putInt("Ammo", MAX_AMMO);
            ItemStackData.set(stack, tag);
        }

        int reload = tag.getInt("Reload");

        if (reload > 0) {

            reload--;

            tag.putInt("Reload", reload);
            ItemStackData.set(stack, tag);

            // リロード完了
            if (reload <= 0 && entity instanceof Player player) {

                int ammo = tag.getInt("Ammo");

                int needed = MAX_AMMO - ammo;

                if (needed <= 0) {
                    return;
                }

                int loaded = 0;

                for (ItemStack invStack : player.getInventory().items) {

                    if (invStack.is(PMERegistryItem.MAGIC_BULLET_AMMO.get())) {

                        while (!invStack.isEmpty() && loaded < needed) {

                            invStack.shrink(1);

                            loaded++;
                        }
                    }

                    if (loaded >= needed) {
                        break;
                    }
                }

                tag.putInt("Ammo", ammo + loaded);
                ItemStackData.set(stack, tag);
            }
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(PMERegistryItem.MAGIC_BULLET_AMMO.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return 128;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        CompoundTag tag = ItemStackData.copy(stack);
        tooltip.add(
                Component.literal(
                        "Ammo: "
                                + tag.getInt("Ammo")
                                + " / "
                                + MAX_AMMO
                )
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}

