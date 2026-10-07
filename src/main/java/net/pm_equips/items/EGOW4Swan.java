package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
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
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.BlockInit;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW4Swan extends SwordItem {
    private static final float REFLECTION_CHANCE = 0.1F;
    private static final UUID REACH_UUID = UUID.randomUUID();
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "swan_reach", -0.5, AttributeModifier.Operation.ADDITION);

    public EGOW4Swan() {
        super(new CustomTier(), 6, -1.8F, new Properties().durability(3000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }
        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;

        AttributeInstance reachAttr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class SwanEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamage(LivingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player)
                    || player.level().isClientSide()
                    || event.getAmount() <= 0.0F
                    || event.getSource().is(DamageTypes.THORNS)) {
                return;
            }

            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.is(ItemInit.W4_SWAN.get())) {
                return;
            }

            if (!(event.getSource().getEntity() instanceof LivingEntity attacker)
                    || attacker == player
                    || !attacker.isAlive()
                    || player.getRandom().nextFloat() >= REFLECTION_CHANCE) {
                return;
            }

            attacker.hurt(player.damageSources().thorns(player), event.getAmount());
        }
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.SWAN_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
