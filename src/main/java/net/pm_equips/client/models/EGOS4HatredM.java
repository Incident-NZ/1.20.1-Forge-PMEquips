package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Hatred;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4HatredM extends GeoModel<LobEGOSuit4Hatred> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Hatred animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Hatred animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_hatred.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Hatred animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
