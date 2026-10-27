package net.pm_equips.entity;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.pm_equips.PMEquipsMain;

/** Vanilla PlayerModel, including skin layers, walking, swimming and held-item animations. */
@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID, value = Dist.CLIENT)
public class MobNPC2 extends HumanoidMobRenderer<MobNPC1, PlayerModel<MobNPC1>> {
    private static final Map<String, ResourceLocation> CUSTOM_SKINS = new HashMap<>();
    private final PlayerModel<MobNPC1> wide, slim;

    public MobNPC2(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), .5F);
        wide = getModel();
        slim = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override public ResourceLocation getTextureLocation(MobNPC1 npc) {
        String name = npc.getSkinName();
        if (name.startsWith("builtin:")) return new ResourceLocation(PMEquipsMain.MOD_ID,
                "textures/entity/npc_" + name.substring(8) + ".png");
        return CUSTOM_SKINS.getOrDefault(name, DefaultPlayerSkin.getDefaultSkin(npc.getUUID()));
    }

    @Override public void render(MobNPC1 npc, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        String skin = npc.getSkinName();
        boolean slimSkin = skin.endsWith(".slim.png") || skin.endsWith(".slim")
                || (!skin.startsWith("builtin:") && !CUSTOM_SKINS.containsKey(skin) && DefaultPlayerSkin.getSkinModelName(npc.getUUID()).equals("slim"));
        model = slimSkin ? slim : wide;
        model.setAllVisible(true);
        model.crouching = npc.isCrouching();
        InteractionHand right = npc.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        model.rightArmPose = armPose(npc, right);
        model.leftArmPose = armPose(npc, right == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        super.render(npc, yaw, partialTick, pose, buffers, light);
    }

    private static HumanoidModel.ArmPose armPose(MobNPC1 npc, InteractionHand hand) {
        ItemStack held = npc.getItemInHand(hand);
        ItemStack other = npc.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (held.isEmpty()) return HumanoidModel.ArmPose.EMPTY;
        return npc.isAggressive() && MobNPC1.isRangedWeapon(held) && other.isEmpty()
                ? HumanoidModel.ArmPose.BOW_AND_ARROW : HumanoidModel.ArmPose.ITEM;
    }

    public static void acceptSkins(Map<String, byte[]> files) {
        clearTextures();
        files.forEach((name, png) -> {
            try {
                NativeImage image = normalizeSkin(NativeImage.read(png));
                ResourceLocation location = new ResourceLocation(PMEquipsMain.MOD_ID,
                        "npc_custom/" + Hashing.sha256().hashBytes(png));
                Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(image));
                CUSTOM_SKINS.put(name, location);
            } catch (IOException | IllegalArgumentException error) {
                PMEquipsMain.LOGGER.warn("Cannot decode NPC skin {}", name, error);
            }
        });
    }

    private static NativeImage normalizeSkin(NativeImage input) throws IOException {
        if (input.getWidth() != 64 || input.getHeight() != 32 && input.getHeight() != 64) {
            input.close(); throw new IOException("NPC skins must be 64x32 or 64x64");
        }
        boolean legacy = input.getHeight() == 32;
        NativeImage image = input;
        if (legacy) {
            image = new NativeImage(64, 64, true);
            for (int x = 0; x < 64; x++) for (int y = 0; y < 32; y++) image.setPixelRGBA(x, y, input.getPixelRGBA(x, y));
            input.close();
            // Mirror the six right-limb UV faces into the modern left-limb regions.
            int[][] faces = {{4,16,16,32,4,4},{8,16,16,32,4,4},{0,20,24,32,4,12},{4,20,16,32,4,12},
                    {8,20,8,32,4,12},{12,20,16,32,4,12},{44,16,-8,32,4,4},{48,16,-8,32,4,4},
                    {40,20,0,32,4,12},{44,20,-8,32,4,12},{48,20,-16,32,4,12},{52,20,-8,32,4,12}};
            for (int[] face : faces) image.copyRect(face[0], face[1], face[2], face[3], face[4], face[5], true, false);
            boolean opaqueOverlay = true;
            for (int x = 32; x < 64; x++) for (int y = 0; y < 32; y++)
                if ((image.getPixelRGBA(x, y) >>> 24) < 128) opaqueOverlay = false;
            if (opaqueOverlay) for (int x = 32; x < 64; x++) for (int y = 0; y < 16; y++)
                image.setPixelRGBA(x, y, image.getPixelRGBA(x, y) & 0x00ffffff);
        }
        // Player base layers are opaque; outer layers retain their alpha channel.
        for (int x = 0; x < 64; x++) for (int y = 0; y < 64; y++)
            if (x < 32 && y < 16 || y >= 16 && y < 32 || x >= 16 && x < 48 && y >= 48)
                image.setPixelRGBA(x, y, image.getPixelRGBA(x, y) | 0xff000000);
        return image;
    }

    private static void clearTextures() {
        CUSTOM_SKINS.values().stream().distinct().forEach(Minecraft.getInstance().getTextureManager()::release);
        CUSTOM_SKINS.clear();
    }
    @SubscribeEvent public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) { clearTextures(); }
}
