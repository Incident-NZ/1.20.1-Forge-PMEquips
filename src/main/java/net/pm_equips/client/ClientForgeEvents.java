package net.pm_equips.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.fml.common.Mod;
import net.pm_equips.PMERegistryItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.minecraft.resources.ResourceLocation;
import net.pm_equips.PMEquipsMain;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.PMERegistryKeyBind;
import net.pm_equips.network.ModPackets;

import java.util.List;

@net.neoforged.fml.common.EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID,
        bus = net.neoforged.fml.common.EventBusSubscriber.Bus.GAME,
        value = Dist.CLIENT)
public class ClientForgeEvents {

    public static boolean isScopeActive = false;

    //Overlay
    private static final ResourceLocation SCOPE_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "textures/gui/gun_scope.png");

    private static final List<DeferredHolder<Item, ? extends Item>> OVERLAY_ITEMS = List.of(
            PMERegistryItem.W1_SODA,
            PMERegistryItem.W2_SOLITUDE,
            PMERegistryItem.W2_TODAY,
            PMERegistryItem.W2_FOURTH_MATCH_FIRE,
            PMERegistryItem.W2_BEAK,
            PMERegistryItem.W3_LAETITIA,
            PMERegistryItem.W3_HARMONY,
            PMERegistryItem.W4_MAGIC_BULLET,
            PMERegistryItem.W4_AROMA,
            PMERegistryItem.W4_SOLEMN_LAMENT_R,
            PMERegistryItem.W4_HORNET,
            PMERegistryItem.W4_CRIMSON_SCAR_L,
            PMERegistryItem.W5_PARADISE_LOST,
            PMERegistryItem.W5_PINK,
            PMERegistryItem.WEAPON_ROLAND_REVOLVER,
            PMERegistryItem.WEAPON_ROLAND_SHOTGUN,
            PMERegistryItem.RCORP_RABBIT_RIFLE
    );

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
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
        for (DeferredHolder<Item, ? extends Item> regObj : OVERLAY_ITEMS) {
            if (regObj.get() == item) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        isScopeActive = PMERegistryKeyBind.SCOPE_KEY.isDown();
        if (PMERegistryKeyBind.RELOAD_KEY.consumeClick()) {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new ModPackets.ReloadPacket());
        }
        if (PMERegistryKeyBind.CORE_PAGE_ABILITY_KEY.consumeClick()) {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new ModPackets.CorePageActivatePacket());
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
}
