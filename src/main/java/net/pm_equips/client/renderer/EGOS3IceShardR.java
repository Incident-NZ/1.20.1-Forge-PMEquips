package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3IceShardM;
import net.pm_equips.items.LobEGOSuit3IceShard;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3IceShardR extends GeoArmorRenderer<LobEGOSuit3IceShard> {
    public EGOS3IceShardR() {
        super(new EGOS3IceShardM());
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
