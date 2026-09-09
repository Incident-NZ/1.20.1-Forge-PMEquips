package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL5Twilight;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5TwilightM extends GeoModel<LCorpEGOL5Twilight> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5Twilight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL5Twilight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_twilight.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL5Twilight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
