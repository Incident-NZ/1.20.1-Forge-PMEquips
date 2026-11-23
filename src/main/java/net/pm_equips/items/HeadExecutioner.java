package net.pm_equips.items;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.renderer.HeadExecutionerR;
import net.pm_equips.items.materials.ArmorEquips;
import net.pm_equips.util.ItemStackData;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class HeadExecutioner extends ArmorItem implements GeoItem {
    public static final int COOLDOWN = 1200, SINGULARITY_DURATION = 18000;
    private static final String MODE = "executioner_serum_mode", NEXT = "executioner_next_attack";
    private static final Map<LivingEntity, Float> ABSORPTION_BEFORE = new WeakHashMap<>();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HeadExecutioner() {
        super(ArmorEquips.HEAD_EXECUTIONER, Type.LEGGINGS, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
    public static ItemStack equipped(LivingEntity entity) {
        ItemStack armor = entity.getItemBySlot(EquipmentSlot.LEGS);
        return armor.getItem() instanceof HeadExecutioner ? armor : ItemStack.EMPTY;
    }
    public static boolean unarmed(LivingEntity entity) {
        return entity.getMainHandItem().isEmpty() && entity.getOffhandItem().isEmpty();
    }
    public static int serum(ItemStack stack) { return Math.floorMod(ItemStackData.getInt(stack, MODE), 3); }
    public static int pendingDamage(ItemStack stack) {
        return switch (ItemStackData.getInt(stack, NEXT)) { case 30 -> 30; case 27 -> 27; case 16 -> 16; default -> 0; };
    }
    private static String serumName(int mode) { return switch (mode) { case 1 -> "R"; case 2 -> "K"; default -> "W"; }; }
    public static boolean cycleSerum(Player player) {
        ItemStack stack = equipped(player);
        if (stack.isEmpty() || !player.isAlive() || player.isSpectator()) return false;
        if (!player.level().isClientSide) {
            int next = (serum(stack) + 1) % 3;
            ItemStackData.putInt(stack, MODE, next);
            player.displayClientMessage(Component.translatable("tooltip.pm_equips.executioner_serum", serumName(next)), true);
        }
        return true;
    }
    public static boolean activate(Player player) {
        ItemStack stack = equipped(player);
        if (stack.isEmpty() || !unarmed(player) || !player.isAlive() || player.isSpectator() || player.isSleeping()
                || player.level().isClientSide || player.getCooldowns().isOnCooldown(stack.getItem())) return false;
        int mode = serum(stack);
        if (mode == 0 && !ClassWCorpTeleport.teleportForward(player.level(), player, 128)) return false;
        if (mode == 2) player.heal(player.getMaxHealth() * .5F);
        player.addEffect(new MobEffectInstance(switch (mode) {
            case 1 -> MobEffectInit.RCORP_SIN.get(); case 2 -> MobEffectInit.KCORP_SIN.get(); default -> MobEffectInit.WCORP_SIN.get();
        }, SINGULARITY_DURATION, 0));
        ItemStackData.putInt(stack, NEXT, mode == 0 ? 30 : mode == 1 ? 27 : 16);
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN);
        return true;
    }
    private static LivingEntity meleeAttacker(DamageSource source) {
        if (!source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK)
                && !source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) return null;
        return source.getEntity() == source.getDirectEntity() && source.getEntity() instanceof LivingEntity attacker
                && unarmed(attacker) && !equipped(attacker).isEmpty() ? attacker : null;
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingHurtEvent event) {
        LivingEntity attacker = meleeAttacker(event.getSource());
        if (attacker == null || event.getAmount() <= 0) return;
        ItemStack stack = equipped(attacker);
        int damage = pendingDamage(stack);
        double current = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (damage > 0 && current > 0) {
            ABSORPTION_BEFORE.put(event.getEntity(), event.getEntity().getAbsorptionAmount());
            event.setAmount((float) (event.getAmount() * damage / current));
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onDamage(LivingDamageEvent event) {
        Float absorption = ABSORPTION_BEFORE.remove(event.getEntity());
        LivingEntity attacker = meleeAttacker(event.getSource());
        if (attacker != null && ((!event.isCanceled() && event.getAmount() > 0)
                || absorption != null && event.getEntity().getAbsorptionAmount() < absorption))
            ItemStackData.update(equipped(attacker), tag -> tag.remove(NEXT));
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAttack(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide && unarmed(event.getEntity())
                && !equipped(event.getEntity()).isEmpty() && event.getTarget() instanceof LivingEntity target) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }
    }
    @Override public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HeadExecutionerR renderer;
            @Override public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                    EquipmentSlot slot, HumanoidModel<?> original) {
                if (renderer == null) renderer = new HeadExecutionerR();
                renderer.prepForRender(entity, stack, slot, original);
                return renderer;
            }
        });
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.animations.idle"));
            return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.executioner_controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.executioner_serum", serumName(serum(stack))));
        if (pendingDamage(stack) > 0) tooltip.add(Component.translatable("tooltip.pm_equips.executioner_pending", pendingDamage(stack)));
        tooltip.add(Component.translatable("tooltip.pm_equips.integrated_weapon", 14, 2));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
