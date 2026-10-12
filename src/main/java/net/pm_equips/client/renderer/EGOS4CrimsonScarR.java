package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4CrimsonScarM;
import net.pm_equips.items.LobEGOSuit4CrimsonScar;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4CrimsonScarR extends GeoArmorRenderer<LobEGOSuit4CrimsonScar> {
    public EGOS4CrimsonScarR() {
        super(new EGOS4CrimsonScarM());
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
