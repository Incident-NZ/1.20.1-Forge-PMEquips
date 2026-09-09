package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2LanternM;
import net.pm_equips.items.LCorpEGOL2Lantern;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2LanternR extends GeoArmorRenderer<LCorpEGOL2Lantern> {
    public EGOS2LanternR() {
        super(new EGOS2LanternM());
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
