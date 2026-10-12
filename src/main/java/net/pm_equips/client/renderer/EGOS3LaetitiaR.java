package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3LaetitiaM;
import net.pm_equips.items.LobEGOSuit3Laetitia;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3LaetitiaR extends GeoArmorRenderer<LobEGOSuit3Laetitia> {
    public EGOS3LaetitiaR() {
        super(new EGOS3LaetitiaM());
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
