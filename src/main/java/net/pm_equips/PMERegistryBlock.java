package net.pm_equips;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.blocks.LobotomyEGOExtractionTable;
import net.pm_equips.blocks.PEBoxGenerator;

public class PMERegistryBlock {
    public static class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PMEquipsMain.MOD_ID);

    //PE-Box
    public static final DeferredHolder<Block, ? extends Block> ZAYIN_PE_BOX = BLOCKS.register("e_box_1",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.0f, 50f)
                    .sound(SoundType.METAL)
                    .instabreak()));

        public static final DeferredHolder<Block, ? extends Block> TETH_PE_BOX = BLOCKS.register("e_box_2",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(1.0f, 50f)
                        .sound(SoundType.METAL)
                        .instabreak()));

        public static final DeferredHolder<Block, ? extends Block> HE_PE_BOX = BLOCKS.register("e_box_3",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(1.0f, 50f)
                        .sound(SoundType.METAL)
                        .instabreak()));

        public static final DeferredHolder<Block, ? extends Block> WAW_PE_BOX = BLOCKS.register("e_box_4",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(1.0f, 50f)
                        .sound(SoundType.METAL)
                        .instabreak()));

        public static final DeferredHolder<Block, ? extends Block> ALEPH_PE_BOX = BLOCKS.register("e_box_5",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(1.0f, 50f)
                        .sound(SoundType.METAL)
                        .instabreak()));

        public static final DeferredHolder<Block, ? extends Block> PEBOX_GENERATOR = BLOCKS.register("pe_box_generator",
                () -> new PEBoxGenerator(BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 5.0F)
                        .sound(SoundType.METAL)));
        public static final DeferredHolder<Block, ? extends Block> EGO_EXTRACTION_TABLE = BLOCKS.register("ego_extraction_table",
                () -> new LobotomyEGOExtractionTable(BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 5.0F)
                        .sound(SoundType.METAL)));
    }

    public static class BlockItems {
        public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(Registries.ITEM, PMEquipsMain.MOD_ID);

        public static final DeferredHolder<Item, ? extends Item> ZAYIN_PE_BOX = BLOCK_ITEMS.register("e_box_1",
                () -> new BlockItem(Blocks.ZAYIN_PE_BOX.get(), new Item.Properties().stacksTo(64)));

        public static final DeferredHolder<Item, ? extends Item> TETH_PE_BOX = BLOCK_ITEMS.register("e_box_2",
                () -> new BlockItem(Blocks.TETH_PE_BOX.get(), new Item.Properties().stacksTo(64)));

        public static final DeferredHolder<Item, ? extends Item> HE_PE_BOX = BLOCK_ITEMS.register("e_box_3",
                () -> new BlockItem(Blocks.HE_PE_BOX.get(), new Item.Properties().stacksTo(64)));

        public static final DeferredHolder<Item, ? extends Item> WAW_PE_BOX = BLOCK_ITEMS.register("e_box_4",
                () -> new BlockItem(Blocks.WAW_PE_BOX.get(), new Item.Properties().stacksTo(64)));

        public static final DeferredHolder<Item, ? extends Item> ALEPH_PE_BOX = BLOCK_ITEMS.register("e_box_5",
                () -> new BlockItem(Blocks.ALEPH_PE_BOX.get(), new Item.Properties().stacksTo(64)));

        public static final DeferredHolder<Item, ? extends Item> PEBOX_GENERATOR = BLOCK_ITEMS.register("pe_box_generator",
                () -> new BlockItem(Blocks.PEBOX_GENERATOR.get(), new Item.Properties().stacksTo(64)));
        public static final DeferredHolder<Item, ? extends Item> EGO_EXTRACTION_TABLE = BLOCK_ITEMS.register("ego_extraction_table",
                () -> new BlockItem(Blocks.EGO_EXTRACTION_TABLE.get(), new Item.Properties().stacksTo(64)));
    }
}
