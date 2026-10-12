package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit5Justitia;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5JustitiaM extends GeoModel<LobEGOSuit5Justitia> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit5Justitia animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit5Justitia animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s5_justitia.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit5Justitia animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
