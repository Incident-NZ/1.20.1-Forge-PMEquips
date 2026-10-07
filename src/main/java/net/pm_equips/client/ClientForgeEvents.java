package net.pm_equips.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.ItemInit;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraft.resources.ResourceLocation;
import net.pm_equips.PMEquipsMain;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import net.pm_equips.KeyBindInit;
import net.pm_equips.items.RCorpRabbitRifle;
import net.pm_equips.network.NetworkPacketInit;
import net.pm_equips.network.RCorpRabbitRiflePacket;

import java.util.List;

@Mod.EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public class ClientForgeEvents {

    public static boolean isScopeActive = false;

    //Overlay
    private static final ResourceLocation SCOPE_OVERLAY =
            new ResourceLocation(PMEquipsMain.MOD_ID, "textures/gui/gun_scope.png");

    private static final List<RegistryObject<Item>> OVERLAY_ITEMS = List.of(
            ItemInit.W1_SODA,
            ItemInit.W2_SOLITUDE,
            ItemInit.W2_TODAY,
            ItemInit.W2_FOURTH_MATCH_FIRE,
            ItemInit.W2_BEAK,
            ItemInit.W3_LAETITIA,
            ItemInit.W3_HARMONY,
            ItemInit.W4_MAGIC_BULLET,
            ItemInit.W4_AROMA,
            ItemInit.W4_SOLEMN_LAMENT_R,
            ItemInit.W4_HORNET,
            ItemInit.W4_CRIMSON_SCAR_L,
            ItemInit.W5_PARADISE_LOST,
            ItemInit.W5_PINK,
            ItemInit.WEAPON_ROLAND_REVOLVER,
            ItemInit.WEAPON_ROLAND_SHOTGUN,
            ItemInit.RCORP_RABBIT_RIFLE,
            ItemInit.LCORP_PISTOL
    );

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return;
        if (!isScopeItem(stack.getItem())) return;
        if (!net.pm_equips.client.ClientForgeEvents.isScopeActive) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int overlayWidth = 64;
        int overlayHeight = 64;
        int x = (screenWidth - overlayWidth) / 2;
        int y = (screenHeight - overlayHeight) / 2;

        event.getGuiGraphics().blit(
                SCOPE_OVERLAY,
                x, y,
                0, 0,
                overlayWidth, overlayHeight,
                overlayWidth, overlayHeight
        );

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static boolean isScopeItem(Item item) {
        for (RegistryObject<Item> regObj : OVERLAY_ITEMS) {
            if (regObj.get() == item) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            isScopeActive = KeyBindInit.SCOPE_KEY.isDown();
            if (KeyBindInit.RELOAD_KEY.consumeClick()) {
                NetworkPacketInit.INSTANCE.sendToServer(new NetworkPacketInit.ReloadPacket());
            }
            if (KeyBindInit.CORE_PAGE_ABILITY_KEY.consumeClick()) {
                NetworkPacketInit.INSTANCE.sendToServer(new NetworkPacketInit.CorePageActivatePacket());
            }
        }
    }

    @SubscribeEvent
    public static void onFovModifier(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return;

        if (ClientForgeEvents.isScopeItem(stack.getItem()) && isScopeActive) {
            event.setFOV(event.getFOV() * 0.5F);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }

        if (KeyBindInit.WEAPON_ABILITY_KEY.consumeClick()) {
            Player player = mc.player;
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            // ライフルを持っているときだけ送信（無駄パケット防止）
            if (main.getItem() instanceof RCorpRabbitRifle
                    || off.getItem() instanceof RCorpRabbitRifle) {
                NetworkPacketInit.INSTANCE.sendToServer(new RCorpRabbitRiflePacket());
            }
        }
    }
}
