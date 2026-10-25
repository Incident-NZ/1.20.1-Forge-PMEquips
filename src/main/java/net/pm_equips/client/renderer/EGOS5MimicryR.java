package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5MimicryM;
import net.pm_equips.items.LobEGOSuit5Mimicry;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5MimicryR extends GeoArmorRenderer<LobEGOSuit5Mimicry> {
    public EGOS5MimicryR() {
        super(new EGOS5MimicryM());
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
