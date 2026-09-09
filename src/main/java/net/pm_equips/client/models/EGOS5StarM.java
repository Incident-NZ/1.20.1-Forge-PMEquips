package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL5Star;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5StarM extends GeoModel<LCorpEGOL5Star> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5Star animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL5Star animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_star.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL5Star animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
