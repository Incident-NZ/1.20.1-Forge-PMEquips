package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCAUdjatArmor;
import software.bernie.geckolib.model.GeoModel;

public class LCAUdjatArmorM extends GeoModel<LCAUdjatArmor> {
    @Override
    public ResourceLocation getModelResource(LCAUdjatArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/lca_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCAUdjatArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/lca_udjat_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCAUdjatArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
