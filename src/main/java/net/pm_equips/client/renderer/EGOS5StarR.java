package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5StarM;
import net.pm_equips.items.LCorpEGOL5Star;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5StarR extends GeoArmorRenderer<LCorpEGOL5Star> {
    public EGOS5StarR() {
        super(new EGOS5StarM());
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
