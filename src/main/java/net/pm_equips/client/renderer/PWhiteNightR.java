package net.pm_equips.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.pm_equips.entity.PWhiteNight;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PWhiteNightR extends EntityRenderer<PWhiteNight> {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/entity/entity_whitenight_1.png"),
            ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/entity/entity_whitenight_2.png"),
            ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/entity/entity_whitenight_3.png"),
    };

    public PWhiteNightR(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        this.shadowStrength = 0.0F;
    }

    @Override
    public void render(PWhiteNight entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float entityYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float scale = entity.getRenderScale();

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        poseStack.translate(0.0F, scale * 0.5F, 0.0F);
        poseStack.scale(scale, scale, scale);

        PoseStack.Pose pose =
                poseStack.last();

        VertexConsumer vertexConsumer =
                buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        int light =
                Math.max(packedLight, LightTexture.FULL_BRIGHT);

        vertex(
                vertexConsumer,
                pose,
                -0.5F,
                -0.5F,
                0.0F,
                1.0F,
                light
        );
        vertex(
                vertexConsumer,
                pose,
                0.5F,
                -0.5F,
                1.0F,
                1.0F,
                light
        );
        vertex(
                vertexConsumer,
                pose,
                0.5F,
                0.5F,
                1.0F,
                0.0F,
                light
        );
        vertex(
                vertexConsumer,
                pose,
                -0.5F,
                0.5F,
                0.0F,
                0.0F,
                light
        );

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(
            VertexConsumer vertexConsumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float u,
            float v,
            int light
    ) {
        vertexConsumer.addVertex(pose, x, y, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(PWhiteNight entity) {
        int id = entity.getTextureId();
        id = Mth.clamp(id, 0, TEXTURES.length - 1);
        return TEXTURES[id];
    }
}
