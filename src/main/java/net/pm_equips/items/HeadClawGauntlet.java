package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.renderer.HeadClawR;
import net.pm_equips.util.FingerEquipment;
import net.pm_equips.util.ItemStackData;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class HeadClawGauntlet extends SwordItem implements GeoItem {
    public static final int COOLDOWN = 1200, SINGULARITY_DURATION = 18000;
    private static final String MODE = "claw_serum_mode", NEXT = "claw_next_attack";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HeadClawGauntlet() {
        super(new Tier() {
            @Override public int getUses() { return 0; }
            @Override public float getSpeed() { return 4; }
            @Override public float getAttackDamageBonus() { return 0; }
            @Override public int getLevel() { return 0; }
            @Override public int getEnchantmentValue() { return 0; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        }, 10, -2F, new Properties().rarity(Rarity.EPIC));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }
    public static int serum(ItemStack stack) { return Math.floorMod(ItemStackData.getInt(stack, MODE), 3); }
    public static int pendingDamage(ItemStack stack) {
        return switch (ItemStackData.getInt(stack, NEXT)) { case 30 -> 30; case 27 -> 27; case 16 -> 16; default -> 0; };
    }
    private static String serumName(int mode) { return switch (mode) { case 1 -> "R"; case 2 -> "K"; default -> "W"; }; }
    public static boolean cycleSerum(Player player) {
        if (!player.isAlive() || player.isSpectator()) return false;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof HeadClawGauntlet)) stack = player.getOffhandItem();
        if (!(stack.getItem() instanceof HeadClawGauntlet)) return false;
        if (!player.level().isClientSide) {
            int next = (serum(stack) + 1) % 3;
            ItemStackData.putInt(stack, MODE, next);
            player.displayClientMessage(Component.translatable("tooltip.pm_equips.claw_serum", serumName(next)), true);
        }
        return true;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isAlive() || player.isSpectator() || player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.consume(stack);
        int mode = serum(stack);
        if (mode == 0 && !ClassWCorpTeleport.teleportForward(level, player, 128)) return InteractionResultHolder.fail(stack);
        if (mode == 2) player.heal(player.getMaxHealth() * .5F);
        player.addEffect(new MobEffectInstance(switch (mode) {
            case 1 -> MobEffectInit.RCORP_SIN.get(); case 2 -> MobEffectInit.KCORP_SIN.get(); default -> MobEffectInit.WCORP_SIN.get();
        }, SINGULARITY_DURATION, 0));
        ItemStackData.putInt(stack, NEXT, mode == 0 ? 30 : mode == 1 ? 27 : 16);
        player.getCooldowns().addCooldown(this, COOLDOWN);
        return InteractionResultHolder.consume(stack);
    }
    private static LivingEntity meleeAttacker(DamageSource source) {
        if (!source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK) && !source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) return null;
        return source.getEntity() == source.getDirectEntity() && source.getEntity() instanceof LivingEntity attacker
                && FingerEquipment.weapon(attacker).getItem() instanceof HeadClawGauntlet ? attacker : null;
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingHurtEvent event) {
        LivingEntity attacker = meleeAttacker(event.getSource());
        if (attacker == null || event.getAmount() <= 0) return;
        ItemStack stack = FingerEquipment.weapon(attacker);
        int damage = pendingDamage(stack);
        double current = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        // Hurt runs after shields/iframes and before armor; R's multiplier is already in the attack stat.
        if (damage > 0 && current > 0) {
            event.setAmount((float)(event.getAmount() * damage / current));
            ItemStackData.update(stack, tag -> tag.remove(NEXT));
        }
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) { target.hurtTime = 0; target.invulnerableTime = 0; }
        return true;
    }

    @Override public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HeadClawR renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new HeadClawR();
                return renderer;
            }
        });
    }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(IDLE); return PlayState.CONTINUE;
        }));
    }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("tooltip.pm_equips.claw_controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.claw_serum", serumName(serum(stack))));
        if (pendingDamage(stack) > 0) tooltip.add(Component.translatable("tooltip.pm_equips.claw_pending", pendingDamage(stack)));
        super.appendHoverText(stack, context, tooltip, flags);
    }
}
