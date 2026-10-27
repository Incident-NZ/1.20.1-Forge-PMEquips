package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.HeadArbiterArmorM;
import net.pm_equips.items.HeadArbiterArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HeadArbiterArmorR extends GeoArmorRenderer<HeadArbiterArmor> {
    public HeadArbiterArmorR() {
        super(new HeadArbiterArmorM());
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
