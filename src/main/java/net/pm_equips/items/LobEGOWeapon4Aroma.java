package net.pm_equips.items;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SpectralArrowItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.network.ForgeFeaturePackets;
import net.pm_equips.util.ItemStackData;

public class LobEGOWeapon4Aroma extends ProjectileWeaponItem {
    private static final int RANGE = 128, COOLDOWN_TICKS = 20;

    public LobEGOWeapon4Aroma(Properties properties) { super(properties); }
    @Override public Predicate<ItemStack> getAllSupportedProjectiles() { return LobEGOWeapon4Aroma::isSupportedArrow; }
    @Override public int getDefaultProjectileRange() { return RANGE; }
    private static boolean isSupportedArrow(ItemStack stack) {
        return stack.getItem() instanceof ArrowItem && !(stack.getItem() instanceof TippedArrowItem);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        if (!player.isAlive() || player.isSpectator() || player.isSleeping() || player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(gun);
        if (!(level instanceof ServerLevel server)) return InteractionResultHolder.consume(gun);
        refundLegacyArrow(player, gun);
        ItemStack ammo = player.getProjectile(gun);
        if (!isSupportedArrow(ammo)) ammo = ItemStack.EMPTY;
        if (ammo.isEmpty() && !player.getAbilities().instabuild) {
            level.playSound(null, player, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1, 1.2F);
            return InteractionResultHolder.fail(gun);
        }
        boolean spectral = ammo.getItem() instanceof SpectralArrowItem;
        Vec3 start = player.getEyePosition();
        HitResult hit = WingTCorpWeapon3.trace(player, RANGE);
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target) {
            DamageSource damage = new DamageSource(server.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.ARROW), null, player, start);
            if (target.hurt(damage, 10 + server.random.nextInt(11)) && spectral)
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0), player);
        }
        Vec3 middle = start.lerp(hit.getLocation(), .5);
        ForgeFeaturePackets.sendNear(server, middle, start.distanceTo(hit.getLocation()) / 2 + 64,
                new ForgeFeaturePackets.FingerShotTrail(start, hit.getLocation(), spectral ? 8 : 1));
        if (!player.getAbilities().instabuild) ammo.shrink(1);
        gun.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(hand));
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        level.playSound(null, player, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1, 1);
        return InteractionResultHolder.consume(gun);
    }
    // Convert a previously loaded arrow back into ordinary ammunition, once, without retaining a magazine.
    private static void refundLegacyArrow(Player player, ItemStack gun) {
        if (!ItemStackData.contains(gun, "LoadedArrow") && !ItemStackData.contains(gun, "Reload")) return;
        CompoundTag data = ItemStackData.copy(gun);
        ItemStack arrow = data.contains("LoadedArrow", Tag.TAG_COMPOUND)
                ? ItemStack.of(data.getCompound("LoadedArrow")) : ItemStack.EMPTY;
        data.remove("LoadedArrow"); data.remove("Reload"); ItemStackData.set(gun, data);
        if (!player.getAbilities().instabuild && isSupportedArrow(arrow)) {
            arrow.setCount(1);
            if (!player.addItem(arrow)) player.drop(arrow, false);
        }
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.AROMA_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
