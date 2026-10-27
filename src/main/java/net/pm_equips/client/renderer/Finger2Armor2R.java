package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.Finger2Armor2M;
import net.pm_equips.items.Finger2Armor2;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Finger2Armor2R extends GeoArmorRenderer<Finger2Armor2> {
    public Finger2Armor2R() {
        super(new Finger2Armor2M());
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
