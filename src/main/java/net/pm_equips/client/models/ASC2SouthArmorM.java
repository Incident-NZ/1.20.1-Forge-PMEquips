package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.FixerASC2SouthArmor;
import software.bernie.geckolib.model.GeoModel;

public class ASC2SouthArmorM extends GeoModel<FixerASC2SouthArmor> {

    @Override
    public ResourceLocation getModelResource(FixerASC2SouthArmor animatable) {
        return new ResourceLocation("pm_equips", "geo/asc2_south_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FixerASC2SouthArmor animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/asc2_south_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FixerASC2SouthArmor animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
