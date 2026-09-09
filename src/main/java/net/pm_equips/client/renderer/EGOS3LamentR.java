package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3LamentM;
import net.pm_equips.items.LCorpEGOL3Lament;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3LamentR extends GeoArmorRenderer<LCorpEGOL3Lament> {
    public EGOS3LamentR() {
        super(new EGOS3LamentM());
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
