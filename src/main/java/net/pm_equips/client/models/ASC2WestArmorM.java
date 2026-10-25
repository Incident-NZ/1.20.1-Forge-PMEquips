package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.FixerASC2WestArmor;
import software.bernie.geckolib.model.GeoModel;

public class ASC2WestArmorM extends GeoModel<FixerASC2WestArmor> {

    @Override
    public ResourceLocation getModelResource(FixerASC2WestArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/asc2_west_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FixerASC2WestArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/asc2_west_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FixerASC2WestArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
