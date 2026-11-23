package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.HeadExecutioner;
import software.bernie.geckolib.model.GeoModel;

public class HeadExecutionerM extends GeoModel<HeadExecutioner> {
    @Override public ResourceLocation getModelResource(HeadExecutioner item) { return new ResourceLocation("pm_equips", "geo/armor_head_executioner.geo.json"); }
    @Override public ResourceLocation getTextureResource(HeadExecutioner item) { return new ResourceLocation("pm_equips", "textures/armor/head_executioner.png"); }
    @Override public ResourceLocation getAnimationResource(HeadExecutioner item) { return new ResourceLocation("pm_equips", "animations/armor.animation.json"); }
}
