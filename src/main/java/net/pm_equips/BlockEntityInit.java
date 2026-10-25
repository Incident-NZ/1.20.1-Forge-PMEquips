package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pm_equips.blockentity.LobotomyEGOExtractionTableBlockEntity;
import net.pm_equips.blockentity.PEBoxGeneratorBlockEntity;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PEBoxGeneratorBlockEntity>> EBOX_GEN = BLOCK_ENTITIES.register("pe_box_generator",
            () -> BlockEntityType.Builder.of(PEBoxGeneratorBlockEntity::new, BlockInit.Blocks.PEBOX_GENERATOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LobotomyEGOExtractionTableBlockEntity>> LOBOTOMY_EGO_EXTRACTION_TABLE = BLOCK_ENTITIES.register("ego_extraction_table",
            () -> BlockEntityType.Builder.of(LobotomyEGOExtractionTableBlockEntity::new, BlockInit.Blocks.EGO_EXTRACTION_TABLE.get()).build(null));
}
