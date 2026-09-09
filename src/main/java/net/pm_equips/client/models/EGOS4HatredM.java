package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL4Hatred;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4HatredM extends GeoModel<LCorpEGOL4Hatred> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4Hatred animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4Hatred animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_hatred.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4Hatred animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
