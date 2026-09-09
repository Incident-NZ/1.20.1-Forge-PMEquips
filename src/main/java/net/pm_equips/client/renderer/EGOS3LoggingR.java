package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3LoggingM;
import net.pm_equips.items.LCorpEGOL3Logging;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3LoggingR extends GeoArmorRenderer<LCorpEGOL3Logging> {
    public EGOS3LoggingR() {
        super(new EGOS3LoggingM());
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
