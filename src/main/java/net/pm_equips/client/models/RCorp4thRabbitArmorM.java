package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingRCorpArmor1;
import software.bernie.geckolib.model.GeoModel;

public class RCorp4thRabbitArmorM extends GeoModel<WingRCorpArmor1> {

    @Override
    public ResourceLocation getModelResource(WingRCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/rcorp_1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingRCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/rcorp_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingRCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
