package net.pm_equips.entity;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.ItemInit;
import net.pm_equips.items.LCEEGODimensionShredderWeapon;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class DimensionShredderTests {
    private static void clearArena(GameTestHelper h, int height) {
        for (BlockPos pos : BlockPos.betweenClosed(h.absolutePos(new BlockPos(0, height, 2)), h.absolutePos(new BlockPos(14, height + 3, 10))))
            h.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
    }
    private static void equip(Player player) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemInit.LCE_EGO_DIMENSION_SHREDDER_WEAPON.get()));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemInit.LCE_EGO_DIMENSION_SHREDDER_WEAPON.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemInit.LCE_EGO_DIMENSION_SHREDDER_ARMOR.get()));
    }
    private static Player player(GameTestHelper h, int height) {
        Player player = ForgeTestPlayers.makeMockPlayer(h, GameType.SURVIVAL);
        clearArena(h, height);
        player.setPos(h.absoluteVec(new Vec3(2.5, height, 5.5)));
        player.setNoGravity(true); player.setYRot(-90); player.setXRot(0); equip(player);
        return player;
    }
    private static Villager target(GameTestHelper h, int x, int height, int z) {
        Villager target = h.spawn(EntityType.VILLAGER, x, height, z);
        target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100); target.setHealth(100);
        return target;
    }
    private static InteractionResult use(Player player) {
        return player.getMainHandItem().getItem().use(player.level(), player, InteractionHand.MAIN_HAND).getResult();
    }
    private static void ready(Player player) {
        player.getCooldowns().removeCooldown(ItemInit.LCE_EGO_DIMENSION_SHREDDER_WEAPON.get());
    }
    private static void openPath(Player player) {
        Vec3 start = player.position(), look = player.getLookAngle();
        for (int step = 0; step <= 144; step += 8)
            player.level().getChunk(BlockPos.containing(start.add(look.scale(step))));
        for (int step = 0; step <= 129; step++) {
            BlockPos block = BlockPos.containing(start.add(look.scale(step)));
            for (int y = 0; y <= 2; y++) player.level().setBlock(block.above(y), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void requiresMatchingWeaponsAndLegArmorAndOnlyActivatesOnce(GameTestHelper h) {
        Player player = player(h, 90); Vec3 before = player.position();
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        h.assertTrue(use(player) == InteractionResult.PASS, "Both weapons alone do not activate the skill");
        equip(player); player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        h.assertTrue(use(player) == InteractionResult.PASS && player.position().equals(before), "The matching offhand is required");
        equip(player);
        h.assertTrue(player.getOffhandItem().getItem().use(player.level(), player, InteractionHand.OFF_HAND).getResult() == InteractionResult.PASS,
                "The fallback offhand interaction cannot activate a second time");
        h.assertTrue(!player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem()), "Invalid attempts do not spend the cooldown");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void targetedWarpDeals13AndSphericalArea10WithoutDoubleHitting(GameTestHelper h) {
        Player player = player(h, 100);
        Villager primary = target(h, 8, 100, 5), nearby = target(h, 8, 100, 7), corner = target(h, 11, 100, 8);
        primary.invulnerableTime = 100;
        InteractionResult result = use(player);
        h.assertTrue(result.consumesAction(), "A visible entity can be targeted: " + result + "; player=" + player.position() + "; target=" + primary.position());
        h.assertTrue(player.position().distanceTo(primary.position().add(-2, 0, 0)) < .001, "Arrive exactly two blocks before the target");
        h.assertTrue(primary.getHealth() == 87 && nearby.getHealth() == 90 && corner.getHealth() == 100,
                "Primary takes 13 once; the three-block spherical area takes 10");
        h.assertTrue(player.getHealth() == 20, "The user is excluded from the area");
        h.assertTrue(primary.getLastDamageSource().is(DamageTypes.PLAYER_ATTACK)
                && primary.getLastDamageSource().getDirectEntity() == player, "Hits retain sword/player attack attribution");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void cooldownIsSharedByBothCopiesForExactlySixtyTicks(GameTestHelper h) {
        Player player = player(h, 110); Villager target = target(h, 8, 110, 5);
        h.assertTrue(use(player).consumesAction(), "The initial cooldown strike succeeds"); Vec3 after = player.position();
        h.assertTrue(use(player) == InteractionResult.FAIL && target.getHealth() == 87 && player.position().equals(after), "An immediate repeat does nothing");
        ItemStack main = player.getMainHandItem(); player.setItemInHand(InteractionHand.MAIN_HAND, player.getOffhandItem());
        player.setItemInHand(InteractionHand.OFF_HAND, main);
        for (int tick = 0; tick < 59; tick++) player.getCooldowns().tick();
        h.assertTrue(use(player) == InteractionResult.FAIL, "Changing stacks cannot bypass the first 59 ticks");
        player.getCooldowns().tick();
        h.assertTrue(use(player).consumesAction() && target.getHealth() == 74, "Tick 60 permits another 13-damage strike");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void forwardWarpReaches128AndStopsBeforeWallsAndHiddenTargets(GameTestHelper h) {
        Player player = player(h, 120); openPath(player); Vec3 start = player.position();
        use(player);
        h.assertTrue(Math.abs(player.position().distanceTo(start) - 128) < .001, "No target means an exact 128-block warp in open space");
        player.setPos(start); ready(player);
        Villager hidden = target(h, 12, 120, 5);
        for (int y = 0; y <= 3; y++) h.setBlock(new BlockPos(7, 120 + y, 5), Blocks.STONE);
        use(player);
        h.assertTrue(player.getX() > start.x && player.getBoundingBox().maxX < h.absolutePos(new BlockPos(7, 120, 5)).getX(),
                "The full body stays before a wall");
        h.assertTrue(hidden.getHealth() == 100, "An entity behind a wall is not a targeted strike");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void blockedEntityDestinationDoesNotTeleportOrDealDamage(GameTestHelper h) {
        Player player = player(h, 130); Villager target = target(h, 8, 130, 5); Vec3 start = player.position();
        h.setBlock(new BlockPos(6, 130, 5), Blocks.STONE);
        h.assertTrue(use(player) == InteractionResult.FAIL && player.position().equals(start) && target.getHealth() == 100,
                "A visible target with an obstructed feet position fails safely without damage");
        h.assertTrue(!player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem()), "An unsafe destination does not spend the cooldown");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void forwardFallProtectionSurvivesUnequippingAndEndsAtFirstLanding(GameTestHelper h) {
        Player player = player(h, 140); openPath(player); use(player);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        player.fallDistance = 20;
        player.causeFallDamage(20, 1, player.damageSources().fall());
        h.assertTrue(player.getHealth() == 20 && player.fallDistance == 0, "The first fall is fully protected after unequipping");
        player.causeFallDamage(20, 1, player.damageSources().fall());
        h.assertTrue(player.getHealth() == 3, "A later unrelated fall deals normal damage");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void safeLandingAlsoEndsProtectionAndEntityWarpDoesNotGrantIt(GameTestHelper h) {
        Player player = player(h, 150); openPath(player); use(player);
        h.runAfterDelay(2, () -> {
            player.setOnGround(true);
            LCEEGODimensionShredderWeapon.onPlayerTick(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, player));
            player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
            player.causeFallDamage(10, 1, player.damageSources().fall());
            h.assertTrue(player.getHealth() == 13, "Landing without a damaging fall still consumes protection");
            player.invulnerableTime = 0; player.setHealth(20); player.setPos(h.absoluteVec(new Vec3(2.5, 150, 5.5)));
            equip(player); ready(player); target(h, 8, 150, 5); use(player);
            player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
            player.causeFallDamage(10, 1, player.damageSources().fall());
            h.assertTrue(player.getHealth() == 13, "The targeted branch does not add forward-warp fall immunity");
            h.succeed();
        });
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void skillKillsRemainDirectPlayerSwordKills(GameTestHelper h) {
        Player player = player(h, 170);
        Villager primary = target(h, 8, 170, 5), nearby = target(h, 8, 170, 7);
        primary.setHealth(13); nearby.setHealth(10); use(player);
        h.assertTrue(!primary.isAlive() && !nearby.isAlive(), "Both primary and area strikes can kill");
        for (Villager dead : List.of(primary, nearby))
            h.assertTrue(dead.getLastDamageSource().is(DamageTypes.PLAYER_ATTACK) && dead.getKillCredit() == player,
                    "Kills retain the player's normal sword damage type and kill credit");
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 80)
    public static void normalBlockActionsWinEvenWhileTheSkillIsOnCooldown(GameTestHelper h) {
        List<EmbeddedChannel> channels = new ArrayList<>();
        ServerPlayer player = realPlayer(h, "shredder-blocks", channels);
        try {
            clearArena(h, 180); equip(player);
            Vec3 start = h.absoluteVec(new Vec3(2.5, 180, 5.5));
            player.teleportTo(start.x, start.y, start.z); player.setYRot(-90); player.setXRot(0);
            BlockPos pos = h.absolutePos(new BlockPos(4, 180, 5));
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.WEST, pos, false);
            h.getLevel().setBlock(pos.below(), Blocks.STONE.defaultBlockState(), 3);
            h.getLevel().setBlock(pos, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR), 3);
            InteractionResult result = player.gameMode.useItemOn(player, h.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            h.assertTrue(result.consumesAction() && h.getLevel().getBlockState(pos).getValue(LeverBlock.POWERED), "A normal lever click toggles power");
            h.assertTrue(player.position().equals(start) && !player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem()),
                    "A handled block interaction neither teleports nor starts the skill cooldown");

            player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), 60);
            player.gameMode.useItemOn(player, h.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            h.assertTrue(!h.getLevel().getBlockState(pos).getValue(LeverBlock.POWERED), "The skill cooldown does not block a second lever click");
            h.getLevel().setBlock(pos, Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
            h.getLevel().setBlock(pos.above(), Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
            result = player.gameMode.useItemOn(player, h.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            h.assertTrue(result.consumesAction() && h.getLevel().getBlockState(pos).getValue(DoorBlock.OPEN), "A door opens while the full set and cooldown are active");
            h.getLevel().setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
            h.getLevel().setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
            result = player.gameMode.useItemOn(player, h.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            h.assertTrue(result.consumesAction() && player.containerMenu != player.inventoryMenu, "The actual server block-use path opens a chest menu");
            h.assertTrue(player.position().equals(start), "Opening menus and doors leaves the wearer in place");
            player.closeContainer(); ready(player);

            h.getLevel().setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
            h.getLevel().setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
            h.getLevel().setBlock(pos.above(2), Blocks.STONE.defaultBlockState(), 3);
            result = player.gameMode.useItemOn(player, h.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
            h.assertTrue(result == InteractionResult.PASS && player.position().equals(start), "An inert block leaves the click unhandled");
            result = player.gameMode.useItem(player, h.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND);
            h.assertTrue(result.consumesAction() && player.getX() > start.x && player.getBoundingBox().maxX < pos.getX(),
                    "The ordinary item-use fallback still warps up to a solid block");
        } finally {
            player.closeContainer(); player.discard();
            for (EmbeddedChannel channel : channels) channel.finishAndReleaseAll();
        }
        h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void normalEntityInteractionOpensTradingWithoutTeleporting(GameTestHelper h) {
        Player player = player(h, 175); Villager villager = target(h, 4, 175, 5);
        villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.FARMER));
        h.assertTrue(!villager.getOffers().isEmpty(), "The interaction fixture has real trades");
        Vec3 start = player.position();
        PlayerInteractEvent.EntityInteractSpecific event = new PlayerInteractEvent.EntityInteractSpecific(player, InteractionHand.MAIN_HAND, villager, Vec3.ZERO);
        MinecraftForge.EVENT_BUS.post(event);
        h.assertTrue(!event.isCanceled() && player.position().equals(start) && villager.getHealth() == 100,
                "The interact-at event is left to vanilla without a skill activation");
        InteractionResult result = player.interactOn(villager, InteractionHand.MAIN_HAND);
        h.assertTrue(result.consumesAction() && villager.getTradingPlayer() == player, "Normal entity interaction starts villager trading");
        h.assertTrue(player.position().equals(start) && villager.getHealth() == 100
                && !player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem()), "Trading does not teleport, hurt the villager or spend the skill cooldown");
        villager.setTradingPlayer(null); h.succeed();
    }

    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 60)
    public static void unhandledEntityInteractionStillAllowsTheTargetedSkill(GameTestHelper h) {
        Player player = player(h, 185); Zombie zombie = h.spawn(EntityType.ZOMBIE, 8, 185, 5);
        zombie.setNoAi(true); zombie.setNoGravity(true);
        for (EquipmentSlot slot : EquipmentSlot.values()) if (slot.isArmor()) zombie.setItemSlot(slot, ItemStack.EMPTY);
        zombie.getAttribute(Attributes.ARMOR).setBaseValue(0);
        zombie.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100); zombie.setHealth(100);
        Vec3 start = player.position();
        h.assertTrue(player.interactOn(zombie, InteractionHand.MAIN_HAND) == InteractionResult.PASS && player.position().equals(start),
                "An entity with no ordinary sword interaction leaves the click unhandled");
        h.assertTrue(use(player).consumesAction() && zombie.getHealth() == 87
                && player.position().distanceTo(zombie.position().add(-2, 0, 0)) < .001,
                "The item-use fallback retains the targeted teleport and thirteen-damage strike");
        h.succeed();
    }

    private static ServerPlayer realPlayer(GameTestHelper h, String name, List<EmbeddedChannel> channels) {
        var server = h.getLevel().getServer();
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        ServerPlayer player = new ServerPlayer(server, h.getLevel(), profile);
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        EmbeddedChannel channel = new EmbeddedChannel(connection); channels.add(channel);
        new ServerGamePacketListenerImpl(server, connection, player);
        h.getLevel().addNewPlayer(player); player.setGameMode(GameType.SURVIVAL); player.setNoGravity(true);
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); player.setHealth(200);
        // Advance the vanilla post-spawn grace period before testing permitted PvP damage.
        for (int tick = 0; tick < 61; tick++) player.tick();
        return player;
    }
    @GameTest(batch = "dimension_shredder_regression", template = "npc_empty", timeoutTicks = 80)
    public static void realPlayersTeleportWithPositionPacketsAndRespectPvpForDirectAndAreaHits(GameTestHelper h) {
        List<EmbeddedChannel> channels = new ArrayList<>();
        ServerPlayer player = realPlayer(h, "shredder-user", channels), other = realPlayer(h, "shredder-target", channels);
        var server = h.getLevel().getServer(); boolean pvp = server.isPvpAllowed();
        Vec3 start = h.absoluteVec(new Vec3(2.5, 190, 5.5)), goal = h.absoluteVec(new Vec3(8.5, 190, 5.5));
        try {
            clearArena(h, 190);
            equip(player); player.teleportTo(start.x, start.y, start.z); player.setYRot(-90); player.setXRot(0);
            other.teleportTo(goal.x, goal.y, goal.z);
            while (channels.get(0).readOutbound() != null) { /* discard initial packets */ }
            Villager protectedBystander = target(h, 8, 190, 7);
            server.setPvpAllowed(false); use(player);
            h.assertTrue(player.position().distanceTo(goal.add(-2, 0, 0)) < .001 && other.getHealth() == 200,
                    "PvP disabled still teleports to a player but applies no direct damage");
            h.assertTrue(protectedBystander.getHealth() == 100, "A protected player target means teleport only, without an area strike");
            protectedBystander.discard();
            int packets = 0; Object packet;
            while ((packet = channels.get(0).readOutbound()) != null) if (packet instanceof ClientboundPlayerPositionPacket) packets++;
            h.assertTrue(packets > 0, "A real server player receives the teleport correction");
            player.teleportTo(start.x, start.y, start.z); ready(player);
            other.teleportTo(goal.x, goal.y, goal.z + 2); Villager mob = target(h, 8, 190, 5); use(player);
            h.assertTrue(mob.getHealth() == 87 && other.getHealth() == 200, "PvP disabled also excludes players from the area hit");
            mob.discard(); player.teleportTo(start.x, start.y, start.z); ready(player); other.teleportTo(goal.x, goal.y, goal.z);
            server.setPvpAllowed(true); other.invulnerableTime = 0; use(player);
            h.assertTrue(other.getHealth() == 187, "PvP enabled allows the thirteen-damage player strike; health=" + other.getHealth());
        } finally {
            server.setPvpAllowed(pvp); player.discard(); other.discard();
            for (EmbeddedChannel channel : channels) channel.finishAndReleaseAll();
        }
        h.succeed();
    }
}
