package net.pm_equips.items;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.pm_equips.MobEffectInit;
import net.pm_equips.util.FingerEquipment;

public class Finger1Weapon2 extends ProjectileWeaponItem {
    public Finger1Weapon2() {
        super(new Properties().durability(2000).attributes(FingerEquipment.meleeAttributes(8, 1.2)));
    }

    @Override public Predicate<ItemStack> getAllSupportedProjectiles() { return FingerEquipment::supportsAmmo; }
    @Override public int getDefaultProjectileRange() { return FingerEquipment.RANGE; }
    @Override protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity,
            float inaccuracy, float angle, LivingEntity target) { /* Hitscan shots never spawn projectile entities. */ }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return FingerEquipment.shootRifle(player, hand, false)
                ? InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide)
                : InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) target.addEffect(new MobEffectInstance(MobEffectInit.BLEED, 100, 1), attacker);
        return true;
    }

    @Override public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("tooltip.pm_equips.thumb_controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.thumb_ammo", FingerEquipment.ammoItem(FingerEquipment.ammoMode(stack)).getDescription()));
        super.appendHoverText(stack, context, tooltip, flags);
    }
}
