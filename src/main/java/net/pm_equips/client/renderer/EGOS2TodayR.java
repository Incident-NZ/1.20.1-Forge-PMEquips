package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2TodayM;
import net.pm_equips.items.LCorpEGOL2Today;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2TodayR extends GeoArmorRenderer<LCorpEGOL2Today> {
    public EGOS2TodayR() {
        super(new EGOS2TodayM());
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
