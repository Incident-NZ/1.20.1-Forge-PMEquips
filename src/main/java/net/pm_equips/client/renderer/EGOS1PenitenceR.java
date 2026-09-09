package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS1PenitenceM;
import net.pm_equips.items.LCorpEGOL1Penitence;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS1PenitenceR extends GeoArmorRenderer<LCorpEGOL1Penitence> {
    public EGOS1PenitenceR() {
        super(new EGOS1PenitenceM());
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
