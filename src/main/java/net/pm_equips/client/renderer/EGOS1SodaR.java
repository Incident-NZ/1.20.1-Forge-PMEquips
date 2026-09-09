package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS1SodaM;
import net.pm_equips.items.LCorpEGOL1Soda;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS1SodaR extends GeoArmorRenderer<LCorpEGOL1Soda> {
    public EGOS1SodaR() {
        super(new EGOS1SodaM());
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
