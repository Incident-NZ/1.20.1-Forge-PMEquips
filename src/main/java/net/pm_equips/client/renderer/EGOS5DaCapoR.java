package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS5DaCapoM;
import net.pm_equips.items.LobEGOSuit5DaCapo;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS5DaCapoR extends GeoArmorRenderer<LobEGOSuit5DaCapo> {
    public EGOS5DaCapoR() {
        super(new EGOS5DaCapoM());
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
