package net.pm_equips.items;

import net.minecraft.network.chat.Component;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.BlockInit;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.config.CommonConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW4BlueScar extends SwordItem {
    private static final UUID REACH_UUID = UUID.randomUUID();
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "blue_scar_reach", -1.0, AttributeModifier.Operation.ADDITION);
    private static final float LOW_HEALTH_DAMAGE_MULTIPLIER = 1.5F;

    public EGOW4BlueScar() {
        super(new CustomTier(), 16, -2.2f, new Properties().durability(3000));
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
        return player.getMainHandItem().is(ItemInit.W4_BLUE_SCAR.get())
                && player.getHealth() <= player.getMaxHealth() * 0.5F;
    }

    @Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class BlueScarEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                return;
            }

            if (!player.getMainHandItem().is(ItemInit.W4_BLUE_SCAR.get())) {
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

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.WAW_PE_BOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.BLUE_SCAR_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
