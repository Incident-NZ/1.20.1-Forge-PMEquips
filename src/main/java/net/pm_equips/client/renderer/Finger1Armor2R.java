package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.Finger1Armor2M;
import net.pm_equips.items.Finger1Armor2;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Finger1Armor2R extends GeoArmorRenderer<Finger1Armor2> {
    public Finger1Armor2R() {
        super(new Finger1Armor2M());
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
