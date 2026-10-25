package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.LCEEGODimensionShredderArmorM;
import net.pm_equips.items.LCEEGODimensionShredderArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LCEEGODimensionShredderArmorR extends GeoArmorRenderer<LCEEGODimensionShredderArmor> {
    public LCEEGODimensionShredderArmorR() {
        super(new LCEEGODimensionShredderArmorM());
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
