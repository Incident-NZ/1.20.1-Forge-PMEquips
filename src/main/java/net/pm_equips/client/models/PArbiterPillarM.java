package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.entity.PArbiterPillar;
import software.bernie.geckolib.model.GeoModel;

public class PArbiterPillarM extends GeoModel<PArbiterPillar> {
    private static final ResourceLocation MODEL = new ResourceLocation(PMEquipsMain.MOD_ID, "geo/p_arbiter_pillar.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(PMEquipsMain.MOD_ID, "textures/entity/p_arbiter_pillar.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(PMEquipsMain.MOD_ID, "animations/p_arbiter_pillar.animation.json");

    @Override
    public ResourceLocation getModelResource(PArbiterPillar entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(PArbiterPillar entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(PArbiterPillar entity) { return ANIMATION; }
}
