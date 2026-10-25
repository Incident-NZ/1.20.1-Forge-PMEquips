package net.pm_equips.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;

public class FixerColorRolandWeapon1 extends SwordItem {
    private static final float MIN_DAMAGE = 3.0F;
    private static final int DAMAGE_VARIANCE = 6;
    private static final float MIN_ARMOR_PIERCING_BONUS_DAMAGE = 4.0F;
    private static final int ARMOR_PIERCING_BONUS_DAMAGE_VARIANCE = 5;

    public FixerColorRolandWeapon1() {
        super(new CustomTier(), new Properties().durability(1000).attributes(SwordItem.createAttributes(new CustomTier(), 7, -2.9F)));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide
                && player.getOffhandItem().is(ItemInit.FIXER_ROLAND_ZELKOVA_AXE.get())
                && entity instanceof LivingEntity target
                && target.isAlive()) {
            target.invulnerableTime = 0;
            target.hurt(player.level().damageSources().magic(), rollArmorPiercingBonusDamage(player));
        }

        return false;
    }

    private static boolean isArmorPiercing(Player player) {
        return !player.getOffhandItem().is(ItemInit.FIXER_ROLAND_ZELKOVA_AXE.get());
    }

    private static float rollDamage(Player player) {
        return MIN_DAMAGE + player.level().random.nextInt(DAMAGE_VARIANCE);
    }

    private static float rollArmorPiercingBonusDamage(Player player) {
        return MIN_ARMOR_PIERCING_BONUS_DAMAGE + player.level().random.nextInt(ARMOR_PIERCING_BONUS_DAMAGE_VARIANCE);
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

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }

    @EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
    public static class ZelkovaMaceEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                return;
            }

            if (!player.getMainHandItem().is(ItemInit.FIXER_ROLAND_ZELKOVA_MACE.get())) {
                return;
            }

            if (isArmorPiercing(player)) {
                event.setAmount(rollDamage(player));
            }
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof Player player)) {
                return;
            }

            if (!player.getMainHandItem().is(ItemInit.FIXER_ROLAND_ZELKOVA_MACE.get())
                    || isArmorPiercing(player)) {
                return;
            }

            event.setNewDamage(rollDamage(player));
        }
    }
}
