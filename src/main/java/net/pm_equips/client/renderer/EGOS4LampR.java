package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4LampM;
import net.pm_equips.items.LCorpEGOL4Lamp;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4LampR extends GeoArmorRenderer<LCorpEGOL4Lamp> {
    public EGOS4LampR() {
        super(new EGOS4LampM());
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
