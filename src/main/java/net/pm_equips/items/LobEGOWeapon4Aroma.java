package net.pm_equips.items;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.util.ItemStackData;

public class LobEGOWeapon4Aroma extends ProjectileWeaponItem {

    private static final String LOADED_ARROW_TAG = "LoadedArrow";
    private static final String RELOAD_TAG = "Reload";
    private static final int RELOAD_TICKS = 20;

    public LobEGOWeapon4Aroma(Properties properties) {
        super(properties);
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
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return LobEGOWeapon4Aroma::isSupportedArrow;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 128;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack gun = player.getItemInHand(hand);
        CompoundTag tag = ItemStackData.copy(gun);

        if (tag.getInt(RELOAD_TAG) > 0) {
            return InteractionResultHolder.fail(gun);
        }

        ItemStack loadedArrow = getLoadedArrow(gun, level.registryAccess());
        if (loadedArrow.isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("矢を装填してください / Reload an arrow"),
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

        if (!level.isClientSide) {
            shootArrow(level, player, loadedArrow, gun);
            tag.remove(LOADED_ARROW_TAG);
            ItemStackData.set(gun, tag);

            gun.hurtAndBreak(
                    1,
                    player,
                    (hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
            );
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.sidedSuccess(gun, level.isClientSide);
    }

    private void shootArrow(
            Level level,
            Player player,
            ItemStack arrowStack,
            ItemStack weaponStack
    ) {
        ArrowItem arrowItem = (ArrowItem) arrowStack.getItem();
        AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, weaponStack);
        arrow.setBaseDamage(10.0D + level.random.nextInt(11));
        arrow.shootFromRotation(
                player,
                player.getXRot(),
                player.getYRot(),
                0.0F,
                4.0F,
                1.0F
        );

        level.addFreshEntity(arrow);
        level.playSound(
                null,
                player,
                SoundEvents.CROSSBOW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }

    public void startReload(
            ItemStack stack,
            Player player
    ) {
        CompoundTag tag = ItemStackData.copy(stack);

        if (tag.getInt(RELOAD_TAG) > 0 || !getLoadedArrow(stack, player.registryAccess()).isEmpty()) {
            return;
        }

        if (findSupportedArrow(player).isEmpty()) {
            return;
        }

        tag.putInt(RELOAD_TAG, RELOAD_TICKS);
        ItemStackData.set(stack, tag);
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            Level level,
            Entity entity,
            int slot,
            boolean selected
    ) {
        if (!level.isClientSide && entity instanceof Player player) {
            CompoundTag tag = ItemStackData.copy(stack);
            int reload = tag.getInt(RELOAD_TAG);

            if (reload > 0) {
                reload--;
                tag.putInt(RELOAD_TAG, reload);
                ItemStackData.set(stack, tag);

                if (reload == 0 && getLoadedArrow(stack, level.registryAccess()).isEmpty()) {
                    ItemStack inventoryArrow = findSupportedArrow(player);
                    if (!inventoryArrow.isEmpty()) {
                        ItemStack loadedArrow = inventoryArrow.copy();
                        loadedArrow.setCount(1);
                        tag.put(LOADED_ARROW_TAG, loadedArrow.save(level.registryAccess()));
                        ItemStackData.set(stack, tag);

                        if (!player.getAbilities().instabuild) {
                            inventoryArrow.shrink(1);
                        }
                    }
                }
            }
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    private static ItemStack findSupportedArrow(Player player) {
        for (ItemStack inventoryStack : player.getInventory().items) {
            if (isSupportedArrow(inventoryStack)) {
                return inventoryStack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getLoadedArrow(ItemStack stack, HolderLookup.Provider registries) {
        if (registries == null) return ItemStack.EMPTY;
        CompoundTag tag = ItemStackData.copy(stack);
        if (tag == null || !tag.contains(LOADED_ARROW_TAG, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        ItemStack arrow = ItemStack.parseOptional(registries, tag.getCompound(LOADED_ARROW_TAG));
        return isSupportedArrow(arrow) ? arrow : ItemStack.EMPTY;
    }

    private static boolean isSupportedArrow(ItemStack stack) {
        return stack.getItem() instanceof ArrowItem
                && !(stack.getItem() instanceof TippedArrowItem);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Ammo: " + (getLoadedArrow(stack, level.registries()).isEmpty() ? 0 : 1) + " / 1"));
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.AROMA_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index,
                                   float velocity, float inaccuracy, float angle, LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), angle, velocity, inaccuracy);
    }
}
