package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.LCDArmorM;
import net.pm_equips.items.LCDArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LCDArmorR extends GeoArmorRenderer<LCDArmor> {
    public LCDArmorR() {
        super(new LCDArmorM());
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
