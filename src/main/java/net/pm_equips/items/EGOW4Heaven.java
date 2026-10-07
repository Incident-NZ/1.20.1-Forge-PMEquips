package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.pm_equips.BlockInit;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.entity.EGOHeavenP;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW4Heaven extends SwordItem {
    private static final int MIN_THROW_CHARGE_TICKS = 10;
    private static final UUID REACH_UUID = UUID.randomUUID();
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "blue_scar_reach", -1.0, AttributeModifier.Operation.ADDITION);

    public EGOW4Heaven() {
        super(new CustomTier(), 15, -2.2F, new Properties().durability(3000));
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;

        AttributeInstance reachAttr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)
                || getUseDuration(stack) - timeLeft < MIN_THROW_CHARGE_TICKS
                || level.isClientSide) {
            return;
        }

        EGOHeavenP projectile = new EGOHeavenP(level, player, player.getLookAngle());
        level.addFreshEntity(projectile);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(this));

        stack.shrink(1);
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.WAW_PE_BOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.HEAVEN_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
