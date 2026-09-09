package net.pm_equips.items;

import net.minecraft.world.item.Item;


import net.pm_equips.util.ItemStackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.PMERegistryBlock;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMERegistrySound;
import net.pm_equips.entity.AmmoGun;

import java.util.List;
import java.util.function.Predicate;

public class LCorpEGOR5Pink extends net.minecraft.world.item.ProjectileWeaponItem {
    private static final int MAX_AMMO = 10;
    private static final int RELOAD_TICKS = 30; // 1.5秒
    private static final float VELOCITY = 4.0f;
    private static final int COOLDOWN_TICKS = 20; // 0.5秒
    public LCorpEGOR5Pink(Properties properties) {
        super(properties.durability(4000));
    }
    @Override
    protected void shootProjectile(net.minecraft.world.entity.LivingEntity shooter,
                                   net.minecraft.world.entity.projectile.Projectile projectile,
                                   int index, float velocity, float inaccuracy, float angle,
                                   net.minecraft.world.entity.LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), angle, velocity, inaccuracy);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack) -> stack.getItem() == PMERegistryItem.RIFLE_BULLET_AMMO.get();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 128;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);

        int reload = ItemStackData.getInt(gun, "Reload");
        int ammo = ItemStackData.getInt(gun, "Ammo");

        if (reload > 0) return InteractionResultHolder.fail(gun);

        if (ammo <= 0) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("弾薬切れ / No Ammo"), true);
                level.playSound(null, player, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return InteractionResultHolder.fail(gun);
        }

        if (level.isClientSide) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    PMERegistrySound.GUN_SEMI.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            return InteractionResultHolder.success(gun);
        }

        shootBullet(level, player);

        ItemStackData.putInt(gun, "Ammo", ammo - 1);

        gun.hurtAndBreak(1, player, hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.consume(gun);
    }

'    private void shootBullet(Level level, Player player) {
        float damage = 20.0F + level.random.nextInt(5);
        net.pm_equips.util.ParticleBullets.fire(player, getDefaultProjectileRange(), damage, net.minecraft.core.particles.ParticleTypes.CRIT, false, true);
    }'

    // Rキーリロード開始
    public void startReload(ItemStack stack, Player player) {
        int reload = ItemStackData.getInt(stack, "Reload");
        if (reload > 0) return;

        int ammo = ItemStackData.getInt(stack, "Ammo");
        if (ammo >= MAX_AMMO) return;

        boolean hasAmmo = false;
        for (ItemStack invStack : player.getInventory().items) {
            if (invStack.is(PMERegistryItem.RIFLE_BULLET_AMMO.get())) {
                hasAmmo = true; break;
            }
        }
        if (!hasAmmo) return;

        ItemStackData.putInt(stack, "Reload", RELOAD_TICKS);
        player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (!ItemStackData.contains(stack, "Ammo")) {
            ItemStackData.putInt(stack, "Ammo", MAX_AMMO);
        }

        int reload = ItemStackData.getInt(stack, "Reload");
        if (reload > 0) {
            reload--;
            ItemStackData.putInt(stack, "Reload", reload);

            if (reload <= 0 && entity instanceof Player player) {
                int ammo = ItemStackData.getInt(stack, "Ammo");
                int needed = MAX_AMMO - ammo;
                if (needed <= 0) return;

                int loaded = 0;
                for (ItemStack invStack : player.getInventory().items) {
                    if (invStack.is(PMERegistryItem.RIFLE_BULLET_AMMO.get())) {
                        while (!invStack.isEmpty() && loaded < needed) { invStack.shrink(1); loaded++; }
                    }
                    if (loaded >= needed) break;
                }

                ItemStackData.putInt(stack, "Ammo", ammo + loaded);
            }
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }

        target.hurt(attacker.level().damageSources().generic(), 7.0f);

        stack.hurtAndBreak(1, attacker, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(PMERegistryBlock.BlockItems.ZAYIN_PE_BOX.get());
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

