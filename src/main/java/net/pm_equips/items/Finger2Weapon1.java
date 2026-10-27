package net.pm_equips.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.pm_equips.MobEffectInit;
import net.pm_equips.util.FingerEquipment;
import net.pm_equips.util.ItemStackData;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class Finger2Weapon1 extends SwordItem {
    public static final int CHARGE_TICKS = 40;
    private static final String COOLDOWN = "PMEProselyteDrawUntil";
    public Finger2Weapon1() {
        super(FingerEquipment.swordTier(1000), 1, -2.5F, new Properties());
    }

    public static boolean drawn(ItemStack stack) {
        return ItemStackData.getInt(stack,"CustomModelData")==1;
    }

    private static void setDrawn(ItemStack stack, boolean drawn) {
        ItemStackData.putInt(stack,"CustomModelData",drawn?1:0);
    }

    @Override public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot,ItemStack stack) {
        return slot==EquipmentSlot.MAINHAND ? FingerEquipment.meleeAttributes(drawn(stack)?8:4,1.5) : super.getAttributeModifiers(slot,stack);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand); return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.NONE; }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }

    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        if (level.isClientSide || !(user instanceof Player player)) return;
        boolean charged = !drawn(stack) && getUseDuration(stack) - timeLeft >= CHARGE_TICKS
                && level.getGameTime() >= player.getPersistentData().getLong(COOLDOWN);
        if (charged) {
            player.addEffect(new MobEffectInstance(MobEffectInit.POWER.get(), 100, 3));
            player.getPersistentData().putLong(COOLDOWN, level.getGameTime() + 100);
        }
        setDrawn(stack, !drawn(stack));
    }

    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return !drawn(stack) || super.hurtEnemy(stack,target,attacker);
    }
    @Override public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity user) {
        return !drawn(stack) || super.mineBlock(stack, level, state, pos, user);
    }
}
