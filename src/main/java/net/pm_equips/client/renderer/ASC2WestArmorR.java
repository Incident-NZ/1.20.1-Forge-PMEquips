package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.ASC2WestArmorM;
import net.pm_equips.items.FixerASC2WestArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ASC2WestArmorR extends GeoArmorRenderer<FixerASC2WestArmor> {
    public ASC2WestArmorR() {
        super(new ASC2WestArmorM());
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
