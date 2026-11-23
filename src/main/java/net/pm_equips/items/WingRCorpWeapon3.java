package net.pm_equips.items;

import net.minecraft.world.item.TooltipFlag;

import net.minecraft.network.chat.Component;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import java.util.function.Predicate;
import java.util.UUID;
import net.pm_equips.util.FingerEquipment;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;
import net.pm_equips.network.NetworkPacketInit;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.entity.projectile.Projectile;
import net.pm_equips.util.ItemStackData;

public class WingRCorpWeapon3 extends ProjectileWeaponItem {
    private static final String ACTIVE = "rcorp_laser_active", ELAPSED = "rcorp_laser_elapsed";
    private static final String RELEASED = "rcorp_laser_released", LAST = "rcorp_laser_last_tick";
    private static final UUID SESSION = UUID.randomUUID();
    public WingRCorpWeapon3() {
        super(new Properties().durability(3000).attributes(FingerEquipment.meleeAttributes(8, 1.2)));
    }
    @Override protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index,
            float velocity, float inaccuracy, float angle, LivingEntity target) {
        // This weapon resolves its particle shot in use/onUseTick; no projectile entity is spawned.
    }
    @Override public Predicate<ItemStack> getAllSupportedProjectiles() { return stack -> false; }
    @Override public int getDefaultProjectileRange() { return 64; }
    @Override public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(ItemInit.RCORP_BATTERY.get()) || super.isValidRepairItem(stack, ingredient);
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }
    public static boolean isFiring(ItemStack stack) { return ItemStackData.getInt(stack, ACTIVE) == 1; }
    private static ItemStack armor(Player player) { return player.getItemBySlot(EquipmentSlot.LEGS); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this) || !(armor(player).getItem() instanceof WingRCorpArmor2)
                || ClassEnergySupport.energy(armor(player)).map(energy -> energy.getEnergyStored() < 2000).orElse(true))
            return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && !isFiring(stack)) {
            ClassEnergySupport.energy(armor(player)).ifPresent(energy -> energy.extractEnergy(200, false));
            ItemStackData.update(stack, tag -> {
                tag.putInt(ACTIVE, 1); tag.putInt(ELAPSED, 0); tag.putBoolean(RELEASED, false); tag.remove(LAST);
                tag.putUUID("rcorp_laser_session", SESSION); tag.putUUID("rcorp_laser_owner", player.getUUID());
            });
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!level.isClientSide && entity instanceof Player player) tickLaser(player, stack);
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player && isFiring(stack)) tickLaser(player, stack);
    }
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
        if (!level.isClientSide && entity instanceof Player player && isFiring(stack)) {
            ItemStackData.update(stack, tag -> tag.putBoolean(RELEASED, true));
            if (ItemStackData.getInt(stack, ELAPSED) >= 60) stop(player, stack, false);
        }
    }
    public static void tickLaser(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level) || !isFiring(stack)) return;
        boolean held = player.getMainHandItem() == stack || player.getOffhandItem() == stack;
        if (!held || !player.isAlive() || player.isSpectator() || !(armor(player).getItem() instanceof WingRCorpArmor2)
                || ClassEnergySupport.energy(armor(player)).map(energy -> energy.getEnergyStored() <= 0).orElse(true)) {
            stop(player, stack, false); return;
        }
        var data = ItemStackData.copy(stack);
        long now = level.getGameTime();
        if (!data.hasUUID("rcorp_laser_session") || !SESSION.equals(data.getUUID("rcorp_laser_session"))
                || !data.hasUUID("rcorp_laser_owner") || !player.getUUID().equals(data.getUUID("rcorp_laser_owner"))
                || data.contains(LAST) && now - data.getLong(LAST) > 1) { stop(player, stack, false); return; }
        if (data.contains(LAST) && data.getLong(LAST) == now) return;
        int elapsed = data.getInt(ELAPSED);
        if (elapsed >= 60 && data.getBoolean(RELEASED)) { stop(player, stack, false); return; }
        if (elapsed > 0 && elapsed % 60 == 0
                && ClassEnergySupport.energy(armor(player)).map(energy -> energy.extractEnergy(200, true) != 200).orElse(true)) {
            stop(player, stack, false); return;
        }
        if (elapsed > 0 && elapsed % 60 == 0)
            ClassEnergySupport.energy(armor(player)).ifPresent(energy -> energy.extractEnergy(200, false));
        HitResult hit = WingTCorpWeapon3.trace(player, 64);
        if (elapsed % 5 == 0) {
            Vec3 start = player.getEyePosition();
            PacketDistributor.sendToPlayersNear(level, null, start.x, start.y, start.z, 128,
                    new NetworkPacketInit.FingerShotTrail(start, hit.getLocation(), 16));
        }
        if (elapsed % 20 == 0) {
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target) {
                target.hurtTime = 0; target.invulnerableTime = 0;
                target.hurt(player.damageSources().playerAttack(player), 23);
            }
            InteractionHand hand = player.getMainHandItem() == stack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            if (stack.isEmpty()) { stop(player, stack, false); return; }
        }
        data = ItemStackData.copy(stack);
        data.putInt(ELAPSED, elapsed + 1); data.putLong(LAST, now); ItemStackData.set(stack, data);
        if (elapsed + 1 >= 300) stop(player, stack, true);
        else if (elapsed + 1 >= 60 && data.getBoolean(RELEASED)) stop(player, stack, false);
    }
    private static void stop(Player player, ItemStack stack, boolean overheated) {
        if (!stack.isEmpty()) ItemStackData.update(stack, tag -> {
            tag.remove(ACTIVE); tag.remove(ELAPSED); tag.remove(RELEASED); tag.remove(LAST);
            tag.remove("rcorp_laser_session"); tag.remove("rcorp_laser_owner");
        });
        player.getCooldowns().addCooldown(ItemInit.RCORP_WEAPON_3.get(), overheated ? 200 : 100);
        if (player.getUseItem() == stack) player.stopUsingItem();
        if (overheated) player.hurt(player.damageSources().magic(), 4);
    }
    public static void cancelLaser(Player player, ItemStack stack) { if (isFiring(stack)) stop(player, stack, false); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.rcorp_laser"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
