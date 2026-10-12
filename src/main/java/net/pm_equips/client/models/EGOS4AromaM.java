package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Aroma;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4AromaM extends GeoModel<LobEGOSuit4Aroma> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Aroma animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Aroma animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s4_aroma.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Aroma animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
