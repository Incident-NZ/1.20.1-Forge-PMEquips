package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5TwilightM;
import net.pm_equips.items.LobEGOSuit5Twilight;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5TwilightR extends GeoArmorRenderer<LobEGOSuit5Twilight> {
    public EGOS5TwilightR() {
        super(new EGOS5TwilightM());
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
