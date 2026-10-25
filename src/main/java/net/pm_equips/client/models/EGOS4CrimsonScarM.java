package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4CrimsonScar;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4CrimsonScarM extends GeoModel<LobEGOSuit4CrimsonScar> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4CrimsonScar animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4CrimsonScar animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_crimson_scar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4CrimsonScar animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
