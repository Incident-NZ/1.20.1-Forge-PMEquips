package net.pm_equips.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.PMERegistryItem;

import java.util.List;

public class ADSPoseHandler
{
    private static final List<DeferredHolder<Item, ? extends Item>> ADS_ITEMS =
            List.of(
                    PMERegistryItem.W5_PINK,
                    PMERegistryItem.W4_HORNET,
                    PMERegistryItem.W4_AROMA,
                    PMERegistryItem.W4_MAGIC_BULLET,
                    PMERegistryItem.W4_SOLEMN_LAMENT_R,
                    PMERegistryItem.W4_CRIMSON_SCAR_L,
                    PMERegistryItem.W3_LAETITIA,
                    PMERegistryItem.W3_HARMONY,
                    PMERegistryItem.W2_BEAK,
                    PMERegistryItem.W2_FOURTH_MATCH_FIRE,
                    PMERegistryItem.W2_SOLITUDE,
                    PMERegistryItem.W2_TODAY,
                    PMERegistryItem.W1_SODA,
                    PMERegistryItem.WEAPON_ROLAND_REVOLVER,
                    PMERegistryItem.WEAPON_ROLAND_SHOTGUN,
                    PMERegistryItem.RCORP_RABBIT_RIFLE
            );

    public static boolean isADSWeapon(
            ItemStack stack
    )
    {
        if (stack.isEmpty()) {
            return false;
        }

        return ADS_ITEMS.stream()
                .anyMatch(obj -> obj.get() == stack.getItem());
    }

    public static void applyPose(
            HumanoidModel<?> model,
            Player player
    )
    {
        if (!isADSWeapon(player.getMainHandItem())) {
            return;
        }

        model.rightArm.xRot = (float)Math.toRadians(-100.0D);
        model.rightArm.yRot = (float)Math.toRadians(-10.0D);

        model.leftArm.xRot = (float)Math.toRadians(-90.0D);
        model.leftArm.yRot = (float)Math.toRadians(15.0D);
    }
}
