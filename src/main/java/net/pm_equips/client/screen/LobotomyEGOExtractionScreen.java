package net.pm_equips.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.pm_equips.network.NetworkPacketInit;

public class LobotomyEGOExtractionScreen extends AbstractContainerScreen<LobotomyEGOExtractionMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "textures/gui/block_lobotomy_ego_extraction_table.png");

    public LobotomyEGOExtractionScreen(LobotomyEGOExtractionMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 222;
        inventoryLabelY = imageHeight - 90;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("gui.pm_equips.lobotomy_ego_extraction_table.craft"),
                        button -> PacketDistributor.sendToServer(new NetworkPacketInit.LobotomyEGOExtractPacket()))
                .bounds(leftPos + (imageWidth - 46), topPos + 8, 44, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        String[] tiers = {"zayin", "teth", "he", "waw", "aleph"};
        int[] costs = {8, 16, 32, 48, 64};
        for (int i = 0; i < tiers.length; i++) {
            guiGraphics.drawString(font,
                    Component.translatable("gui.pm_equips.lobotomy_ego_extraction_table.requirement",
                            Component.translatable("gui.pm_equips.lobotomy_ego_extraction_table.tier." + tiers[i]),
                            costs[i]),
                    8, 32 + i * 9, 0x404040, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
