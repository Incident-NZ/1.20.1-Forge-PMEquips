package net.pm_equips.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pm_equips.menu.LobotomyEGOCraftMenu;
import net.pm_equips.network.LobotomyEGOCraftRecipePacket;
import net.pm_equips.network.NetworkPacketInit;
import net.pm_equips.recipe.LobotomyEGORecipe;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * テクスチャ table_lcorp_extract_recipe.png 準拠
 * - 左上 6x6: レシピ一覧（アイコン表示・クリック選択）
 * - その右: スクロールバー
 * - 右の小スロット位置: 選択成果物プレビュー
 * - 下部 3x9+ホットバー: プレイヤインベントリのみ（テーブル機能なし）
 */
public class LobotomyEGOCraftScreen extends AbstractContainerScreen<LobotomyEGOCraftMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("pm_equips", "textures/gui/block_ego_table.png");

    // ===== テクスチャ上の座標（解析値） =====
    private static final int GRID_X = 8;
    private static final int GRID_Y = 27;
    private static final int SLOT = 18;
    private static final int COLS = 6;
    private static final int ROWS = 6;
    private static final int VISIBLE = COLS * ROWS; // 36

    private static final int SCROLL_X = 116;
    private static final int SCROLL_Y = 27;
    private static final int SCROLL_W = 12;
    private static final int SCROLL_H = ROWS * SLOT; // 108

    /** 右の小スロット（成果物プレビュー） */
    private static final int RESULT_X = 152;
    private static final int RESULT_Y = 80;

    private final List<LobotomyEGORecipe> recipes = new ArrayList<>();
    private int selectedIndex = 0;
    /** 行単位スクロール（6列グリッドなので 1スクロール = 1行 = 6レシピ） */
    private int scrollRow = 0;

    public LobotomyEGOCraftScreen(LobotomyEGOCraftMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // テクスチャ実パネルに合わせる（黒余白を除き 176 幅想定）
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = 6;

        this.recipes.addAll(LobotomyEGORecipeManager.getAll());
        if (!recipes.isEmpty()) {
            selectedIndex = 0;
        }
    }

    private int maxScrollRow() {
        int totalRows = (recipes.size() + COLS - 1) / COLS;
        return Math.max(0, totalRows - ROWS);
    }

    @Override
    protected void init() {
        super.init();

        // 作成ボタン（成果物スロット下あたり）
        addRenderableWidget(Button.builder(Component.literal("作成"), b -> {
            if (selectedIndex >= 0 && selectedIndex < recipes.size()) {
                LobotomyEGORecipe recipe = recipes.get(selectedIndex);
                NetworkPacketInit.INSTANCE.sendToServer(new LobotomyEGOCraftRecipePacket(recipe.getId()));
            }
        }).bounds(leftPos + 134, topPos + 110, 36, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        g.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderScrollbar(g);
    }

    private void renderScrollbar(GuiGraphics g) {
        int max = maxScrollRow();
        // トラック（半透明）
        g.fill(leftPos + SCROLL_X, topPos + SCROLL_Y,
                leftPos + SCROLL_X + SCROLL_W, topPos + SCROLL_Y + SCROLL_H,
                0x66000000);

        if (max <= 0) {
            // スクロール不要時はつまみを満杯表示
            g.fill(leftPos + SCROLL_X + 1, topPos + SCROLL_Y + 1,
                    leftPos + SCROLL_X + SCROLL_W - 1, topPos + SCROLL_Y + SCROLL_H - 1,
                    0xFF8B8B8B);
            return;
        }

        int thumbH = Math.max(12, SCROLL_H * ROWS / (max + ROWS));
        int thumbY = topPos + SCROLL_Y + (int) ((SCROLL_H - thumbH) * (scrollRow / (float) max));
        g.fill(leftPos + SCROLL_X + 1, thumbY,
                leftPos + SCROLL_X + SCROLL_W - 1, thumbY + thumbH,
                0xFFC6C6C6);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partial);

        // 6x6 レシピアイコン
        renderRecipeGrid(g, mouseX, mouseY);

        // 成果物プレビュー（右小スロット位置）
        if (selectedIndex >= 0 && selectedIndex < recipes.size()) {
            ItemStack result = recipes.get(selectedIndex).getResult();
            g.renderItem(result, leftPos + RESULT_X, topPos + RESULT_Y);
            g.renderItemDecorations(font, result, leftPos + RESULT_X, topPos + RESULT_Y);
        }

        renderTooltip(g, mouseX, mouseY);

        // グリッド上のツールチップ
        int hovered = getHoveredRecipeIndex(mouseX, mouseY);
        if (hovered >= 0 && hovered < recipes.size()) {
            g.renderTooltip(font, recipes.get(hovered).getResult(), mouseX, mouseY);
        } else if (isHovering(RESULT_X, RESULT_Y, 16, 16, mouseX, mouseY)
                && selectedIndex >= 0 && selectedIndex < recipes.size()) {
            LobotomyEGORecipe recipe = recipes.get(selectedIndex);
            List<Component> tip = new ArrayList<>();
            tip.add(recipe.getResult().getHoverName());
            for (LobotomyEGORecipe.IngredientEntry ing : recipe.getIngredients()) {
                int have = countHave(ing.asStack());
                tip.add(Component.literal(
                        ing.asStack().getHoverName().getString() + " " + have + "/" + ing.count()
                ).withStyle(have >= ing.count()
                        ? net.minecraft.ChatFormatting.GREEN
                        : net.minecraft.ChatFormatting.RED));
            }
            g.renderComponentTooltip(font, tip, mouseX, mouseY);
        }
    }

    private void renderRecipeGrid(GuiGraphics g, int mouseX, int mouseY) {
        int startIndex = scrollRow * COLS;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int index = startIndex + row * COLS + col;
                int x = leftPos + GRID_X + col * SLOT;
                int y = topPos + GRID_Y + row * SLOT;

                if (index >= recipes.size()) {
                    continue;
                }

                LobotomyEGORecipe recipe = recipes.get(index);
                ItemStack stack = recipe.getResult();

                // 選択枠
                if (index == selectedIndex) {
                    g.fill(x - 1, y - 1, x + 17, y + 17, 0x80FFAA00);
                }

                // 材料不足は暗く
                boolean can = recipe.matchesInventory(minecraft.player.getInventory());
                if (!can) {
                    g.fill(x, y, x + 16, y + 16, 0x80000000);
                }

                g.renderItem(stack, x, y);
                g.renderItemDecorations(font, stack, x, y);

                // ホバーハイライト
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    g.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                }
            }
        }
    }

    private int getHoveredRecipeIndex(int mouseX, int mouseY) {
        int relX = mouseX - leftPos - GRID_X;
        int relY = mouseY - topPos - GRID_Y;
        if (relX < 0 || relY < 0) return -1;

        int col = relX / SLOT;
        int row = relY / SLOT;
        if (col >= COLS || row >= ROWS) return -1;
        // スロット内余白クリックも許容（18pxセル）
        int index = scrollRow * COLS + row * COLS + col;
        return index < recipes.size() ? index : -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int index = getHoveredRecipeIndex((int) mouseX, (int) mouseY);
            if (index >= 0) {
                selectedIndex = index;
                return true;
            }

            // スクロールバークリック
            if (isHovering(SCROLL_X, SCROLL_Y, SCROLL_W, SCROLL_H, (int) mouseX, (int) mouseY)) {
                int max = maxScrollRow();
                if (max > 0) {
                    double ratio = (mouseY - (topPos + SCROLL_Y)) / (double) SCROLL_H;
                    scrollRow = Mth.clamp((int) Math.round(ratio * max), 0, max);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // グリッド or スクロール上でホイール
        if (isHovering(GRID_X, GRID_Y, COLS * SLOT + SCROLL_W + 4, SCROLL_H, (int) mouseX, (int) mouseY)) {
            int max = maxScrollRow();
            if (max > 0) {
                scrollRow = Mth.clamp(scrollRow - (int) Math.signum(delta), 0, max);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private int countHave(ItemStack need) {
        int total = 0;
        for (ItemStack s : minecraft.player.getInventory().items) {
            if (ItemStack.isSameItem(s, need)) {
                total += s.getCount();
            }
        }
        return total;
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, this.title, 8, 6, 0x404040, false);
        // inventoryLabel は親に任せる場合:
        g.drawString(font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
    }
}
