package net.pm_equips.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import net.pm_equips.ItemInit;

import java.util.List;

public class ADSPoseHandler
{
    private static final List<RegistryObject<Item>> ADS_ITEMS =
            List.of(
                    ItemInit.W5_PINK,
                    ItemInit.W4_HORNET,
                    ItemInit.W4_AROMA,
                    ItemInit.W4_MAGIC_BULLET,
                    ItemInit.W4_SOLEMN_LAMENT_R,
                    ItemInit.W4_CRIMSON_SCAR_L,
                    ItemInit.W3_LAETITIA,
                    ItemInit.W3_HARMONY,
                    ItemInit.W2_BEAK,
                    ItemInit.W2_FOURTH_MATCH_FIRE,
                    ItemInit.W2_SOLITUDE,
                    ItemInit.W2_TODAY,
                    ItemInit.W1_SODA,
                    ItemInit.LCORP_PISTOL,
                    ItemInit.WEAPON_ROLAND_REVOLVER,
                    ItemInit.WEAPON_ROLAND_SHOTGUN,
                    ItemInit.RCORP_RABBIT_RIFLE
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
