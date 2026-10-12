package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit1Soda;
import software.bernie.geckolib.model.GeoModel;

public class EGOS1SodaM extends GeoModel<LobEGOSuit1Soda> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit1Soda animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit1Soda animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s1_soda.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit1Soda animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}