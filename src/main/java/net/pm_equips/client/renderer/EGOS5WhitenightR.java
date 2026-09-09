package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5WhitenightM;
import net.pm_equips.items.LCorpEGOL5WhiteNight;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5WhitenightR extends GeoArmorRenderer<LCorpEGOL5WhiteNight> {
    public EGOS5WhitenightR() {
        super(new EGOS5WhitenightM());
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
