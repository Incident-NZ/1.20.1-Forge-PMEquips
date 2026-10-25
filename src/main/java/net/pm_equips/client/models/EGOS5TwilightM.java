package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit5Twilight;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5TwilightM extends GeoModel<LobEGOSuit5Twilight> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit5Twilight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit5Twilight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_twilight.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit5Twilight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
