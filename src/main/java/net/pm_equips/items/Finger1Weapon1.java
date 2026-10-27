package net.pm_equips.items;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.util.FingerEquipment;

public class Finger1Weapon1 extends ProjectileWeaponItem {
    public Finger1Weapon1() {
        super(new Properties().durability(2000));
    }

    @Override public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot,ItemStack stack) {
        return slot==EquipmentSlot.MAINHAND ? FingerEquipment.meleeAttributes(3,1.2) : super.getAttributeModifiers(slot,stack);
    }
    @Override public Predicate<ItemStack> getAllSupportedProjectiles() { return FingerEquipment::supportsAmmo; }
    @Override public int getDefaultProjectileRange() { return FingerEquipment.RANGE; }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return FingerEquipment.shootRifle(player, hand, false)
                ? InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide)
                : InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        stack.hurtAndBreak(1,attacker,entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }


    @Override public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("tooltip.pm_equips.thumb_controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.thumb_ammo", FingerEquipment.ammoItem(FingerEquipment.ammoMode(stack)).getDescription()));
        super.appendHoverText(stack, context, tooltip, flags);
    }
}
