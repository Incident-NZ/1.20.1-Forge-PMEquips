package net.pm_equips.items;



import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.pm_equips.PMERegistryBlock;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.config.CommonConfig;

import java.util.UUID;

public class LCorpEGOR4BlueScar extends SwordItem {
    private static final ResourceLocation REACH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("pm_equips", "blue_scar_reach");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_MODIFIER_ID, -1.0, AttributeModifier.Operation.ADD_VALUE);
    private static final float LOW_HEALTH_DAMAGE_MULTIPLIER = 1.5F;

    public LCorpEGOR4BlueScar() {
        super(new CustomTier(), new Properties().durability(3000).attributes(SwordItem.createAttributes(new CustomTier(), 16, -2.2f)));
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

    private static boolean isEmpowered(Player player) {
        return player.getMainHandItem().is(PMERegistryItem.W4_BLUE_SCAR.get())
                && player.getHealth() <= player.getMaxHealth() * 0.5F;
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class BlueScarEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                return;
            }

            if (!player.getMainHandItem().is(PMERegistryItem.W4_BLUE_SCAR.get())) {
                return;
            }

            LivingEntity target = event.getEntity();
            boolean empowered = isEmpowered(player);

            if (player.isAlliedTo(target) && !empowered) {
                if (!CommonConfig.ALLOW_FRIENDLY_FIRE.get()) {
                    event.setCanceled(true);
                    return;
                }
            }

            if (empowered) {
                event.setAmount(event.getAmount() * LOW_HEALTH_DAMAGE_MULTIPLIER);
            }

            target.invulnerableTime = 0;
        }
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 3000;
        }

        @Override
        public float getSpeed() {
            return 4.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0f;
        }



        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(PMERegistryBlock.BlockItems.WAW_PE_BOX.get());
        }
    }
}
