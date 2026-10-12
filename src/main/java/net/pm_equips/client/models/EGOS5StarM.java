package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit5Star;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5StarM extends GeoModel<LobEGOSuit5Star> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit5Star animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit5Star animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s5_star.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit5Star animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
