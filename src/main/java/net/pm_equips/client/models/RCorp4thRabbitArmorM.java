package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsRCorp4thRabbitArmorTier1;
import software.bernie.geckolib.model.GeoModel;

public class RCorp4thRabbitArmorM extends GeoModel<WingsRCorp4thRabbitArmorTier1> {

    @Override
    public ResourceLocation getModelResource(WingsRCorp4thRabbitArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/rcorp_4th_rabbit_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsRCorp4thRabbitArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/rcorp_4th_rabbit_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsRCorp4thRabbitArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
