package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4HornetM;
import net.pm_equips.items.LCorpEGOL4Hornet;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4HornetR extends GeoArmorRenderer<LCorpEGOL4Hornet> {
    public EGOS4HornetR() {
        super(new EGOS4HornetM());
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
