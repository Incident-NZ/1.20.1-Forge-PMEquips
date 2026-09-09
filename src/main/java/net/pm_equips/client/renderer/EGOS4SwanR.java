package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4SwanM;
import net.pm_equips.items.LCorpEGOL4Swan;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4SwanR extends GeoArmorRenderer<LCorpEGOL4Swan> {
    public EGOS4SwanR() {
        super(new EGOS4SwanM());
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
