package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingHCorpYouArmor;
import software.bernie.geckolib.model.GeoModel;

public class HCorpYouArmorM extends GeoModel<WingHCorpYouArmor> {
    @Override
    public ResourceLocation getModelResource(WingHCorpYouArmor animatable) {
        return new ResourceLocation("pm_equips", "geo/hcorp_you_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingHCorpYouArmor animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/hcorp_you.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingHCorpYouArmor animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}