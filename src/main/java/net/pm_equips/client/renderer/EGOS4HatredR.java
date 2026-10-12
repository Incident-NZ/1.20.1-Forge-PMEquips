package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4HatredM;
import net.pm_equips.items.LobEGOSuit4Hatred;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4HatredR extends GeoArmorRenderer<LobEGOSuit4Hatred> {
    public EGOS4HatredR() {
        super(new EGOS4HatredM());
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
