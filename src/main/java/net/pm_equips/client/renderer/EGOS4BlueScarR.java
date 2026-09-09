package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4BlueScarM;
import net.pm_equips.items.LCorpEGOL4BlueScar;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4BlueScarR extends GeoArmorRenderer<LCorpEGOL4BlueScar> {
    public EGOS4BlueScarR() {
        super(new EGOS4BlueScarM());
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
