package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5SmileM;
import net.pm_equips.items.LobEGOSuit5Smile;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5SmileR extends GeoArmorRenderer<LobEGOSuit5Smile> {
    public EGOS5SmileR() {
        super(new EGOS5SmileM());
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
