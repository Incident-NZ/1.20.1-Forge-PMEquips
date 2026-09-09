package net.pm_equips.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMEquipsMain;

public class FixerRolandZelkovaAxe extends SwordItem {
    private static final float MIN_DAMAGE = 4.0F;
    private static final int DAMAGE_VARIANCE = 5;
    private static final float MIN_GUARD_PIERCING_DAMAGE = 3.0F;
    private static final int GUARD_PIERCING_DAMAGE_VARIANCE = 6;

    public FixerRolandZelkovaAxe() {
        super(new CustomTier(), new Properties().durability(1000).attributes(SwordItem.createAttributes(new CustomTier(), 7, -2.8F)));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide
                && player.getOffhandItem().is(PMERegistryItem.FIXER_ROLAND_ZELKOVA_MACE.get())
                && entity instanceof LivingEntity target
                && target.isAlive()) {
            target.invulnerableTime = 0;
            target.hurt(player.level().damageSources().magic(), rollGuardPiercingDamage(player));
        }

        return false;
    }

    private static float rollDamage(Player player) {
        return MIN_DAMAGE + player.level().random.nextInt(DAMAGE_VARIANCE);
    }

    private static float rollGuardPiercingDamage(Player player) {
        return MIN_GUARD_PIERCING_DAMAGE + player.level().random.nextInt(GUARD_PIERCING_DAMAGE_VARIANCE);
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 1000;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }



        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class ZelkovaAxeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                return;
            }

            if (!player.getMainHandItem().is(PMERegistryItem.FIXER_ROLAND_ZELKOVA_AXE.get())) {
                return;
            }

            event.setAmount(rollDamage(player));
        }
    }
}
