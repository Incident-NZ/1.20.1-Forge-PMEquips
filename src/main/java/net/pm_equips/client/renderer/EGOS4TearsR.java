package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4TearsM;
import net.pm_equips.items.LobEGOSuit4Tears;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4TearsR extends GeoArmorRenderer<LobEGOSuit4Tears> {
    public EGOS4TearsR() {
        super(new EGOS4TearsM());
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
