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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.PMERegistryBlock;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.entity.AmmoExp;

import java.util.List;
import java.util.function.Predicate;

public class LCorpEGOR2Match extends net.minecraft.world.item.ProjectileWeaponItem {

    private static final int MAX_AMMO = 1;
    private static final int RELOAD_TICKS = 100;
    private static final float MIN_DAMAGE = 20.0F;
    private static final float VELOCITY = 2.0F;
    private static final int RANGE = 128;
    private static final int COOLDOWN_TICKS = 20;

    public LCorpEGOR2Match(
            Properties properties
    ) {
        super(properties.durability(1000));
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
        return stack -> stack.is(PMERegistryItem.EXPLOSIVE_BULLET_AMMO.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return RANGE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {

        ItemStack gun =
                player.getItemInHand(hand);

        CompoundTag tag = ItemStackData.copy(gun);
        int reload =
                tag.getInt("Reload");

        int ammo =
                tag.getInt("Ammo");

        if (reload > 0) {
            return InteractionResultHolder.fail(gun);
        }

        if (ammo <= 0) {

            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("弾薬切れ / No Ammo"),
                        true
                );

                level.playSound(
                        null,
                        player,
                        SoundEvents.DISPENSER_FAIL,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.2F
                );
            }

            return InteractionResultHolder.fail(gun);
        }

        if (level.isClientSide) {
            level.playSound(
                    player,
                    player.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.PLAYERS,
                    1.2F,
                    0.8F
            );

            return InteractionResultHolder.consume(gun);
        }

        shootBullet(
                level,
                player
        );

        tag.putInt(
                "Ammo",
                ammo - 1
        );
        ItemStackData.set(gun, tag);

        gun.hurtAndBreak(1, player, hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);

        player.awardStat(
                Stats.ITEM_USED.get(this)
        );

        player.getCooldowns()
                .addCooldown(
                        this,
                        COOLDOWN_TICKS
                );

        return InteractionResultHolder.consume(gun);
    }

'    private void shootBullet(Level level, Player player) {
        float damage = MIN_DAMAGE + level.random.nextInt(11);
        net.pm_equips.util.ParticleBullets.fireExplosion(player, getDefaultProjectileRange(), damage, 5.0D, net.minecraft.core.particles.ParticleTypes.FLAME, true);
    }'

    // Rキーリロード開始
    public void startReload(
            ItemStack stack,
            Player player
    ) {

        CompoundTag tag = ItemStackData.copy(stack);
        int reload =
                tag.getInt("Reload");

        if (reload > 0) {
            return;
        }

        int ammo =
                tag.getInt("Ammo");

        if (ammo >= MAX_AMMO) {
            return;
        }

        boolean hasAmmo =
                false;

        for (ItemStack invStack : player.getInventory().items) {

            if (invStack.is(PMERegistryItem.EXPLOSIVE_BULLET_AMMO.get())) {
                hasAmmo = true;
                break;
            }
        }

        if (!hasAmmo) {
            return;
        }

        tag.putInt(
                "Reload",
                RELOAD_TICKS
        );
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
    public void inventoryTick(
            ItemStack stack,
            Level level,
            net.minecraft.world.entity.Entity entity,
            int slot,
            boolean selected
    ) {

        CompoundTag tag = ItemStackData.copy(stack);
        if (!tag.contains("Ammo")) {
            tag.putInt(
                    "Ammo",
                    MAX_AMMO
            );
            ItemStackData.set(stack, tag);
        }

        int reload =
                tag.getInt("Reload");

        if (reload > 0) {

            reload--;

            tag.putInt(
                    "Reload",
                    reload
            );
            ItemStackData.set(stack, tag);

            if (reload <= 0
                    && entity instanceof Player player) {

                int ammo =
                        tag.getInt("Ammo");

                int needed =
                        MAX_AMMO - ammo;

                if (needed <= 0) {
                    return;
                }

                int loaded =
                        0;

                for (ItemStack invStack : player.getInventory().items) {

                    if (invStack.is(PMERegistryItem.EXPLOSIVE_BULLET_AMMO.get())) {

                        while (!invStack.isEmpty()
                                && loaded < needed) {

                            invStack.shrink(1);
                            loaded++;
                        }
                    }

                    if (loaded >= needed) {
                        break;
                    }
                }

                tag.putInt(
                        "Ammo",
                        ammo + loaded
                );
                ItemStackData.set(stack, tag);
            }
        }

        super.inventoryTick(
                stack,
                level,
                entity,
                slot,
                selected
        );
    }

    @Override
    public boolean hurtEnemy(
            ItemStack stack,
            LivingEntity target,
            LivingEntity attacker
    ) {

        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }

        target.hurt(
                attacker.level().damageSources().explosion(null),
                MIN_DAMAGE
        );

        stack.hurtAndBreak(1, attacker, net.minecraft.world.entity.EquipmentSlot.MAINHAND);

        return true;
    }

    @Override
    public boolean isValidRepairItem(
            ItemStack stack,
            ItemStack repair
    ) {
        return repair.is(PMERegistryBlock.BlockItems.TETH_PE_BOX.get());
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


