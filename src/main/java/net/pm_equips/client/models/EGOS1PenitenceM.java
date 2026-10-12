package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit1Penitence;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS1PenitenceM extends GeoModel<LobEGOSuit1Penitence> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit1Penitence animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit1Penitence animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s1_penitence.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit1Penitence animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}