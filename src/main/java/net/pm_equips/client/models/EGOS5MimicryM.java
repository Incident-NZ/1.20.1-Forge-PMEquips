package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit5Mimicry;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5MimicryM extends GeoModel<LobEGOSuit5Mimicry> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit5Mimicry animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit5Mimicry animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_mimicry.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit5Mimicry animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
