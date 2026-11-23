package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingTCorpArmor3;
import software.bernie.geckolib.model.GeoModel;

public class TCorpArmor3M extends GeoModel<WingTCorpArmor3> {
    @Override public ResourceLocation getModelResource(WingTCorpArmor3 item) { return new ResourceLocation("pm_equips", "geo/tcorp_3.geo.json"); }
    @Override public ResourceLocation getTextureResource(WingTCorpArmor3 item) { return new ResourceLocation("pm_equips", "textures/armor/tcorp_3.png"); }
    @Override public ResourceLocation getAnimationResource(WingTCorpArmor3 item) { return new ResourceLocation("pm_equips", "animations/armor.animation.json"); }
}
