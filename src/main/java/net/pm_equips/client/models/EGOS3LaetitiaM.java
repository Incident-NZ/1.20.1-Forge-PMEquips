package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Laetitia;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3LaetitiaM extends GeoModel<LobEGOSuit3Laetitia> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Laetitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Laetitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_laetitia.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Laetitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
