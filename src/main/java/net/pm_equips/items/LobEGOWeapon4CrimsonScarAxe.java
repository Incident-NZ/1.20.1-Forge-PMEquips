package net.pm_equips.items;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.config.CommonConfig;

public class LobEGOWeapon4CrimsonScarAxe extends AxeItem {

    private static final float MIN_DAMAGE = 13.0F;

    private static final ResourceLocation REACH_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "lobegoweapon4crimsonscaraxe_reach_uuid");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, -1.0, AttributeModifier.Operation.ADD_VALUE);

    /** POWER amplifier 4 = レベル5 */
    private static final int POWER_AMPLIFIER = 4;
    private static final int POWER_DURATION = 2; // inventoryTick で付け直し

    public LobEGOWeapon4CrimsonScarAxe() {
        // 攻撃力: 1 + 12 = 13
        super(new CustomTier(), new Properties().durability(3000).attributes(AxeItem.createAttributes(new CustomTier(), 12, -2.4F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }
        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;
        boolean empowered = isHolding && isEmpowered(player);

        // リーチ減少
        AttributeInstance reachAttr = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER.id())) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER.id())) {
                reachAttr.removeModifier(REACH_MODIFIER.id());
            }
        }

        // 強化時: POWER Amp4 を付与
        if (empowered) {
            player.addEffect(new MobEffectInstance(
                    MobEffectInit.POWER,
                    POWER_DURATION,
                    POWER_AMPLIFIER,
                    false,
                    false,
                    true
            ));
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide
                && isEmpowered(player)
                && entity instanceof LivingEntity target
                && target.isAlive()
                && player.isAlliedTo(target)) {

            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(player.level().damageSources().playerAttack(player), MIN_DAMAGE);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return true;
        }
        return false;
    }

    /** オフハンドが CrimsonScarL かつ HPが最大の50%以下 */
    public static boolean isEmpowered(Player player) {
        return player.getOffhandItem().is(ItemInit.W4_CRIMSON_SCAR_L.get())
                && player.getHealth() <= player.getMaxHealth() * 0.5F;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(ItemInit.LCORP_EBOX.get());
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(ItemInit.LCORP_EBOX.get());
        }
    }

    @EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class CrimsonScarEvents {

        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                return;
            }
            if (!player.getMainHandItem().is(ItemInit.W4_CRIMSON_SCAR_R.get())) {
                return;
            }

            LivingEntity target = event.getEntity();
            boolean empowered = isEmpowered(player);

            // 非強化時のみ、設定OFFなら味方ダメージ無効
            if (!empowered
                    && !CommonConfig.ALLOW_FRIENDLY_FIRE.get()
                    && player.isAlliedTo(target)) {
                event.setCanceled(true);
                return;
            }

            target.invulnerableTime = 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.CRIMSON_SCAR_SCYTHE_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
