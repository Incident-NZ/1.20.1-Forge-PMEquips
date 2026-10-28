package net.pm_equips.items;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.renderer.HeadClawR;
import net.pm_equips.util.FingerEquipment;
import net.pm_equips.util.ItemStackData;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class HeadClawGauntlet extends SwordItem implements GeoItem {
    public static final int COOLDOWN = 1200, SINGULARITY_DURATION = 18000;
    private static final String MODE = "claw_serum_mode", NEXT = "claw_next_attack";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HeadClawGauntlet() {
        super(new Tier() {
            @Override public int getUses() { return 1000000; }
            @Override public float getSpeed() { return 4; }
            @Override public float getAttackDamageBonus() { return 0; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
            @Override public int getEnchantmentValue() { return 0; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        }, new Properties().rarity(Rarity.EPIC)
                .attributes(FingerEquipment.meleeAttributes(11, 2))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
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
            case 1 -> MobEffectInit.RCORP_SIN; case 2 -> MobEffectInit.KCORP_SIN; default -> MobEffectInit.WCORP_SIN;
        }, SINGULARITY_DURATION, 0));
        ItemStackData.putInt(stack, NEXT, mode == 0 ? 30 : mode == 1 ? 27 : 16);
        player.getCooldowns().addCooldown(this, COOLDOWN);
        return InteractionResultHolder.consume(stack);
    }
    private static LivingEntity meleeAttacker(DamageSource source) {
        if (!source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK) && !source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) return null;
        return source.getEntity() == source.getDirectEntity() && source.getEntity() instanceof LivingEntity attacker
                && attacker.getWeaponItem().getItem() instanceof HeadClawGauntlet ? attacker : null;
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity attacker = meleeAttacker(event.getSource());
        if (attacker == null || event.getAmount() <= 0) return;
        int damage = pendingDamage(attacker.getWeaponItem());
        double current = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        // Replace the effective attack stat, including R's multiplier, retaining cooldown/critical scaling.
        if (damage > 0 && current > 0) event.setAmount((float)(event.getAmount() * damage / current));
    }
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        LivingEntity attacker = meleeAttacker(event.getSource());
        if (attacker != null && event.getNewDamage() + event.getReduction(DamageContainer.Reduction.ABSORPTION) > 0)
            ItemStackData.update(attacker.getWeaponItem(), tag -> tag.remove(NEXT));
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) { target.hurtTime = 0; target.invulnerableTime = 0; }
        return true;
    }
    @Override public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) { }

    @Override public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private HeadClawR renderer;
            @Override public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
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
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.translatable("tooltip.pm_equips.claw_controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.claw_serum", serumName(serum(stack))));
        if (pendingDamage(stack) > 0) tooltip.add(Component.translatable("tooltip.pm_equips.claw_pending", pendingDamage(stack)));
        super.appendHoverText(stack, context, tooltip, flags);
    }
}
