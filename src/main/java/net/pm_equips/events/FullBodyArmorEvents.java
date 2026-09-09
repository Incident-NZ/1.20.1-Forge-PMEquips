package net.pm_equips.events;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.GeoArmorItem;
import net.pm_equips.items.LCorpEGOL4CrimsonScar;
import net.pm_equips.items.LCorpEGOL5Twilight;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class FullBodyArmorEvents {
    private static final EquipmentSlot[] DEFENSIVE_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.FEET
    };

    private FullBodyArmorEvents() {}

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack legArmor = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(legArmor.getItem() instanceof GeoArmorItem)) return;

        for (EquipmentSlot slot : DEFENSIVE_SLOTS) {
            ItemStack equipped = player.getItemBySlot(slot);
            if (isDefensive(equipped)) {
                returnToInventory(player, slot, equipped);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof LCorpEGOL5Twilight) {
            LCorpEGOL5Twilight.tick(player);
        }
        if (legs.getItem() instanceof LCorpEGOL4CrimsonScar) {
            LCorpEGOL4CrimsonScar.updateSpeedModifier(player);
        } else {
            LCorpEGOL4CrimsonScar.removeSpeedModifier(player);
        }
    }

    private static boolean isDefensive(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof ArmorItem) return true;

        ItemAttributeModifiers modifiers = stack.getAttributeModifiers();
        return modifiers.modifiers().stream().anyMatch(entry ->
                (entry.attribute().equals(Attributes.ARMOR)
                        || entry.attribute().equals(Attributes.ARMOR_TOUGHNESS)
                        || entry.attribute().equals(Attributes.KNOCKBACK_RESISTANCE))
                        && entry.modifier().amount() > 0);
    }

    private static void returnToInventory(Player player, EquipmentSlot slot, ItemStack equipped) {
        ItemStack moved = equipped.copy();
        player.setItemSlot(slot, ItemStack.EMPTY);
        if (!player.getInventory().add(moved)) {
            player.drop(moved, false);
        }
    }
}

