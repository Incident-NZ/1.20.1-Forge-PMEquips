package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pm_equips.blocks.LobotomyEGOCraftTable;
import net.pm_equips.blocks.LobotomyEGOExtractionTable;
import net.pm_equips.blocks.PEBoxGenerator;

public class BlockInit {
    public static class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PMEquipsMain.MOD_ID);

        public static final DeferredHolder<Block, Block> PEBOX_GENERATOR = BLOCKS.register("pe_box_generator", () -> new PEBoxGenerator());
        public static final DeferredHolder<Block, Block> EGO_EXTRACTION_TABLE = BLOCKS.register("ego_extraction_table", () -> new LobotomyEGOExtractionTable());
        public static final DeferredHolder<Block, Block> EGO_CRAFT_TABLE = BLOCKS.register("ego_craft_table", () -> new LobotomyEGOCraftTable());
    }

    public static class BlockItems {
        public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(Registries.ITEM, PMEquipsMain.MOD_ID);

        public static final DeferredHolder<Item, Item> PEBOX_GENERATOR = BLOCK_ITEMS.register("pe_box_generator",
                () -> new BlockItem(Blocks.PEBOX_GENERATOR.get(), new Item.Properties().stacksTo(64)));
        public static final DeferredHolder<Item, Item> EGO_EXTRACTION_TABLE = BLOCK_ITEMS.register("ego_extraction_table",
                () -> new BlockItem(Blocks.EGO_EXTRACTION_TABLE.get(), new Item.Properties().stacksTo(64)));
        public static final DeferredHolder<Item, BlockItem> EGO_CRAFT_TABLE = BLOCK_ITEMS.register("ego_craft_table",
                () -> new BlockItem(Blocks.EGO_CRAFT_TABLE.get(), new Item.Properties().stacksTo(64)));
    }
}
