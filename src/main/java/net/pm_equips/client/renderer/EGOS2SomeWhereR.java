package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS2SomeWhereM;
import net.pm_equips.items.LobEGOSuit2SomeWhere;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2SomeWhereR extends GeoArmorRenderer<LobEGOSuit2SomeWhere> {
    public EGOS2SomeWhereR() {
        super(new EGOS2SomeWhereM());
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
