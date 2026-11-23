package net.pm_equips.events;

import net.minecraft.nbt.CompoundTag;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.ClassEnergySupport;
import net.pm_equips.items.HeadExecutioner;
import net.pm_equips.items.WingKCorpArmor1;
import net.pm_equips.items.WingKCorpArmor2;
import net.pm_equips.items.WingRCorpArmor1;
import net.pm_equips.items.WingRCorpArmor2;
import net.pm_equips.items.WingRCorpArmor3;
import net.pm_equips.items.WingRCorpWeapon2;
import net.pm_equips.items.WingTCorpArmor1;
import net.pm_equips.items.WingTCorpArmor2;
import net.pm_equips.items.WingTCorpArmor3;
import net.pm_equips.items.WingWCorpArmor;
import net.pm_equips.util.ItemStackData;
import net.pm_equips.MobEffectInit;
import net.pm_equips.entity.MobNPC1;
import net.pm_equips.util.FingerEquipment;

@EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID
)
public class CombatEvents {
    private static final ResourceLocation ARMOR_DAMAGE = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "armor_weapon_damage");
    private static final ResourceLocation ARMOR_SPEED = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "armor_weapon_speed");
    private static final ResourceLocation TIME_SLOW = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "tcorp_slow");
    private static final Map<ServerLevel, Set<LivingEntity>> TIME_SOURCES = new WeakHashMap<>();

    public static boolean activateArmor(Player player) {
        if (!player.isAlive() || player.isSpectator() || player.level().isClientSide) return false;
        ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
        if (stack.getItem() instanceof WingTCorpArmor1 || stack.getItem() instanceof WingTCorpArmor2
                || stack.getItem() instanceof WingTCorpArmor3) {
            long day = Math.floorDiv(player.level().getDayTime(), 24000);
            String key = Math.floorMod(player.level().getDayTime(), 24000) < 12000 ? "TCorpUsedDay" : "TCorpUsedNight";
            CompoundTag persistent = player.getPersistentData().getCompound("PlayerPersisted");
            if (persistent.contains(key) && persistent.getLong(key) == day) return false;
            persistent.putLong(key, day); player.getPersistentData().put("PlayerPersisted", persistent);
            player.addEffect(new MobEffectInstance(MobEffectInit.TCORP_SIN, 12000, 0));
            return true;
        }
        int cost;
        Holder<MobEffect> effect;
        int duration = 12000;
        if (stack.getItem() instanceof WingWCorpArmor) { cost = 1000; effect = MobEffectInit.WCORP_SIN; }
        else if (stack.getItem() instanceof WingRCorpArmor1 || stack.getItem() instanceof WingRCorpArmor2
                || stack.getItem() instanceof WingRCorpArmor3) {
            cost = 2000; effect = MobEffectInit.RCORP_SIN;
            if (stack.getItem() instanceof WingRCorpArmor3) duration = 36000;
        } else if (stack.getItem() instanceof WingKCorpArmor2 || stack.getItem() instanceof WingKCorpArmor1) {
            cost = 1500; effect = MobEffectInit.KCORP_SIN;
        } else return false;
        var energy = ClassEnergySupport.energy(stack).orElse(null);
        if (energy == null || energy.extractEnergy(cost, true) != cost) return false;
        energy.extractEnergy(cost, false);
        player.addEffect(new MobEffectInstance(effect, duration, 0));
        return true;
    }

    public static boolean isHostile(LivingEntity source, LivingEntity target) {
        if (source == target || target.isSpectator() || source.isAlliedTo(target)) return false;
        if (source instanceof MobNPC1 npc) return npc.canAttack(target);
        if (source instanceof Player player && FingerEquipment.isAlly(player, target)) return false;
        if (target instanceof Player player)
            return !player.isCreative() && player.getServer().isPvpAllowed();
        return target instanceof Enemy || target instanceof Mob mob && mob.getTarget() == source;
    }

    @SubscribeEvent public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!(entity.level() instanceof ServerLevel level)) return;
        ItemStack armor = entity.getItemBySlot(EquipmentSlot.LEGS);
        boolean unarmed = HeadExecutioner.unarmed(entity);
        int damage = unarmed && armor.getItem() instanceof HeadExecutioner ? 14
                : unarmed && armor.getItem() instanceof WingTCorpArmor1 ? 9 : 0;
        double speed = armor.getItem() instanceof HeadExecutioner ? 2 : 1.5;
        setModifier(entity, Attributes.ATTACK_DAMAGE, ARMOR_DAMAGE,
                damage == 0 || entity.getAttribute(Attributes.ATTACK_DAMAGE) == null ? 0 : damage - entity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), AttributeModifier.Operation.ADD_VALUE);
        setModifier(entity, Attributes.ATTACK_SPEED, ARMOR_SPEED,
                damage == 0 || entity.getAttribute(Attributes.ATTACK_SPEED) == null ? 0 : speed - entity.getAttributeBaseValue(Attributes.ATTACK_SPEED), AttributeModifier.Operation.ADD_VALUE);
        Set<LivingEntity> sources = TIME_SOURCES.computeIfAbsent(level, unused -> new HashSet<>());
        if (entity.isAlive() && entity.hasEffect(MobEffectInit.TCORP_SIN)) sources.add(entity);
        else sources.remove(entity);
        sources.removeIf(source -> source.isRemoved() || !source.isAlive() || source.level() != level
                || !source.hasEffect(MobEffectInit.TCORP_SIN));
        boolean slowed = sources.stream().anyMatch(source -> source.distanceToSqr(entity) <= 1024 && isHostile(source, entity));
        setModifier(entity, Attributes.MOVEMENT_SPEED, TIME_SLOW, slowed ? -.2 : 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    private static void setModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier old = instance.getModifier(id);
        if (old != null && old.amount() == amount) return;
        instance.removeModifier(id);
        if (amount != 0) instance.addTransientModifier(new AttributeModifier(id, amount, operation));
    }
    @SubscribeEvent public static void onArmorMelee(LivingDamageEvent.Pre event) {
        if (event.getNewDamage() <= 0 || event.getSource().getEntity() != event.getSource().getDirectEntity()
                || !(event.getSource().getEntity() instanceof LivingEntity attacker) || !HeadExecutioner.unarmed(attacker)) return;
        ItemStack armor = attacker.getItemBySlot(EquipmentSlot.LEGS);
        if (armor.getItem() instanceof WingTCorpArmor1)
            armor.hurtAndBreak(1, attacker, EquipmentSlot.LEGS);
    }
    @SubscribeEvent public static void onServerStopped(ServerStoppedEvent event) { TIME_SOURCES.clear(); }

    private static final String LAST_ATTACK_KEY =
            "LastEnergyAttackTick";

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDamage(
            LivingDamageEvent.Pre event)
    {
        if (!(event.getSource().getEntity()
                instanceof Player player))
        {
            return;
        }

        if (player instanceof FakePlayer)
        {
            return;
        }

        ItemStack weapon =
                player.getMainHandItem();

        if (!(weapon.getItem()
                instanceof WingRCorpWeapon2))
        {
            return;
        }

        if (!WingRCorpWeapon2
                .hasEnoughEnergy(weapon))
        {
            return;
        }

        CompoundTag tag =
                ItemStackData.copy(weapon);

        long currentTick =
                player.level().getGameTime();

        long lastTick =
                tag.getLong(
                        "LastEnergyAttackTick");

        if (lastTick == currentTick)
        {
            return;
        }

        tag.putLong(
                "LastEnergyAttackTick",
                currentTick);
        ItemStackData.set(weapon, tag);

        event.setNewDamage(
                event.getNewDamage() * 2.0F);

        WingRCorpWeapon2
                .consumeEnergy(weapon);
    }
}
