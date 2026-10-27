package net.pm_equips.entity;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.registration.ChannelAttributes;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.SoundInit;
import net.pm_equips.items.HeadArbiterArmor;
import net.pm_equips.network.ArbiterArmorPacket;
import net.pm_equips.network.SyncEGORecipesPacket;
import net.pm_equips.network.NetworkPacketInit;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class ArbiterArmorTests {
    private static void equip(GameTestHelper h, Player player) {
        player.setPos(h.absoluteVec(new Vec3(2.5, 2, 5.5)));
        player.setYRot(-90); player.setXRot(0); player.setNoGravity(true);
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemInit.HEAD_ARBITER_ARMOR.get()));
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        for (int y = 2; y < 7; y++) for (int z = 4; z <= 6; z++) h.setBlock(new BlockPos(13, y, z), Blocks.STONE);
    }

    private static Player wearer(GameTestHelper h) {
        Player player = h.makeMockPlayer(GameType.SURVIVAL); equip(h, player); return player;
    }

    private static Villager target(GameTestHelper h, int x) {
        Villager target = h.spawn(EntityType.VILLAGER, x, 2, 5);
        target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); target.setHealth(200);
        return target;
    }

    private static void tap(Player player) {
        ArbiterArmorPacket.process(player, ArbiterArmorPacket.START);
        ArbiterArmorPacket.process(player, ArbiterArmorPacket.RELEASE);
    }

    private static List<PArbiterPillar> pillars(GameTestHelper h, Player owner) {
        return h.getLevel().getEntitiesOfClass(PArbiterPillar.class, new AABB(owner.position(), owner.position()).inflate(160),
                pillar -> pillar.getOwner() == owner);
    }

    private static void keepAlive(GameTestHelper h, Player... players) {
        for (int tick : new int[]{10, 20, 30, 40}) h.runAfterDelay(tick, () -> {
            for (Player player : players) ArbiterArmorPacket.process(player, ArbiterArmorPacket.KEEP_ALIVE);
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void armorHasRealFiftyStatsAndNeverBreaks(GameTestHelper h) {
        Player player = wearer(h); ItemStack armor = player.getItemBySlot(EquipmentSlot.LEGS);
        HeadArbiterArmor item = (HeadArbiterArmor) armor.getItem();
        h.assertTrue(item.getEquipmentSlot() == EquipmentSlot.LEGS && item.getDefense() == 50 && item.getToughness() == 50,
                "Armor declares the leg slot and fifty defense/toughness");
        player.tick();
        h.assertTrue(player.getArmorValue() == 50 && player.getAttributeValue(Attributes.ARMOR_TOUGHNESS) == 50,
                "Equipped attributes are actually fifty instead of being clamped by vanilla");
        h.assertTrue(player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) == 1
                && player.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE) == 1, "Both knockback resistances reach 100 percent");
        Vec3 motion = player.getDeltaMovement(); player.knockback(2, 1, 0);
        h.assertTrue(player.getDeltaMovement().equals(motion), "Melee knockback has no effect");
        float before = player.getHealth();
        boolean damaged = player.hurt(player.damageSources().playerAttack(wearer(h)), 20);
        h.assertTrue(damaged && Math.abs(before - player.getHealth() - 4) < .001,
                "Armor reduces twenty damage to four: before=" + before + ", after=" + player.getHealth() + ", accepted=" + damaged);
        armor.hurtAndBreak(100000, player, EquipmentSlot.LEGS);
        h.assertTrue(!armor.isDamageableItem() && armor.has(DataComponents.UNBREAKABLE) && armor.getCount() == 1 && armor.getDamageValue() == 0,
                "The item is unbreakable and cannot accumulate durability damage");
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY); player.setItemSlot(EquipmentSlot.MAINHAND, armor); player.tick();
        h.assertTrue(player.getArmorValue() == 0 && player.getAttributeValue(Attributes.ARMOR_TOUGHNESS) == 0,
                "Holding the armor does not leave equipped defense modifiers behind");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void tapFiresOneEightDamageFairyShot(GameTestHelper h) {
        Player player = wearer(h); Villager near = target(h, 7), far = target(h, 10);
        tap(player);
        h.assertTrue(near.getHealth() == 192 && far.getHealth() == 200, "A single tap deals eight damage to only the nearest target");
        var fairy = near.getEffect(MobEffectInit.FAIRY);
        h.assertTrue(fairy != null && fairy.getAmplifier() == 0 && fairy.getDuration() == 60, "Fairy I lasts exactly three seconds");
        h.assertTrue(pillars(h, player).isEmpty(), "A tap never also creates a pillar");
        near.invulnerableTime = 0; tap(player);
        h.assertTrue(near.getHealth() == 192, "Cooldown rejects repeated fire requests");
        player.getCooldowns().removeCooldown(ItemInit.HEAD_ARBITER_ARMOR.get());
        ArbiterArmorPacket.process(player, ArbiterArmorPacket.RELEASE);
        h.assertTrue(near.getHealth() == 192, "A release without a matching press cannot fire");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void fairyShotCannotHitThroughWalls(GameTestHelper h) {
        Player player = wearer(h); Villager target = target(h, 8);
        for (int y = 2; y < 6; y++) h.setBlock(new BlockPos(5, y, 5), Blocks.STONE);
        tap(player);
        h.assertTrue(target.getHealth() == 200 && !target.hasEffect(MobEffectInit.FAIRY), "Blocks stop both damage and debuff before an entity behind them");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void fortyTicksSwitchFromFairyToOnePillar(GameTestHelper h) {
        Player early = wearer(h), charged = wearer(h); charged.setXRot(-90);
        Villager target = target(h, 7);
        ArbiterArmorPacket.process(early, ArbiterArmorPacket.START);
        ArbiterArmorPacket.process(charged, ArbiterArmorPacket.START);
        keepAlive(h, early, charged);
        h.runAfterDelay(39, () -> {
            ArbiterArmorPacket.process(early, ArbiterArmorPacket.RELEASE);
            h.assertTrue(target.getHealth() == 192 && pillars(h, early).isEmpty(), "Releasing at thirty-nine ticks still uses the fairy shot");
        });
        h.runAfterDelay(40, () -> {
            ArbiterArmorPacket.process(charged, ArbiterArmorPacket.RELEASE);
            List<PArbiterPillar> shots = pillars(h, charged);
            h.assertTrue(shots.size() == 1, "Forty ticks produces exactly one pillar");
            PArbiterPillar pillar = shots.getFirst();
            h.assertTrue(pillar.getDeltaMovement().normalize().distanceTo(charged.getLookAngle()) < .00001
                    && Math.abs(pillar.getDeltaMovement().length() - 2) < .00001, "Pillar launches along the current view direction at speed two");
            h.assertTrue(pillar.position().distanceTo(charged.getEyePosition().add(charged.getLookAngle().scale(.35))) < .00001,
                    "Pillar is spawned directly in front of the viewpoint");
            ArbiterArmorPacket.process(charged, ArbiterArmorPacket.RELEASE);
            h.assertTrue(pillars(h, charged).size() == 1, "Repeated release packets do not duplicate the pillar");
            pillar.discard(); h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void equipmentReplacementAndCancelCannotFireStoredCharges(GameTestHelper h) {
        Player replaced = wearer(h), canceled = wearer(h), removed = wearer(h);
        Villager target = target(h, 7);
        for (Player player : List.of(replaced, canceled, removed)) ArbiterArmorPacket.process(player, ArbiterArmorPacket.START);
        keepAlive(h, replaced, canceled, removed);
        h.runAfterDelay(20, () -> {
            replaced.setItemSlot(EquipmentSlot.LEGS, replaced.getItemBySlot(EquipmentSlot.LEGS).copy());
            removed.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
            ArbiterArmorPacket.process(canceled, ArbiterArmorPacket.CANCEL);
        });
        h.runAfterDelay(40, () -> {
            for (Player player : List.of(replaced, canceled, removed)) {
                ArbiterArmorPacket.process(player, ArbiterArmorPacket.RELEASE);
                h.assertTrue(pillars(h, player).isEmpty(), "Canceled and changed armor cannot retain an old charge");
            }
            h.assertTrue(target.getHealth() == 200, "Canceled releases do not fall back to fairy fire");
            h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void missingHeartbeatsAndLogoutExpireTheCharge(GameTestHelper h) {
        Player silent = wearer(h), loggedOut = wearer(h); Villager target = target(h, 7);
        ArbiterArmorPacket.process(silent, ArbiterArmorPacket.START);
        ArbiterArmorPacket.process(loggedOut, ArbiterArmorPacket.START);
        ArbiterArmorPacket.onLogout(new PlayerEvent.PlayerLoggedOutEvent(loggedOut));
        h.runAfterDelay(40, () -> {
            ArbiterArmorPacket.process(silent, ArbiterArmorPacket.RELEASE);
            ArbiterArmorPacket.process(loggedOut, ArbiterArmorPacket.RELEASE);
            h.assertTrue(target.getHealth() == 200 && pillars(h, silent).isEmpty() && pillars(h, loggedOut).isEmpty(),
                    "Disconnected or silent clients cannot fire a stored charge");
            h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void bothHandsAndLegArmorAreRequired(GameTestHelper h) {
        Player player = wearer(h); Villager target = target(h, 7);
        for (EquipmentSlot slot : List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)) {
            player.setItemSlot(slot, new ItemStack(Items.STICK)); tap(player);
            player.setItemSlot(slot, ItemStack.EMPTY); ArbiterArmorPacket.process(player, ArbiterArmorPacket.RELEASE);
        }
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY); tap(player);
        h.assertTrue(target.getHealth() == 200 && !target.hasEffect(MobEffectInit.FAIRY), "Items in either hand or missing leg armor prevent attacks");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void fairyRespectsCompanionOwnership(GameTestHelper h) {
        Player player = wearer(h);
        MobNPC1 npc = h.spawn(EntityInit.MOB_NPC_1.get(), 7, 2, 5); npc.setNoAi(true); npc.setNoGravity(true);
        npc.setOwnerUUID(player.getUUID()); npc.setTame(true, false);
        tap(player);
        h.assertTrue(npc.getHealth() == 20 && !npc.hasEffect(MobEffectInit.FAIRY), "The fairy attack cannot damage or debuff its owner's companion");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 100)
    public static void soundsAndSingleTrailPayloadReachAConnectedPlayer(GameTestHelper h) {
        var server = h.getLevel().getServer();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "arbiter-test"), false);
        ServerPlayer player = new ServerPlayer(server, h.getLevel(), cookie.gameProfile(), cookie.clientInformation());
        Connection connection = new Connection(PacketFlow.SERVERBOUND); EmbeddedChannel channel = new EmbeddedChannel(connection);
        for (String name : List.of("sync_curios", "sync_data", "sync_modifiers", "sync_render", "sync_stack", "sync_active", "set_icons")) {
            ChannelAttributes.getOrCreateAdHocChannels(connection).add(ResourceLocation.fromNamespaceAndPath("curios", name));
        }
        ChannelAttributes.getOrCreateAdHocChannels(connection).add(ArbiterArmorPacket.FairyTrail.TYPE.id());
        ChannelAttributes.getOrCreateAdHocChannels(connection).add(NetworkPacketInit.FingerShotTrail.TYPE.id());
        ChannelAttributes.getOrCreateAdHocChannels(connection).add(MobNPC3.SkinCatalog.TYPE.id());
        ChannelAttributes.getOrCreateAdHocChannels(connection).add(SyncEGORecipesPacket.TYPE.id());
        server.getPlayerList().placeNewPlayer(connection, player, cookie);
        player.setGameMode(GameType.SURVIVAL); equip(h, player); target(h, 7);
        while (channel.readOutbound() != null) { }
        List<Object> packets = new ArrayList<>();
        tap(player);
        Object first;
        while ((first = channel.readOutbound()) != null) packets.add(first);
        h.assertTrue(packets.stream().filter(p -> p instanceof ClientboundCustomPayloadPacket payload
                        && payload.payload() instanceof ArbiterArmorPacket.FairyTrail).count() == 1,
                "The complete fairy trail uses a single client payload");
        player.getCooldowns().removeCooldown(ItemInit.HEAD_ARBITER_ARMOR.get());
        ArbiterArmorPacket.process(player, ArbiterArmorPacket.START); keepAlive(h, player);
        h.runAfterDelay(5, () -> ArbiterArmorPacket.onPlayerTick(new PlayerTickEvent.Post(player)));
        h.runAfterDelay(6, () -> ArbiterArmorPacket.onPlayerTick(new PlayerTickEvent.Post(player)));
        h.runAfterDelay(40, () -> {
            try {
                ArbiterArmorPacket.process(player, ArbiterArmorPacket.RELEASE);
                List<PArbiterPillar> shots = pillars(h, player);
                h.assertTrue(shots.size() == 1, "Connected player fires a pillar after charging");
                PArbiterPillar pillar = shots.getFirst();
                for (int i = 0; i < 10 && !pillar.isEmbedded(); i++) pillar.tick();
                // The server batches outbound packets until the end of the tick.
                connection.flushChannel();
                Object packet; while ((packet = channel.readOutbound()) != null) packets.add(packet);
                List<ResourceLocation> sounds = packets.stream().filter(ClientboundSoundPacket.class::isInstance)
                        .map(ClientboundSoundPacket.class::cast).map(p -> p.getSound().value().getLocation()).toList();
                h.assertTrue(sounds.stream().filter(SoundInit.ARBITER_FAIRY.get().getLocation()::equals).count() == 1, "A tap plays the fairy firing sound once");
                h.assertTrue(sounds.stream().filter(SoundInit.ARBITER_PILLAR_START.get().getLocation()::equals).count() == 1, "Charge sound is played once per hold");
                h.assertTrue(sounds.stream().filter(SoundInit.ARBITER_PILLAR_SHOT.get().getLocation()::equals).count() == 1,
                        "Pillar firing sound is played once: " + sounds);
                h.assertTrue(sounds.contains(SoundInit.ARBITER_PILLAR_END.get().getLocation()), "The pillar impact broadcasts its end sound");
                pillar.discard(); h.succeed();
            } finally {
                ArbiterArmorPacket.process(player, ArbiterArmorPacket.CANCEL);
                server.getPlayerList().remove(player); channel.finishAndReleaseAll();
            }
        });
    }
}
