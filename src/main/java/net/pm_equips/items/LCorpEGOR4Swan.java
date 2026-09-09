package net.pm_equips.items;



import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.pm_equips.PMERegistryBlock;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMEquipsMain;

import java.util.UUID;

public class LCorpEGOR4Swan extends SwordItem {
    private static final float REFLECTION_CHANCE = 0.1F;
    private static final ResourceLocation REACH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("pm_equips", "swan_reach");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_MODIFIER_ID, -0.5, AttributeModifier.Operation.ADD_VALUE);

    public LCorpEGOR4Swan() {
        super(new CustomTier(), new Properties().durability(3000).attributes(SwordItem.createAttributes(new CustomTier(), 6, -1.8F)));
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

        AttributeInstance reachAttr = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER_ID)) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER_ID)) {
                reachAttr.removeModifier(REACH_MODIFIER_ID);
            }
        }
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class SwanEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player)
                    || player.level().isClientSide()
                    || event.getAmount() <= 0.0F
                    || event.getSource().is(DamageTypes.THORNS)) {
                return;
            }

            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.is(PMERegistryItem.W4_SWAN.get())) {
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

        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(PMERegistryBlock.BlockItems.WAW_PE_BOX.get());
        }
    }
}
