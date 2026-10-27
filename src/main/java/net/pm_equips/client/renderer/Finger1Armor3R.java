package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.Finger1Armor3M;
import net.pm_equips.items.Finger1Armor3;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Finger1Armor3R extends GeoArmorRenderer<Finger1Armor3> {
    public Finger1Armor3R() {
        super(new Finger1Armor3M());
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
