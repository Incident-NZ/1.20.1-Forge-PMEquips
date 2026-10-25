package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Hornet;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4HornetM extends GeoModel<LobEGOSuit4Hornet> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Hornet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Hornet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_hornet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Hornet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
