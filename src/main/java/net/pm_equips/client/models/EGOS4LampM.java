package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL4Lamp;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4LampM extends GeoModel<LCorpEGOL4Lamp> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4Lamp animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4Lamp animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_lamp.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4Lamp animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
