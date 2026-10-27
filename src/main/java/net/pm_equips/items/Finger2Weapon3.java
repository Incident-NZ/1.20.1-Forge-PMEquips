package net.pm_equips.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.pm_equips.util.FingerEquipment;

public class Finger2Weapon3 extends SwordItem {
    public Finger2Weapon3() {
        super(FingerEquipment.swordTier(2000), new Properties().attributes(FingerEquipment.meleeAttributes(12, .8)));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand); return InteractionResultHolder.consume(stack);
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BLOCK; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
}
