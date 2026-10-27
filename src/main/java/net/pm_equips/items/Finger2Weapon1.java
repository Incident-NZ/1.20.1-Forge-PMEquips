package net.pm_equips.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.pm_equips.MobEffectInit;
import net.pm_equips.util.FingerEquipment;

public class Finger2Weapon1 extends SwordItem {
    public static final int CHARGE_TICKS = 40;
    private static final String COOLDOWN = "PMEProselyteDrawUntil";
    public Finger2Weapon1() {
        super(FingerEquipment.swordTier(1000), new Properties().attributes(FingerEquipment.meleeAttributes(4, 1.5)));
    }

    public static boolean drawn(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT).value() == 1;
    }

    private static void setDrawn(ItemStack stack, boolean drawn) {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(drawn ? 1 : 0));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, FingerEquipment.meleeAttributes(drawn ? 8 : 4, 1.5));
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand); return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.NONE; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }

    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        if (level.isClientSide || !(user instanceof Player player)) return;
        boolean charged = !drawn(stack) && getUseDuration(stack, user) - timeLeft >= CHARGE_TICKS
                && level.getGameTime() >= player.getPersistentData().getLong(COOLDOWN);
        if (charged) {
            player.addEffect(new MobEffectInstance(MobEffectInit.POWER, 100, 3));
            player.getPersistentData().putLong(COOLDOWN, level.getGameTime() + 100);
        }
        setDrawn(stack, !drawn(stack));
    }

    @Override public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (drawn(stack)) super.postHurtEnemy(stack, target, attacker);
    }
    @Override public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity user) {
        return !drawn(stack) || super.mineBlock(stack, level, state, pos, user);
    }
}
