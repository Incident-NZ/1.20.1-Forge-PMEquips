package net.pm_equips.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LCorpEGOL5Smile extends GeoArmorItem {
    public LCorpEGOL5Smile(Properties props) {
        super(props);
    }

    private static final Map<UUID, Integer> bonusHealth = new HashMap<>();
    private static final Map<UUID, Integer> bonusAttack = new HashMap<>();

    @net.neoforged.fml.common.EventBusSubscriber(modid = "pm_equips")
    public static class SmileEvents {
        @SubscribeEvent
        public static void onMobKill(LivingDeathEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) return;
            if (!SmileFullSet(player)) return;

            UUID id = player.getUUID();

            int hpBonus = bonusHealth.getOrDefault(id, 0) + 2;
            bonusHealth.put(id, hpBonus);

            if (hpBonus % 20 == 0) {
                player.setHealth(player.getMaxHealth());
            }

            int atkBonus = (hpBonus / 40) * 5;
            bonusAttack.put(id, atkBonus);

            applyModifiers(player, hpBonus, atkBonus);
        }

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            UUID id = player.getUUID();

            if (!SmileFullSet(player)) {
                if (bonusHealth.containsKey(id) || bonusAttack.containsKey(id)) {
                    bonusHealth.remove(id);
                    bonusAttack.remove(id);
                    clearModifiers(player);
                }
                return;
            }

            int hpBonus = bonusHealth.getOrDefault(id, 0);
            int atkBonus = bonusAttack.getOrDefault(id, 0);
            applyModifiers(player, hpBonus, atkBonus);
        }

        private static boolean SmileFullSet(Player player) {
            return (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof LCorpEGOL5Smile) &&
                    (player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LCorpEGOL5Smile);
        }

        private static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("pm_equips", "ego_s5_smile_health");
        private static final ResourceLocation ATTACK_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("pm_equips", "ego_s5_smile_attack");

        private static void applyModifiers(Player player, int hpBonus, int atkBonus) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);

            if (maxHealth != null) {
                maxHealth.removeModifier(HEALTH_MODIFIER_ID);
                maxHealth.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_ID, hpBonus, AttributeModifier.Operation.ADD_VALUE));
            }

            if (attack != null) {
                attack.removeModifier(ATTACK_MODIFIER_ID);
                attack.addPermanentModifier(new AttributeModifier(ATTACK_MODIFIER_ID, atkBonus, AttributeModifier.Operation.ADD_VALUE));
            }
        }

        private static void clearModifiers(Player player) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);

            if (maxHealth != null) {
                maxHealth.removeModifier(HEALTH_MODIFIER_ID);
            }
            if (attack != null) {
                attack.removeModifier(ATTACK_MODIFIER_ID);
            }
        }
    }
}

