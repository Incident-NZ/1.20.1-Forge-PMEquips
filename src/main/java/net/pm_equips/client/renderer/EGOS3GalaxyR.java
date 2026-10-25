package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3GalaxyM;
import net.pm_equips.items.LobEGOSuit3Galaxy;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3GalaxyR extends GeoArmorRenderer<LobEGOSuit3Galaxy> {
    public EGOS3GalaxyR() {
        super(new EGOS3GalaxyM());
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot slot) {
        setAllVisible(false);

        if (slot == EquipmentSlot.LEGS) {
            setAllVisible(true);
            setAllBonesVisible(true);
        }
    }
}
