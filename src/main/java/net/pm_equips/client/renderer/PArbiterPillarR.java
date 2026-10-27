package net.pm_equips.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.pm_equips.client.models.PArbiterPillarM;
import net.pm_equips.entity.PArbiterPillar;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PArbiterPillarR extends GeoEntityRenderer<PArbiterPillar> {
    public PArbiterPillarR(EntityRendererProvider.Context context) {
        super(context, new PArbiterPillarM());
        shadowRadius = 0;
    }

    @Override
    protected void applyRotations(PArbiterPillar entity, PoseStack poses, float age,
                                  float bodyYaw, float partialTick, float scale) {
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        // Projectile yaw is atan2(x,z), pitch is positive upwards. The supplied model's long axis is +X.
        poses.mulPose(Axis.YP.rotationDegrees(yaw - 90));
        poses.mulPose(Axis.ZP.rotationDegrees(pitch));
        // Centre the original cube (Y=18/16) and place its leading face at the collision point.
        poses.translate(-PArbiterPillar.LENGTH / 2, -18.0 / 16 - .01, 0);
    }
}
