package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS4HeavenM;
import net.pm_equips.items.LobEGOSuit4Heaven;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS4HeavenR extends GeoArmorRenderer<LobEGOSuit4Heaven> {
    public EGOS4HeavenR() {
        super(new EGOS4HeavenM());
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
