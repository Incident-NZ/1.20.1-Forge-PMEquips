package net.pm_equips.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.ItemInit;
import net.pm_equips.KeyBindInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.WingRCorp4thRabbitWeapon1;
import net.pm_equips.items.Finger2WeaponSP;
import net.pm_equips.network.NetworkPacketInit;
import net.pm_equips.network.RCorpRabbitRiflePacket;
import net.pm_equips.util.FingerEquipment;

@EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME,
        value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onEmptyAttack(PlayerInteractEvent.LeftClickEmpty event) {
        if (Finger2WeaponSP.form(event.getItemStack()) != 0)
            PacketDistributor.sendToServer(new NetworkPacketInit.CaduceusMissPacket());
    }

    public static boolean isScopeActive = false;

    //Overlay
    private static final ResourceLocation SCOPE_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "textures/gui/gun_scope.png");

    private static final List<DeferredHolder<Item, Item>> OVERLAY_ITEMS = List.of(
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
            ItemInit.W4_HORNET_ALTER,
            ItemInit.W4_CRIMSON_SCAR_L,
            ItemInit.W5_PARADISE_LOST,
            ItemInit.W5_PINK,
            ItemInit.WEAPON_ROLAND_REVOLVER,
            ItemInit.WEAPON_ROLAND_SHOTGUN,
            ItemInit.RCORP_RABBIT_RIFLE,
            ItemInit.LCORP_PISTOL,
            ItemInit.LAMENT_YISANG,
            ItemInit.LCA_AUTO_RIFLE,
            ItemInit.LCCB_PISTOL,
            ItemInit.LCCB_SHOTGUN
    );

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return;
        if (!isScopeItem(stack.getItem())) return;
        if (!ClientForgeEvents.isScopeActive) return;

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
        for (DeferredHolder<Item, Item> regObj : OVERLAY_ITEMS) {
            if (regObj.get() == item) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) { isScopeActive = false; return; }
         {
            isScopeActive = KeyBindInit.SCOPE_KEY.isDown();
            if (KeyBindInit.RELOAD_KEY.consumeClick()) {
                PacketDistributor.sendToServer(new NetworkPacketInit.ReloadPacket());
            }
            if (KeyBindInit.LEG_ARMOR_ABILITY_KEY.consumeClick()) {
                PacketDistributor.sendToServer(new NetworkPacketInit.LegArmorAbilityActivatePacket());
            }
            if (KeyBindInit.WEAPON_ABILITY_KEY.consumeClick()) {
                ItemStack main = mc.player.getMainHandItem(), off = mc.player.getOffhandItem();
                if (FingerEquipment.isThumbRifle(main) || FingerEquipment.isThumbRifle(off)
                        || main.getItem() instanceof WingRCorp4thRabbitWeapon1 || off.getItem() instanceof WingRCorp4thRabbitWeapon1)
                    PacketDistributor.sendToServer(new RCorpRabbitRiflePacket());
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

    public static void spawnThumbTrail(Vec3 start, Vec3 end, int mask) {
        var level = Minecraft.getInstance().level;
        double distance = start.distanceTo(end);
        if (level == null || !Double.isFinite(distance) || distance > 129 || mask <= 0 || mask > 31) return;
        List<DustParticleOptions> particles = new ArrayList<>();
        Vector3f[] colors = {new Vector3f(.9f, .9f, .9f), new Vector3f(1, .25f, .05f), new Vector3f(.35f, .8f, 1),
                new Vector3f(1, .9f, .2f), new Vector3f(.8f, .3f, 1)};
        for (int mode = 0; mode < 5; mode++) if ((mask & 1 << mode) != 0) particles.add(new DustParticleOptions(colors[mode], .8f));
        int steps = Mth.clamp(Mth.ceil(distance * 2), 1, 256);
        for (int i = 0; i <= steps; i++) {
            Vec3 pos = start.lerp(end, (double)i / steps);
            level.addParticle(particles.get(i % particles.size()), pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }
}
