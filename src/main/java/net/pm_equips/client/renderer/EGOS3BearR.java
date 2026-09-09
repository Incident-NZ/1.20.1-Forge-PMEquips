package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3BearM;
import net.pm_equips.items.LCorpEGOL3Bear;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3BearR extends GeoArmorRenderer<LCorpEGOL3Bear> {
    public EGOS3BearR() {
        super(new EGOS3BearM());
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
