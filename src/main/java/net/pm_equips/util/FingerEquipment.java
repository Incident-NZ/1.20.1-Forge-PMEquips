package net.pm_equips.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import java.util.UUID;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.PacketDistributor;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.SoundInit;
import net.pm_equips.entity.MobNPC1;
import net.pm_equips.entity.MobNPC3;
import net.pm_equips.items.Finger1Weapon1;
import net.pm_equips.items.Finger1Weapon2;
import net.pm_equips.items.Finger2Armor2;
import net.pm_equips.items.Finger2Armor3;
import net.pm_equips.items.Finger2Weapon2;
import net.pm_equips.items.Finger2Weapon3;
import net.pm_equips.items.Finger2WeaponSP;
import net.pm_equips.network.ForgeFeaturePackets;
import net.pm_equips.network.ForgeFeaturePackets.FingerShotTrail;

/** Shared operations; equipment classes inherit Minecraft classes directly. */
@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public final class FingerEquipment {
    public static final int RIFLE_COOLDOWN = 40;
    public static final int RANGE = 128;
    private static final String AMMO = "ThumbAmmo";
    private static final ResourceKey<DamageType> PIERCING_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            new ResourceLocation(PMEquipsMain.MOD_ID, "thumb_armor_piercing"));
    private FingerEquipment() {}

    public static Tier swordTier(int durability) {
        return new Tier() {
            @Override public int getUses() { return durability; }
            @Override public float getSpeed() { return Tiers.IRON.getSpeed(); }
            @Override public float getAttackDamageBonus() { return Tiers.IRON.getAttackDamageBonus(); }
            @Override public int getLevel() { return Tiers.IRON.getLevel(); }
            @Override public int getEnchantmentValue() { return Tiers.IRON.getEnchantmentValue(); }
            @Override public Ingredient getRepairIngredient() { return Tiers.IRON.getRepairIngredient(); }
        };
    }

    public static Multimap<Attribute,AttributeModifier> meleeAttributes(double damage,double speed) {
        return ImmutableMultimap.of(Attributes.ATTACK_DAMAGE,new AttributeModifier(UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3"),"Weapon damage",damage-1,AttributeModifier.Operation.ADDITION),
                Attributes.ATTACK_SPEED,new AttributeModifier(UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca4"),"Weapon speed",speed-4,AttributeModifier.Operation.ADDITION));
    }
    public static void addModifier(AttributeInstance instance,AttributeModifier modifier) {
        instance.removeModifier(modifier.getId()); instance.addTransientModifier(modifier);
    }
    public static ItemStack weapon(LivingEntity entity) { return entity instanceof MobNPC1 npc ? npc.getWeaponItem() : entity.getMainHandItem(); }

    public static boolean isThumbRifle(ItemStack stack) {
        return stack.getItem() instanceof Finger1Weapon1 || stack.getItem() instanceof Finger1Weapon2;
    }

    public static boolean isPiercingDamage(DamageSource source) { return source.is(PIERCING_DAMAGE); }

    public static Item ammoItem(int mode) {
        return switch (mode) {
            case 1 -> ItemInit.AMMO_THUMBS_IF.get(); case 2 -> ItemInit.AMMO_THUMBS_FR.get();
            case 3 -> ItemInit.AMMO_THUMBS_AP.get(); case 4 -> ItemInit.AMMO_THUMBS_KB.get();
            default -> ItemInit.RIFLE_BULLET_AMMO.get();
        };
    }

    public static int ammoMode(ItemStack stack) { return Mth.clamp(ItemStackData.getInt(stack, AMMO), 0, 4); }
    public static boolean supportsAmmo(ItemStack stack) {
        for (int mode = 0; mode < 5; mode++) if (stack.is(ammoItem(mode))) return true;
        return false;
    }

    private static InteractionHand rifleHand(Player player) {
        return isThumbRifle(player.getMainHandItem()) ? InteractionHand.MAIN_HAND
                : isThumbRifle(player.getOffhandItem()) ? InteractionHand.OFF_HAND : null;
    }

    public static boolean cycleAmmo(Player player) {
        InteractionHand hand = rifleHand(player);
        if (hand == null) return false;
        if (!player.level().isClientSide) {
            ItemStack stack = player.getItemInHand(hand);
            int mode = (ammoMode(stack) + 1) % 5;
            ItemStackData.putInt(stack, AMMO, mode);
            player.displayClientMessage(Component.translatable("tooltip.pm_equips.thumb_ammo", ammoItem(mode).getDescription()), true);
        }
        return true;
    }

    private static ItemStack findAmmo(Player player, int mode) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && stack.is(ammoItem(mode))) return stack;
        }
        return ItemStack.EMPTY;
    }

    public static boolean shootRifle(Player player, InteractionHand hand, boolean combined) {
        ItemStack gun = player.getItemInHand(hand);
        if (!isThumbRifle(gun) || !player.isAlive() || player.isSpectator() || player.isSleeping()
                || player.getCooldowns().isOnCooldown(gun.getItem())) return false;
        if (!(player.level() instanceof ServerLevel level)) return true;
        List<ItemStack> consumed = new ArrayList<>();
        int mask = 0;
        if (combined) {
            for (int mode = 1; mode <= 4; mode++) {
                ItemStack ammo = findAmmo(player, mode);
                if (!ammo.isEmpty()) { mask |= 1 << mode; consumed.add(ammo); }
            }
            if (mask == 0) return false;
        } else {
            int mode = ammoMode(gun);
            ItemStack ammo = findAmmo(player, mode);
            if (ammo.isEmpty() && !player.getAbilities().instabuild) return false;
            mask = 1 << mode;
            if (!ammo.isEmpty()) consumed.add(ammo);
        }
        // Collect first; never partially spend ammunition for a rejected shot.
        if (!player.getAbilities().instabuild) for (ItemStack ammo : consumed) ammo.shrink(1);
        float damage = combined ? 17 + Integer.bitCount(mask) : 7;
        fireRay(level, player, mask, damage);
        gun.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
        player.getCooldowns().addCooldown(ItemInit.FINGER1_WEAPON1.get(), RIFLE_COOLDOWN);
        player.getCooldowns().addCooldown(ItemInit.FINGER1_WEAPON2.get(), RIFLE_COOLDOWN);
        level.playSound(null, player.blockPosition(), SoundInit.GUN_SEMI.get(), SoundSource.PLAYERS, 1, combined ? .8f : 1);
        return true;
    }

    public static boolean specialShot(Player player) {
        InteractionHand hand = rifleHand(player);
        return hand != null && shootRifle(player, hand, true);
    }

    private static boolean canHit(Player shooter, LivingEntity target) {
        if (target == shooter || !target.isAlive() || target.isSpectator() || !target.canBeHitByProjectile()) return false;
        if (target instanceof Player other && (other.isCreative() || !shooter.getServer().isPvpAllowed() || !shooter.canHarmPlayer(other))) return false;
        return !(target instanceof MobNPC1 npc) || !MobNPC3.preventDamage(npc, shooter.damageSources().playerAttack(shooter));
    }

    private static void fireRay(ServerLevel level, Player player, int mask, float damage) {
        Vec3 start = player.getEyePosition(), look = player.getLookAngle();
        Vec3 end = level.clip(new ClipContext(start, start.add(look.scale(RANGE)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation();
        EntityHitResult result = ProjectileUtil.getEntityHitResult(level, player, start, end,
                player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1),
                entity -> entity instanceof LivingEntity living && canHit(player, living));
        if (result != null && result.getEntity() instanceof LivingEntity target) {
            end = result.getLocation();
            DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ARROW), player, player);
            float before = target.getHealth();
            if (target.hurt(source, damage)) {
                float dealt = Math.max(0, before - target.getHealth());
                if ((mask & 1) != 0) target.addEffect(new MobEffectInstance(MobEffectInit.BLEED.get(), 100, 0), player);
                if ((mask & 2) != 0) target.setSecondsOnFire(5);
                if ((mask & 4) != 0) target.addEffect(new MobEffectInstance(MobEffectInit.FREEZE.get(), 100, 0), player);
                if ((mask & 8) != 0 && dealt > 0 && target.isAlive()) {
                    DamageSource piercing = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(PIERCING_DAMAGE), player, player);
                    target.hurt(piercing, dealt * .1f);
                }
                if ((mask & 16) != 0 && target.isAlive()) pushFiveBlocks(target, look);
            }
        }
        Vec3 mid = start.lerp(end, .5);
        ForgeFeaturePackets.sendNear(level, mid, start.distanceTo(end) / 2 + 64, new FingerShotTrail(start, end, mask));
    }

    private static void pushFiveBlocks(LivingEntity target, Vec3 look) {
        Vec3 horizontal = new Vec3(look.x, 0, look.z);
        Vec3 push = (horizontal.lengthSqr() < .000001 ? look : horizontal.normalize()).scale(5);
        target.move(MoverType.SELF, push);
        target.setDeltaMovement(Vec3.ZERO); target.hurtMarked = true;
        if (target instanceof ServerPlayer player) player.connection.teleport(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
    }

    public static boolean holds(LivingEntity holder, Class<? extends Item> type) {
        return type.isInstance(holder.getMainHandItem().getItem()) || type.isInstance(holder.getOffhandItem().getItem());
    }

    public static int setUnlocked(LivingEntity holder) {
        return holder.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof Finger2Armor2 && holds(holder, Finger2Weapon2.class) ? 1 : 0;
    }

    public static int setQuick(LivingEntity holder) {
        return holder.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof Finger2Armor3 && holds(holder, Finger2WeaponSP.class) ? 1 : 0;
    }

    public static boolean distributeBuff(Player player) {
        ItemStack armor = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(armor.getItem() instanceof Finger2Armor2) || !holds(player, Finger2Weapon3.class)
                || !player.isAlive() || player.isSpectator() || player.getCooldowns().isOnCooldown(armor.getItem())) return false;
        if (!(player.level() instanceof ServerLevel level)) return false;
        MobEffect effect = switch (level.random.nextInt(3)) {
            case 0 -> MobEffectInit.POWER.get(); case 1 -> MobEffectInit.ENDURANCE.get(); default -> MobEffectInit.QUICK.get();
        };
        player.addEffect(new MobEffectInstance(effect, 400, 0), player);
        for (LivingEntity ally : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16),
                living -> living != player && living.isAlive() && !living.isSpectator() && living.distanceToSqr(player) <= 256 && isAlly(player, living)))
            ally.addEffect(new MobEffectInstance(effect, 400, 0), player);
        player.getCooldowns().addCooldown(armor.getItem(), 600);
        return true;
    }

    private static boolean isAlly(Player player, LivingEntity target) {
        return player.isAlliedTo(target) || target instanceof TamableAnimal pet && pet.isOwnedBy(player)
                || target instanceof Player && !player.getServer().isPvpAllowed();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onIncomingDamage(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        if (event.getAmount() <= 0 || target.level().isClientSide) return;
        if (target instanceof Player player && player.isUsingItem() && player.getUseItem().getItem() instanceof Finger2Weapon3
                && !player.getCooldowns().isOnCooldown(player.getUseItem().getItem()) && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            Item item = player.getUseItem().getItem();
            event.setCanceled(true); player.stopUsingItem(); player.getCooldowns().addCooldown(item, 60); return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if ((source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO))
                && source.getDirectEntity() == source.getEntity() && source.getEntity() instanceof LivingEntity attacker
                && weapon(attacker).getItem() instanceof Finger2Weapon2 && target.getHealth() <= target.getMaxHealth() * .5f)
            event.setAmount(event.getAmount() + 2);
    }
}
