package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4StemM;
import net.pm_equips.items.LCorpEGOL4Stem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4StemR extends GeoArmorRenderer<LCorpEGOL4Stem> {
    public EGOS4StemR() {
        super(new EGOS4StemM());
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
