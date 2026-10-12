package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5PinkM;
import net.pm_equips.items.LobEGOSuit5Pink;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5PinkR extends GeoArmorRenderer<LobEGOSuit5Pink> {
    public EGOS5PinkR() {
        super(new EGOS5PinkM());
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
