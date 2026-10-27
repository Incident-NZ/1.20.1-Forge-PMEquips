package net.pm_equips.entity;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.MobEffectInit;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class PArbiterPillarTests {
    private static PArbiterPillar pillar(GameTestHelper h, Vec3 point, Vec3 motion) {
        PArbiterPillar pillar = EntityInit.P_ARBITER_PILLAR.get().create(h.getLevel());
        pillar.setPos(Vec3.atLowerCornerOf(h.absolutePos(BlockPos.ZERO)).add(point));
        pillar.shoot(motion.x, motion.y, motion.z, (float) motion.length(), 0);
        h.getLevel().addFreshEntity(pillar);
        return pillar;
    }

    private static Cow cow(GameTestHelper h, int x, int y, int z) {
        // The template floor is placed one block above the structure block used by GameTestHelper.
        Cow cow = h.spawn(EntityType.COW, x, y + 1, z);
        cow.setNoAi(true); cow.setNoGravity(true);
        cow.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); cow.setHealth(200);
        return cow;
    }

    private static void wall(GameTestHelper h) {
        for (int y = 1; y <= 5; y++) for (int z = 4; z <= 6; z++) h.setBlock(new BlockPos(10, y, z), Blocks.STONE);
    }

    private static PArbiterPillar capture(GameTestHelper h, LivingEntity target) {
        wall(h);
        PArbiterPillar pillar = pillar(h, new Vec3(4, 3, 5.5), new Vec3(2, 0, 0));
        for (int i = 0; i < 4 && !pillar.isEmbedded(); i++) pillar.tick();
        h.assertTrue(pillar.isEmbedded(), "The pillar embeds in the wall");
        h.assertTrue(target.getX() > h.absolutePos(new BlockPos(8, 0, 0)).getX()
                && target.getBoundingBox().maxX <= h.absolutePos(new BlockPos(10, 0, 0)).getX() + .001,
                "The target is pushed forwards and stopped by the wall; x=" + target.getX()
                        + ", maxX=" + target.getBoundingBox().maxX + ", wall=" + h.absolutePos(new BlockPos(10, 0, 0)).getX());
        return pillar;
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void straightFlightWithoutGravityOrDrag(GameTestHelper h) {
        for (Vec3 direction : new Vec3[]{new Vec3(1, 0, 0), new Vec3(-1, 0, 0),
                new Vec3(0, 1, 0), new Vec3(0, -1, 0), new Vec3(0, 0, 1),
                new Vec3(0, 0, -1), new Vec3(1, 1, 1).normalize()}) {
            // Forge's test world has terrain above the structure; fly above its surface.
            PArbiterPillar pillar = pillar(h, new Vec3(7, 160, 7), direction);
            Vec3 start = pillar.position();
            for (int i = 0; i < 4; i++) pillar.tick();
            h.assertTrue(pillar.position().distanceTo(start.add(direction.scale(4))) < 1.0E-5,
                    "Flight is straight: direction=" + direction + ", start=" + start + ", current=" + pillar.position() + ", embedded=" + pillar.isEmbedded() + ", removed=" + pillar.isRemoved());
            h.assertTrue(pillar.getDeltaMovement().distanceTo(direction) < 1.0E-5 && pillar.isNoGravity(), "No gravity or drag");
            double yaw = Math.toRadians(pillar.getYRot()), pitch = Math.toRadians(pillar.getXRot());
            Vec3 facing = new Vec3(Math.sin(yaw) * Math.cos(pitch), Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
            h.assertTrue(facing.distanceTo(direction) < 1.0E-5, "Projectile rotation follows the flight direction");
            pillar.discard();
        }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void rangeIsExactly128AndSurvivesSaving(GameTestHelper h) {
        PArbiterPillar original = pillar(h, new Vec3(7, 160, 7), new Vec3(0, 10, 0));
        Vec3 start = original.position();
        for (int i = 0; i < 12; i++) original.tick();
        h.assertTrue(!original.isRemoved() && Math.abs(original.getY() - start.y - 120) < .00001,
                "Still alive at 120 blocks: start=" + start + ", current=" + original.position() + ", embedded=" + original.isEmbedded() + ", removed=" + original.isRemoved());
        CompoundTag saved = original.saveWithoutId(new CompoundTag());
        PArbiterPillar copy = EntityInit.P_ARBITER_PILLAR.get().create(h.getLevel()); copy.load(saved);
        copy.tick();
        h.assertTrue(copy.isRemoved() && Math.abs(copy.getY() - start.y - 128) < .00001, "Range budget is saved and stops at exactly 128");
        original.discard();
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void impactDealsElevenOnceAndAppliesFairyThree(GameTestHelper h) {
        Cow target = cow(h, 5, 1, 5);
        PArbiterPillar pillar = pillar(h, new Vec3(2, 3, 5.5), new Vec3(4, 0, 0));
        pillar.tick();
        h.assertTrue(target.getHealth() == 189, "First contact deals exactly eleven damage");
        var fairy = target.getEffect(MobEffectInit.FAIRY.get());
        h.assertTrue(fairy != null && fairy.getAmplifier() == 2 && fairy.getDuration() == 100, "Fairy III lasts five seconds on impact");
        target.invulnerableTime = 0;
        pillar.tick();
        h.assertTrue(target.getHealth() == 189, "Repeated contact does not deal impact damage again");
        pillar.discard(); h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void wallCaptureExpiresAfterFiveSecondsAndReleasesWithFairySix(GameTestHelper h) {
        Cow target = cow(h, 7, 1, 5);
        PArbiterPillar pillar = capture(h, target);
        Vec3 anchor = target.position();
        target.setPos(anchor.add(-2, 1, 0)); target.setDeltaMovement(1, 1, 1);
        PArbiterPillar.afterLivingTick(target);
        h.assertTrue(target.position().distanceTo(anchor) < .00001 && target.getDeltaMovement().lengthSqr() == 0, "Captured targets cannot move");
        for (int i = 0; i < 99; i++) pillar.tick();
        h.assertTrue(!pillar.isRemoved(), "The embedded pillar remains for 99 ticks");
        pillar.tick();
        var fairy = target.getEffect(MobEffectInit.FAIRY.get());
        h.assertTrue(pillar.isRemoved() && fairy != null && fairy.getAmplifier() == 5 && fairy.getDuration() == 600,
                "At tick 100 the pillar disappears and gives Fairy VI for thirty seconds");
        target.setPos(anchor.add(-1, 0, 0));
        PArbiterPillar.afterLivingTick(target);
        h.assertTrue(target.position().distanceTo(anchor.add(-1, 0, 0)) < .00001 && target.isNoAi(),
                "Movement is released and AI settings are preserved");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void embeddedAuraUsesSphereAndRefreshesOnlyNearbyTargets(GameTestHelper h) {
        PArbiterPillar pillar = pillar(h, new Vec3(7, 3, 7), new Vec3(1, 0, 0));
        BlockPos stone = h.absolutePos(new BlockPos(8, 3, 7));
        h.setBlock(new BlockPos(8, 3, 7), Blocks.STONE);
        pillar.onHitBlock(new BlockHitResult(pillar.position(), Direction.WEST, stone, false));
        Cow near = cow(h, 9, 1, 7), far = cow(h, 11, 1, 7), diagonal = cow(h, 10, 1, 10);
        pillar.tick();
        h.assertTrue(near.hasEffect(MobEffectInit.FAIRY.get()) && !far.hasEffect(MobEffectInit.FAIRY.get()) && !diagonal.hasEffect(MobEffectInit.FAIRY.get()),
                "Aura reaches three blocks, excluding diagonal corners outside the sphere");
        near.removeEffect(MobEffectInit.FAIRY.get()); pillar.tick();
        h.assertTrue(near.hasEffect(MobEffectInit.FAIRY.get()), "Aura reapplies Fairy III throughout the embedded lifetime");
        pillar.discard();
        h.assertTrue(near.getEffect(MobEffectInit.FAIRY.get()).getAmplifier() == 2, "Aura-only victims do not receive the captivity reward");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void overlappingPillarsAndEarlyRemovalDoNotLeavePermanentRestraints(GameTestHelper h) {
        Cow target = cow(h, 7, 1, 5);
        PArbiterPillar first = capture(h, target);
        PArbiterPillar second = pillar(h, new Vec3(8, 3, 5.5), new Vec3(2, 0, 0));
        for (int i = 0; i < 3 && !second.isEmbedded(); i++) second.tick();
        h.assertTrue(second.isEmbedded(), "The second pillar embeds too");
        first.discard();
        Vec3 anchor = target.position(); target.setPos(anchor.add(-1, 0, 0));
        PArbiterPillar.afterLivingTick(target);
        h.assertTrue(target.position().distanceTo(anchor) < .00001 && target.getEffect(MobEffectInit.FAIRY.get()).getAmplifier() == 2,
                "One removed pillar does not release another pillar's restraint");
        second.discard();
        target.setPos(anchor.add(-1, 0, 0)); PArbiterPillar.afterLivingTick(target);
        h.assertTrue(target.position().distanceTo(anchor.add(-1, 0, 0)) < .00001
                && target.getEffect(MobEffectInit.FAIRY.get()).getAmplifier() == 5, "Removing the final pillar releases the target with Fairy VI");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 160)
    public static void unloadedRestraintsExpireAndMovingShootersDoNotDeflectTheShot(GameTestHelper h) {
        Cow target = cow(h, 7, 1, 5);
        PArbiterPillar pillar = capture(h, target);
        pillar.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        Player shooter = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL); shooter.setDeltaMovement(1, 1, 1);
        PArbiterPillar shot = new PArbiterPillar(h.getLevel(), shooter);
        shot.shootFromRotation(shooter, -90, 0, 0, 2, 0);
        h.assertTrue(shot.getDeltaMovement().distanceTo(new Vec3(0, 2, 0)) < .00001, "Shooter movement does not change the straight firing direction");
        h.runAfterDelay(101, () -> {
            PArbiterPillar.afterLivingTick(target);
            h.assertTrue(target.getEffect(MobEffectInit.FAIRY.get()).getAmplifier() == 5, "Unloaded restraints expire without a loaded pillar");
            h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 160)
    public static void nativeServerTicksKeepFairyDamageAndReleaseTheVictim(GameTestHelper h) {
        Cow target = cow(h, 7, 1, 5);
        PArbiterPillar pillar = capture(h, target);
        h.runAfterDelay(25, () -> h.assertTrue(target.getHealth() <= 186 && !pillar.isRemoved(),
                "Refreshing the aura does not suppress Fairy's periodic damage"));
        h.runAfterDelay(105, () -> {
            var fairy = target.getEffect(MobEffectInit.FAIRY.get());
            h.assertTrue(pillar.isRemoved() && fairy != null && fairy.getAmplifier() == 5 && fairy.getDuration() >= 590,
                    "Normal server ticks expire the pillar and automatically release its victim");
            Vec3 free = target.position().add(-1, 0, 0); target.setPos(free);
            PArbiterPillar.afterLivingTick(target);
            h.assertTrue(target.position().distanceTo(free) < .00001, "The tick subscriber clears the captivity state");
            h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void realServerPlayersReceivePositionCorrectionsAndRespectPvp(GameTestHelper h) {
        var server = h.getLevel().getServer();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "pillar-test");
        ServerPlayer player = new ServerPlayer(server, h.getLevel(), profile);
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        EmbeddedChannel channel = new EmbeddedChannel(connection);
        boolean pvp = server.isPvpAllowed();
        try {
            // A play connection is sufficient; login would require a separate mod-channel handshake.
            new ServerGamePacketListenerImpl(server, connection, player);
            h.getLevel().addNewPlayer(player);
            player.setGameMode(GameType.SURVIVAL); player.setNoGravity(true);
            Vec3 pos = h.absoluteVec(new Vec3(7.5, 2, 5.5)); player.teleportTo(pos.x, pos.y, pos.z);
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); player.setHealth(200);
            Player shooter = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
            wall(h); server.setPvpAllowed(false);
            PArbiterPillar protectedShot = pillar(h, new Vec3(4, 3, 5.5), new Vec3(2, 0, 0)); protectedShot.setOwner(shooter);
            for (int i = 0; i < 4 && !protectedShot.isEmbedded(); i++) protectedShot.tick();
            h.assertTrue(player.position().distanceTo(pos) < .00001 && !player.hasEffect(MobEffectInit.FAIRY.get()), "PvP-disabled players cannot be pushed or debuffed");
            protectedShot.discard(); server.setPvpAllowed(true);
            while (channel.readOutbound() != null) { /* discard login packets */ }
            PArbiterPillar shot = pillar(h, new Vec3(4, 3, 5.5), new Vec3(2, 0, 0)); shot.setOwner(shooter);
            for (int i = 0; i < 4 && !shot.isEmbedded(); i++) shot.tick();
            Vec3 anchor = player.position();
            h.assertTrue(anchor.x > pos.x + 1, "A PvP-enabled player is pushed toward the wall");
            player.setPos(anchor.add(-1, 1, 0));
            PArbiterPillar.afterPlayerTick(new TickEvent.PlayerTickEvent(TickEvent.Phase.END,player));
            h.assertTrue(player.position().distanceTo(anchor) < .00001, "The player tick subscriber enforces captivity");
            int positionPackets = 0; Object packet;
            while ((packet = channel.readOutbound()) != null) if (packet instanceof ClientboundPlayerPositionPacket) positionPackets++;
            h.assertTrue(positionPackets >= 2, "The server sends position corrections for both pushing and captivity");
            shot.discard();
            h.assertTrue(player.getEffect(MobEffectInit.FAIRY.get()).getAmplifier() == 5, "Captured players are released with Fairy VI");
        } finally {
            server.setPvpAllowed(pvp);
            player.discard();
            channel.finishAndReleaseAll();
        }
        h.succeed();
    }
}
