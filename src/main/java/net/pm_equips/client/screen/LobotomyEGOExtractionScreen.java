package net.pm_equips.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.pm_equips.network.NetworkPacketInit;

public class LobotomyEGOExtractionScreen extends AbstractContainerScreen<LobotomyEGOExtractionMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PMEquipsMain.MOD_ID, "textures/gui/block_lobotomy_ego_extraction_table.png");

    public LobotomyEGOExtractionScreen(LobotomyEGOExtractionMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("gui.pm_equips.lobotomy_ego_extraction_table.craft"),
                        button -> NetworkPacketInit.INSTANCE.sendToServer(new NetworkPacketInit.LobotomyEGOExtractPacket()))
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
