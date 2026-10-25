package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS1WingBeatM;
import net.pm_equips.items.LobEGOSuit1WingBeat;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS1WingBeatR extends GeoArmorRenderer<LobEGOSuit1WingBeat> {
    public EGOS1WingBeatR() {
        super(new EGOS1WingBeatM());
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
