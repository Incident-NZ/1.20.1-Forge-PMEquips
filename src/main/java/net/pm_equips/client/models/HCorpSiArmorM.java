package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingHCorpArmor2;
import software.bernie.geckolib.model.GeoModel;

public class HCorpSiArmorM extends GeoModel<WingHCorpArmor2> {
    @Override
    public ResourceLocation getModelResource(WingHCorpArmor2 animatable) {
        return new ResourceLocation("pm_equips", "geo/hcorp_si_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingHCorpArmor2 animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/hcorp_si.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingHCorpArmor2 animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
