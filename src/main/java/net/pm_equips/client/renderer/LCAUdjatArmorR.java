package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.LCAUdjatArmorM;
import net.pm_equips.items.LCAUdjatArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LCAUdjatArmorR extends GeoArmorRenderer<LCAUdjatArmor> {
    public LCAUdjatArmorR() {
        super(new LCAUdjatArmorM());
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
