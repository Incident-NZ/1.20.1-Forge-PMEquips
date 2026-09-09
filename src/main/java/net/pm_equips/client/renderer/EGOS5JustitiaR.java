package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5JustitiaM;
import net.pm_equips.items.LCorpEGOL5Justitia;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5JustitiaR extends GeoArmorRenderer<LCorpEGOL5Justitia> {
    public EGOS5JustitiaR() {
        super(new EGOS5JustitiaM());
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
