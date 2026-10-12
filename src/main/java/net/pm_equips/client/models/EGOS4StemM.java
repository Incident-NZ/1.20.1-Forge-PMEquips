package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Stem;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4StemM extends GeoModel<LobEGOSuit4Stem> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Stem animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Stem animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s4_stem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Stem animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
