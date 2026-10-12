package net.pm_equips;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pm_equips.blocks.LobotomyEGOCraftTable;
import net.pm_equips.blocks.LobotomyEGOExtractionTable;
import net.pm_equips.blocks.PEBoxGenerator;

public class BlockInit {
    public static class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PMEquipsMain.MOD_ID);

        public static final RegistryObject<Block> PEBOX_GENERATOR = BLOCKS.register("pe_box_generator", PEBoxGenerator::new);
        public static final RegistryObject<Block> EGO_EXTRACTION_TABLE = BLOCKS.register("ego_extraction_table", LobotomyEGOExtractionTable::new);
        public static final RegistryObject<Block> EGO_CRAFT_TABLE = BLOCKS.register("ego_craft_table", LobotomyEGOCraftTable::new);
    }

    public static class BlockItems {
        public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PMEquipsMain.MOD_ID);

        public static final RegistryObject<Item> PEBOX_GENERATOR = BLOCK_ITEMS.register("pe_box_generator",
                () -> new BlockItem(Blocks.PEBOX_GENERATOR.get(), new Item.Properties().stacksTo(64)));
        public static final RegistryObject<Item> EGO_EXTRACTION_TABLE = BLOCK_ITEMS.register("ego_extraction_table",
                () -> new BlockItem(Blocks.EGO_EXTRACTION_TABLE.get(), new Item.Properties().stacksTo(64)));
        public static final RegistryObject<BlockItem> EGO_CRAFT_TABLE = BLOCK_ITEMS.register("ego_craft_table",
                () -> new BlockItem(Blocks.EGO_CRAFT_TABLE.get(), new Item.Properties().stacksTo(64)));
    }
}
