package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.BlockInit;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.config.CommonConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW4CrimsonScarR extends SwordItem {

    private static final float MIN_DAMAGE = 13.0F;

    private static final UUID REACH_UUID = UUID.fromString("c51d9e2a-4b7f-4e8a-9c1d-2f3a4b5c6d7e");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "crimson_scar_reach", -1.0, AttributeModifier.Operation.ADDITION);

    /** POWER amplifier 4 = レベル5 */
    private static final int POWER_AMPLIFIER = 4;
    private static final int POWER_DURATION = 2; // inventoryTick で付け直し

    public EGOW4CrimsonScarR() {
        // 攻撃力: 1 + 12 = 13
        super(new CustomTier(), 12, -2.4F, new Properties().durability(3000));
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
        AttributeInstance reachAttr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER);
            }
        }

        // 強化時: POWER Amp4 を付与
        if (empowered) {
            player.addEffect(new MobEffectInstance(
                    MobEffectInit.POWER.get(),
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
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
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
        return repair.is(BlockInit.BlockItems.WAW_PE_BOX.get());
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 3000; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.WAW_PE_BOX.get());
        }
    }

    @Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class CrimsonScarEvents {

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.CRIMSON_SCAR_SCYTHE_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}