package net.pm_equips.client.renderer;

import net.minecraft.world.entity.EquipmentSlot;
import net.pm_equips.client.models.ASC2SouthArmorM;
import net.pm_equips.items.FixerASC2SouthArmor;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ASC2SouthArmorR extends GeoArmorRenderer<FixerASC2SouthArmor> {
    public ASC2SouthArmorR() {
        super(new ASC2SouthArmorM());
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
