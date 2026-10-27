package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.Finger2Armor3M;
import net.pm_equips.items.Finger2Armor3;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Finger2Armor3R extends GeoArmorRenderer<Finger2Armor3> {
    public Finger2Armor3R() {
        super(new Finger2Armor3M());
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
