package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.LCCBArmorM;
import net.pm_equips.items.LCCBArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LCCBArmorR extends GeoArmorRenderer<LCCBArmor> {
    public LCCBArmorR() {
        super(new LCCBArmorM());
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
