package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingRCorpArmor2;
import software.bernie.geckolib.model.GeoModel;

public class RCorpArmor2M extends GeoModel<WingRCorpArmor2> {
    @Override public ResourceLocation getModelResource(WingRCorpArmor2 item) { return new ResourceLocation("pm_equips", "geo/rcorp_2.geo.json"); }
    @Override public ResourceLocation getTextureResource(WingRCorpArmor2 item) { return new ResourceLocation("pm_equips", "textures/armor/rcorp_2.png"); }
    @Override public ResourceLocation getAnimationResource(WingRCorpArmor2 item) { return new ResourceLocation("pm_equips", "animations/armor.animation.json"); }
}
