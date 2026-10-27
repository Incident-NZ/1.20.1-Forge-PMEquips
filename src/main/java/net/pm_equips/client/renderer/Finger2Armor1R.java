package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.Finger2Armor1M;
import net.pm_equips.items.Finger2Armor1;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Finger2Armor1R extends GeoArmorRenderer<Finger2Armor1> {
    public Finger2Armor1R() {
        super(new Finger2Armor1M());
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
