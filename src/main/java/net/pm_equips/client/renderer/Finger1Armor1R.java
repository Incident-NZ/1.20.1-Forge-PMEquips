package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.Finger1Armor1M;
import net.pm_equips.items.Finger1Armor1;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Finger1Armor1R extends GeoArmorRenderer<Finger1Armor1> {
    public Finger1Armor1R() {
        super(new Finger1Armor1M());
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
