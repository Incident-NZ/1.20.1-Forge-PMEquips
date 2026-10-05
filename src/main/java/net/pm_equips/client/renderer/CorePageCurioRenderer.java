package net.pm_equips.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.pm_equips.client.models.CorePageModel;
import net.pm_equips.items.*;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CorePageCurioRenderer implements ICurioRenderer {
    private final GeoArmorRenderer<CorePageItem> armorRenderer = new GeoArmorRenderer<>(new CorePageModel());
    private final KCorpAgentArmorR kcorpAgentRenderer = new KCorpAgentArmorR();
    private final KCorpOfficerArmorR kcorpOfficerRenderer = new KCorpOfficerArmorR();
    private final RCorp4thRabbitArmorR rcorp4thRabbitRenderer = new RCorp4thRabbitArmorR();
    private final WCorpArmorR wcorpRenderer = new WCorpArmorR();
    private final ASC2SouthArmorR asc2SouthRenderer = new ASC2SouthArmorR();
    private final HCorpMaoArmorR hcorpMaoRenderer = new HCorpMaoArmorR();
    private final HCorpSiArmorR hcorpSiRenderer = new HCorpSiArmorR();
    private final HCorpYouArmorR hcorpYouRenderer = new HCorpYouArmorR();

    @SuppressWarnings({"rawtypes"})
    private void renderArmor(GeoArmorRenderer renderer, ItemStack stack, PoseStack poseStack,
            MultiBufferSource buffers, int light, float partialTicks) {
        GeoAnimatable animatable = (GeoAnimatable) stack.getItem();
        RenderType renderType = renderer.getRenderType((Item) animatable, renderer.getTextureLocation(animatable), buffers, partialTicks);
        renderer.renderToBuffer(poseStack, buffers.getBuffer(renderType), light, OverlayTexture.NO_OVERLAY,
                1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
            PoseStack poseStack, RenderLayerParent<T, M> parent, MultiBufferSource buffers, int light,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(parent.getModel() instanceof HumanoidModel<?> humanoidModel)) return;

        if (stack.getItem() instanceof KCorpAgentArmor) {
            kcorpAgentRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(kcorpAgentRenderer, stack, poseStack, buffers, light, partialTicks);
            kcorpAgentRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(kcorpAgentRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof KCorpOfficerArmor) {
            kcorpOfficerRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(kcorpOfficerRenderer, stack, poseStack, buffers, light, partialTicks);
            kcorpOfficerRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(kcorpOfficerRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof RCorp4thRabbitArmor) {
            rcorp4thRabbitRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(rcorp4thRabbitRenderer, stack, poseStack, buffers, light, partialTicks);
            rcorp4thRabbitRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(rcorp4thRabbitRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof WCorpArmor) {
            wcorpRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(wcorpRenderer, stack, poseStack, buffers, light, partialTicks);
            wcorpRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(wcorpRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof Asc2SouthArmor) {
            asc2SouthRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(asc2SouthRenderer, stack, poseStack, buffers, light, partialTicks);
            asc2SouthRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(asc2SouthRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof HCorpMaoArmor) {
            hcorpMaoRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(hcorpMaoRenderer, stack, poseStack, buffers, light, partialTicks);
            hcorpMaoRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(hcorpMaoRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof HCorpSiArmor) {
            hcorpSiRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(hcorpSiRenderer, stack, poseStack, buffers, light, partialTicks);
            hcorpSiRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(hcorpSiRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        if (stack.getItem() instanceof HCorpYouArmor) {
            hcorpYouRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
            renderArmor(hcorpYouRenderer, stack, poseStack, buffers, light, partialTicks);
            hcorpYouRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
            renderArmor(hcorpYouRenderer, stack, poseStack, buffers, light, partialTicks);
            return;
        }

        armorRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.CHEST, humanoidModel);
        renderArmor(armorRenderer, stack, poseStack, buffers, light, partialTicks);
        armorRenderer.prepForRender(slotContext.entity(), stack, EquipmentSlot.LEGS, humanoidModel);
        renderArmor(armorRenderer, stack, poseStack, buffers, light, partialTicks);
    }
}
