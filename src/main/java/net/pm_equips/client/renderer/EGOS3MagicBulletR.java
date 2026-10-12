package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.EGOS3MagicBulletM;
import net.pm_equips.items.LobEGOSuit3MagicBullet;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS3MagicBulletR extends GeoArmorRenderer<LobEGOSuit3MagicBullet> {
    public EGOS3MagicBulletR() {
        super(new EGOS3MagicBulletM());
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
