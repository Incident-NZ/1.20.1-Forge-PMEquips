package net.pm_equips.items;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.network.ForgeFeaturePackets;
import net.pm_equips.util.FingerEquipment;

public class WingTCorpWeapon3 extends ProjectileWeaponItem {
    public WingTCorpWeapon3() { super(new Properties().durability(1000)); }
    @Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? FingerEquipment.meleeAttributes(16, 4) : super.getAttributeModifiers(slot, stack);
    }
    @Override public Predicate<ItemStack> getAllSupportedProjectiles() { return stack -> false; }
    @Override public int getDefaultProjectileRange() { return 64; }
    @Override public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(ItemInit.TCORP_CLOCK.get()) || super.isValidRepairItem(stack, ingredient);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!WingTCorpWeapon2.canUseAlongsideOtherHand(player, hand) || player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel server) {
            HitResult hit = trace(player, 64);
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target
                    && target.hurt(player.damageSources().playerAttack(player), 16))
                target.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 100, 1), player);
            Vec3 start = player.getEyePosition();
            ForgeFeaturePackets.sendNear(server, start.lerp(hit.getLocation(), .5), 128,
                    new ForgeFeaturePackets.FingerShotTrail(start, hit.getLocation(), 8));
            stack.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(hand));
            player.getCooldowns().addCooldown(this, 60);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    public static HitResult trace(LivingEntity shooter, double range) {
        Vec3 start = shooter.getEyePosition(), end = start.add(shooter.getLookAngle().scale(range));
        BlockHitResult block = shooter.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, shooter));
        Vec3 limit = block.getType() == HitResult.Type.MISS ? end : block.getLocation();
        EntityHitResult target = ProjectileUtil.getEntityHitResult(shooter.level(), shooter, start, limit,
                shooter.getBoundingBox().expandTowards(limit.subtract(start)).inflate(1),
                entity -> entity instanceof LivingEntity && entity.isAlive() && !entity.isSpectator()
                        && entity.isPickable() && !entity.isPassengerOfSameVehicle(shooter));
        return target == null ? block : target;
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.tcorp_exclusive"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
