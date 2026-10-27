package net.pm_equips.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.items.Finger2WeaponSP;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class Finger2WeaponSPTests {
    private static Player wielder(GameTestHelper h) {
        Player player = h.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(h.absoluteVec(new Vec3(2.5, 2, 2.5)));
        player.setNoGravity(true); player.getFoodData().setFoodLevel(10);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemInit.FINGER2_WEAPON_SP_CADUCEUS.get()));
        player.tick();
        return player;
    }

    private static Villager target(GameTestHelper h) {
        Villager target = h.spawn(EntityType.VILLAGER, 5, 2, 5);
        target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000); target.setHealth(1000);
        return target;
    }

    private static void next(Player player) {
        ItemStack stack = player.getMainHandItem();
        stack.getItem().use(player.level(), player, InteractionHand.MAIN_HAND);
        player.releaseUsingItem();
    }

    private static void choose(Player player, int form) {
        for (int i = 0; i < 10 && Finger2WeaponSP.form(player.getMainHandItem()) != form; i++) next(player);
    }

    private static void hit(Player player, Villager target) {
        target.hurt(player.damageSources().playerAttack(player), 1);
    }

    private static void checkEffect(GameTestHelper h, Player player, Holder<MobEffect> type, int amplifier) {
        var effect = player.getEffect(type);
        h.assertTrue(effect != null && effect.getAmplifier() == amplifier, "Expected effect " + type + " with amplifier " + amplifier);
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void allNineFormsHaveRealAttributesAndKeepTheSameStack(GameTestHelper h) {
        Player player = wielder(h); ItemStack stack = player.getMainHandItem();
        stack.setDamageValue(123); stack.set(DataComponents.CUSTOM_NAME, Component.literal("named-caduceus"));
        double[] damages = {1, 10, 14, 6, 11, 9, 9, 8, 12, 17}; // Longsword includes its Power II.
        double[] speeds = {4, 1.6, .8, 1.8, 1.2, 1.1, 2, .7, 1.5, 1.9};
        h.assertTrue(stack.getMaxDamage() == 4000 && Finger2WeaponSP.form(stack) == 0, "Normal state has durability 4000");
        for (int form = 0; form <= 9; form++) {
            if (form != 0) next(player);
            h.assertTrue(Finger2WeaponSP.form(stack) == form, "Every click advances one form");
            h.assertTrue(Math.abs(player.getAttributeValue(Attributes.ATTACK_DAMAGE) - damages[form]) < .001, "Attack damage of form " + form);
            h.assertTrue(Math.abs(player.getAttributeValue(Attributes.ATTACK_SPEED) - speeds[form]) < .001, "Attack speed of form " + form);
            h.assertTrue(Math.abs(player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) - (form == 8 ? 13 : form == 9 ? 6 : 3)) < .001,
                    "Only forms eight/nine extend entity reach");
            h.assertTrue(player.hasEffect(MobEffectInit.POWER) == (form == 1) && player.hasEffect(MobEffectInit.ENDURANCE) == (form == 2)
                    && player.hasEffect(MobEffectInit.QUICK) == (form == 3), "Passives are limited to their form");
            h.assertTrue(player.getMainHandItem() == stack && stack.getDamageValue() == 123 && stack.getHoverName().getString().equals("named-caduceus"),
                    "Form changes keep the same item, damage and custom name");
        }
        next(player); h.assertTrue(Finger2WeaponSP.form(stack) == 1, "The ninth form cycles to the first while held");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY); Finger2WeaponSP.syncHeld(player); player.tick();
        h.assertTrue(Finger2WeaponSP.form(stack) == 0 && player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 1
                && player.getAttributeValue(Attributes.ATTACK_SPEED) == 4 && player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) == 3,
                "Putting away the weapon removes its form, buffs and attributes");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void nativeMeleeIgnoresOnlyItsOwnIframesAndUsesDurability(GameTestHelper h) {
        Player player = wielder(h); next(player); Villager target = target(h);
        for (int i = 0; i < 20; i++) player.tick();
        target.invulnerableTime = 100;
        float before = target.getHealth(); player.attack(target); float afterFirst = target.getHealth(); player.attack(target);
        h.assertTrue(afterFirst < before && target.getHealth() < afterFirst, "Native consecutive attacks both damage through existing I-frames");
        h.assertTrue(player.getMainHandItem().getDamageValue() == 2, "Each successful native attack costs one durability");
        h.assertTrue(target.invulnerableTime >= 20, "The target retains protection from other weapons");
        Player other = h.makeMockPlayer(GameType.SURVIVAL); float beforeOther = target.getHealth();
        target.hurt(other.damageSources().playerAttack(other), .01f);
        h.assertTrue(target.getHealth() == beforeOther, "An unrelated weapon still obeys I-frames");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void threeSixAndNineDistinctFormsUnlockAndPuttingAwayResets(GameTestHelper h) {
        Player player = wielder(h); ItemStack stack = player.getMainHandItem(); Villager target = target(h);
        for (int form = 1; form <= 9; form++) {
            next(player); hit(player, target); hit(player, target);
            int level = form / 3;
            h.assertTrue(Finger2WeaponSP.unlockedLevel(stack) == level, "Repeated hits count each distinct form once: " + form);
            if (level == 0) h.assertTrue(!player.hasEffect(MobEffectInit.UNLOCKED), "First two forms grant no Unlocked");
            else checkEffect(h, player, MobEffectInit.UNLOCKED, level - 1);
        }
        next(player); hit(player, target);
        h.assertTrue(Finger2WeaponSP.unlockedLevel(stack) == 3, "Repeating an already hit form never exceeds Unlocked III");
        Player other = wielder(h); next(other);
        h.assertTrue(!other.hasEffect(MobEffectInit.UNLOCKED), "Players never share progress");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK)); Finger2WeaponSP.syncHeld(player);
        h.assertTrue(Finger2WeaponSP.form(stack) == 0 && Finger2WeaponSP.unlockedLevel(stack) == 0 && !player.hasEffect(MobEffectInit.UNLOCKED),
                "Putting away removes progress and the Unlocked buff immediately");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void allFiveDebuffsScaleAndMissesResetTheNextHit(GameTestHelper h) {
        Player player = wielder(h); Villager target = target(h);
        for (int form = 4; form <= 8; form++) {
            choose(player, form);
            Holder<MobEffect> type = switch (form) {
                case 4 -> MobEffectInit.BIND; case 5 -> MobEffectInit.BLEED; case 6 -> MobEffectInit.WEAK;
                case 7 -> MobEffectInit.PARALYSIS; default -> MobEffectInit.DISARM;
            };
            int base = form == 5 || form == 7 ? 100 : 200;
            for (int count = 1; count <= 3; count++) {
                hit(player, target); var effect = target.getEffect(type);
                h.assertTrue(effect != null && effect.getAmplifier() == count - 1 && effect.getDuration() == base * count,
                        "Debuff strength and duration scale on form " + form + ", hit " + count);
            }
            Finger2WeaponSP.resetCombo(player); target.removeEffect(type); hit(player, target);
            h.assertTrue(target.getEffect(type).getAmplifier() == 0 && target.getEffect(type).getDuration() == base,
                    "The first hit after a miss uses base strength and duration");
        }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void rapierMissFailedAttackAndHandSwitchResetQuick(GameTestHelper h) {
        Player player = wielder(h); choose(player, 3); Villager target = target(h);
        checkEffect(h, player, MobEffectInit.QUICK, 0);
        hit(player, target); hit(player, target); checkEffect(h, player, MobEffectInit.QUICK, 2);
        Finger2WeaponSP.resetCombo(player); checkEffect(h, player, MobEffectInit.QUICK, 0);
        hit(player, target); target.setInvulnerable(true); player.attack(target);
        Finger2WeaponSP.onPlayerTick(new PlayerTickEvent.Post(player)); checkEffect(h, player, MobEffectInit.QUICK, 0);
        target.setInvulnerable(false); hit(player, target);
        ItemStack stack = player.getMainHandItem();
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY); player.setItemInHand(InteractionHand.OFF_HAND, stack);
        Finger2WeaponSP.syncHeld(player);
        h.assertTrue(Finger2WeaponSP.form(stack) == 3, "Moving between hands retains the held form");
        checkEffect(h, player, MobEffectInit.QUICK, 0);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY); Finger2WeaponSP.syncHeld(player);
        h.assertTrue(!player.hasEffect(MobEffectInit.QUICK) && Finger2WeaponSP.form(stack) == 0, "Leaving both hands resets the form");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void executionThresholdsRespectProtectedCompanions(GameTestHelper h) {
        Player player = wielder(h);
        for (int form : new int[]{4, 9}) {
            choose(player, form); Villager above = target(h), below = target(h);
            float threshold = form == 4 ? 100 : 250;
            above.setHealth(threshold + 2); below.setHealth(threshold + 1);
            hit(player, above); hit(player, below);
            h.assertTrue(above.isAlive() && !below.isAlive(), "Execution checks remaining HP at " + (form == 4 ? 10 : 25) + " percent");
        }
        MobNPC1 companion = h.spawn(EntityInit.MOB_NPC_1.get(), 8, 2, 5);
        companion.setOwnerUUID(player.getUUID()); companion.setTame(true, false); companion.setHealth(1);
        companion.hurt(player.damageSources().playerAttack(player), 1);
        h.assertTrue(companion.getHealth() == 1 && companion.isAlive(), "Execution cannot bypass companion protection");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void unlockedAttributesAndHealingRemainStableWhenRefreshed(GameTestHelper h) {
        Player player = wielder(h); player.setHealth(1);
        for (int amplifier = 0; amplifier <= 3; amplifier++) {
            player.removeEffect(MobEffectInit.UNLOCKED);
            player.addEffect(new MobEffectInstance(MobEffectInit.UNLOCKED, 200, amplifier));
            h.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 1 + 2 * (amplifier + 1)
                    && player.getArmorValue() == 2 * (amplifier + 1), "Unlocked attributes scale by two for every amplifier");
        }
        player.removeEffect(MobEffectInit.UNLOCKED);
        player.tickCount = 0;
        for (int tick = 1; tick <= 180; tick++) {
            player.tickCount = tick; // ServerLevel normally advances entity age before calling tick().
            player.addEffect(new MobEffectInstance(MobEffectInit.UNLOCKED, 12, 2)); player.tick();
            h.assertTrue(player.getHealth() == 1 + 3 * (tick / 60), "Refreshing the effect does not accelerate or delay three-second healing at tick " + tick);
        }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void existingBuffsSurviveWeaponChanges(GameTestHelper h) {
        Player player = wielder(h);
        player.addEffect(new MobEffectInstance(MobEffectInit.POWER, 600, 0)); next(player);
        checkEffect(h, player, MobEffectInit.POWER, 1); next(player);
        checkEffect(h, player, MobEffectInit.POWER, 0);
        h.assertTrue(player.getEffect(MobEffectInit.POWER).getDuration() == 600, "An existing weaker buff is restored when leaving the form");
        player.addEffect(new MobEffectInstance(MobEffectInit.QUICK, 600, 4)); next(player); next(player);
        checkEffect(h, player, MobEffectInit.QUICK, 4);
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void dropsContainerTransfersAndLogoutCannotStoreActiveForms(GameTestHelper h) {
        Player player = wielder(h); choose(player, 8); ItemStack stack = player.getMainHandItem();
        ItemStack serialized = ItemStack.parse(h.getLevel().registryAccess(), stack.save(h.getLevel().registryAccess())).orElseThrow();
        h.assertTrue(Finger2WeaponSP.form(serialized) == 8, "Item components preserve the visible form during synchronization");
        Player other = wielder(h); other.setItemInHand(InteractionHand.MAIN_HAND, serialized); Finger2WeaponSP.syncHeld(other);
        h.assertTrue(Finger2WeaponSP.form(serialized) == 0, "A different holder cannot inherit active progress");
        stack.getItem().onDroppedByPlayer(stack, player);
        h.assertTrue(Finger2WeaponSP.form(stack) == 0 && player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) == 3, "Dropping clears the form and reach");
        choose(player, 3); ItemStack carried = stack.copy();
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY); player.containerMenu.setCarried(carried);
        Finger2WeaponSP.onContainerClose(new PlayerContainerEvent.Close(player, player.containerMenu));
        h.assertTrue(Finger2WeaponSP.form(carried) == 0 && !player.hasEffect(MobEffectInit.QUICK), "Cursor and container transfers cannot retain the active form");
        player.containerMenu.setCarried(ItemStack.EMPTY); player.setItemInHand(InteractionHand.MAIN_HAND, stack); next(player);
        Finger2WeaponSP.onLogout(new PlayerEvent.PlayerLoggedOutEvent(player));
        h.assertTrue(Finger2WeaponSP.form(stack) == 0 && !player.hasEffect(MobEffectInit.POWER), "Logout removes the held form and its buffs");
        h.succeed();
    }
}
