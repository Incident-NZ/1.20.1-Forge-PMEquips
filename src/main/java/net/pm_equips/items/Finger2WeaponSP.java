package net.pm_equips.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.ForgeMod;
import java.util.WeakHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.util.ItemStackData;
import net.pm_equips.util.FingerEquipment;

/** A single stack keeps its durability, enchantments and name across all nine forms. */
@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class Finger2WeaponSP extends Item {
    private static final String HITS = "CaduceusHits", FORMS = "CaduceusForms", OWNER = "CaduceusHolder";
    private static final double[] DAMAGE = {1, 8, 14, 6, 11, 9, 9, 8, 12, 17};
    private static final double[] SPEED = {4, 1.6, .8, 1.8, 1.2, 1.1, 2, .7, 1.5, 1.9};
    private static final UUID REACH = UUID.fromString("c3385522-2d71-4ecf-9c31-109897593393");
    private static final Supplier<Multimap<Attribute,AttributeModifier>[]> MODIFIERS = Suppliers.memoize(Finger2WeaponSP::createModifiers);
    private static final Map<LivingEntity, HeldState> HELD = new WeakHashMap<>();

    public Finger2WeaponSP() { super(new Properties().durability(4000).rarity(Rarity.EPIC)); }

    @SuppressWarnings("unchecked")
    private static Multimap<Attribute,AttributeModifier>[] createModifiers() {
        Multimap<Attribute,AttributeModifier>[] result=new Multimap[10];result[0]=ImmutableMultimap.of();
        for(int form=1;form<=9;form++){
            ImmutableMultimap.Builder<Attribute,AttributeModifier> builder=ImmutableMultimap.builder();
            builder.putAll(FingerEquipment.meleeAttributes(DAMAGE[form],SPEED[form]));
            if(form>=8)builder.put(ForgeMod.ENTITY_REACH.get(),new AttributeModifier(REACH,"Caduceus reach",form==8?10:3,AttributeModifier.Operation.ADDITION));
            result[form]=builder.build();
        }
        return result;
    }
    @Override public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot,ItemStack stack) {
        return slot==EquipmentSlot.MAINHAND ? MODIFIERS.get()[form(stack)] : ImmutableMultimap.of();
    }

    public static int form(ItemStack stack) {
        return stack.getItem() instanceof Finger2WeaponSP
                ? Mth.clamp(ItemStackData.getInt(stack,"CustomModelData"),0,9) : 0;
    }

    public static int unlockedLevel(ItemStack stack) {
        return form(stack) == 0 ? 0 : Integer.bitCount(ItemStackData.getInt(stack, FORMS) & 511) / 3;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            checkOwner(stack, player);
            int next = form(stack) % 9 + 1;
            changeForm(stack, player, next);
            ItemStackData.update(stack, tag -> { tag.putInt(HITS, 0); tag.putUUID(OWNER, player.getUUID()); });
            syncHeld(player);
            player.displayClientMessage(Component.translatable("tooltip.pm_equips.caduceus.form." + next), true);
        }
        // Using an item suppresses vanilla's repeated use calls while the button is held.
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override public int getUseDuration(ItemStack stack) { return 72000; }

    private static void changeForm(ItemStack stack, LivingEntity holder, int form) {
        boolean mainHand = holder != null && holder.getMainHandItem() == stack;
        if (mainHand) stack.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach( (attribute, modifier) -> {
            var instance = holder.getAttribute(attribute);
            if (instance != null) instance.removeModifier(modifier.getId());
        });
        if (form == 0) {
            ItemStackData.update(stack,tag->tag.remove("CustomModelData"));
        } else {
            ItemStackData.putInt(stack,"CustomModelData",form);
        }
        if (mainHand) stack.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach( (attribute, modifier) -> {
            var instance = holder.getAttribute(attribute);
            if (instance != null) FingerEquipment.addModifier(instance,modifier);
        });
    }

    public static void reset(ItemStack stack, LivingEntity holder) {
        if (!(stack.getItem() instanceof Finger2WeaponSP)) return;
        if (form(stack) != 0) changeForm(stack, holder, 0);
        CompoundTag tag = ItemStackData.copy(stack);
        if (tag.contains(HITS) || tag.contains(FORMS) || tag.contains(OWNER)) {
            tag.remove(HITS); tag.remove(FORMS); tag.remove(OWNER); ItemStackData.set(stack, tag);
        }
    }

    public static void resetCombo(LivingEntity holder) {
        if (holder.level().isClientSide) return;
        clearHits(holder.getMainHandItem());
        HeldState state = HELD.get(holder);
        if (state != null) state.pendingAttack = false;
        syncHeld(holder);
    }

    private static void clearHits(ItemStack stack) {
        if (stack.getItem() instanceof Finger2WeaponSP && ItemStackData.getInt(stack, HITS) != 0)
            ItemStackData.putInt(stack, HITS, 0);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || form(player.getMainHandItem()) == 0) return;
        HeldState previous = HELD.get(player);
        if (previous != null && previous.pendingAttack) resetCombo(player);
        syncHeld(player);
        HeldState state = HELD.get(player);
        if (state != null) state.pendingAttack = true;
    }

    @SubscribeEvent
    public static void onBlockAttack(PlayerInteractEvent.LeftClickBlock event) {
        if (form(event.getItemStack()) != 0) resetCombo(event.getEntity());
    }

    private static void checkOwner(ItemStack stack, LivingEntity holder) {
        if (form(stack) == 0) return;
        CompoundTag tag = ItemStackData.copy(stack);
        if (!tag.hasUUID(OWNER) || !tag.getUUID(OWNER).equals(holder.getUUID())) reset(stack, holder);
    }

    private static boolean isHeld(LivingEntity holder, ItemStack stack) {
        return holder.isAlive() && (holder.getMainHandItem() == stack || holder.getOffhandItem() == stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;
        if (entity instanceof LivingEntity holder && isHeld(holder, stack)) checkOwner(stack, holder);
        else reset(stack, null);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, Player player) {
        if (!player.level().isClientSide) { reset(stack, player); syncHeld(player); }
        return true;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide) reset(stack, null);
        return false;
    }

    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity target,LivingEntity attacker) {
        if(form(stack)!=0)stack.hurtAndBreak(1,attacker,entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return form(stack)!=0;
    }

    private static LivingEntity meleeAttacker(DamageSource source) {
        if (!source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK) && !source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) return null;
        return source.getDirectEntity() == source.getEntity() && source.getEntity() instanceof LivingEntity living
                && FingerEquipment.weapon(living).getItem() instanceof Finger2WeaponSP ? living : null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingAttackEvent event) {
        if (event.getAmount() <= 0 || meleeAttacker(event.getSource()) == null) return;
        // Run after protection handlers, but before vanilla compares lastHurt/iframes.
        // Keep the post-hit protection for other weapons; only this attack bypasses it.
        event.getEntity().invulnerableTime = 0;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity attacker = meleeAttacker(event.getSource());
        if (attacker == null) return;
        if (event.getAmount() <= 0) { resetCombo(attacker); return; }
        ItemStack stack = FingerEquipment.weapon(attacker);
        checkOwner(stack, attacker);
        int form = form(stack);
        if (form == 0) return;
        HeldState state = HELD.get(attacker);
        if (state != null) state.pendingAttack = false;
        CompoundTag tag = ItemStackData.copy(stack);
        int hits = Mth.clamp(tag.getInt(HITS) + 1, 1, 256);
        tag.putInt(HITS, hits); tag.putInt(FORMS, tag.getInt(FORMS) | 1 << (form - 1));
        ItemStackData.set(stack, tag);
        syncHeld(attacker);
        LivingEntity target = event.getEntity();
        if (!target.isAlive()) return;
        MobEffect debuff = switch (form) {
            case 4 -> MobEffectInit.BIND.get();
            case 5 -> MobEffectInit.BLEED.get();
            case 6 -> MobEffectInit.WEAK.get();
            case 7 -> MobEffectInit.PARALYSIS.get();
            case 8 -> MobEffectInit.DISARM.get();
            default -> null;
        };
        if (debuff != null) target.addEffect(new MobEffectInstance(debuff, hits * (form == 5 || form == 7 ? 100 : 200), hits - 1), attacker);
        float threshold = form == 4 ? .10f : form == 9 ? .25f : 0;
        if (threshold > 0 && target.getHealth() - event.getAmount() <= target.getMaxHealth() * threshold) {
            // Preserve normal death events/loot/kill credit; do not bypass protection with setHealth/kill.
            event.setAmount(Math.max(event.getAmount(),target.getHealth()));
        }
    }

    private static final class HeldState {
        ItemStack main = ItemStack.EMPTY, off = ItemStack.EMPTY;
        int mainForm;
        boolean pendingAttack;
        final Map<MobEffect, BuffLease> buffs = new HashMap<>();
    }

    /** Short leases let unloaded entities recover naturally and preserve pre-existing effects. */
    private static final class BuffLease {
        final MobEffectInstance previous;
        final long began;
        MobEffectInstance applied;
        int amplifier;

        BuffLease(LivingEntity holder, MobEffect type) {
            MobEffectInstance current = holder.getEffect(type);
            previous = current == null ? null : new MobEffectInstance(current);
            began = holder.level().getGameTime();
        }

        void refresh(LivingEntity holder, MobEffect type, int amplifier) {
            MobEffectInstance current = holder.getEffect(type);
            if (current != null && (current.getAmplifier() > amplifier
                    || current.getAmplifier() == amplifier && !current.endsWithin(6))) return;
            holder.addEffect(new MobEffectInstance(type, 12, amplifier, false, false, true));
            applied = holder.getEffect(type); this.amplifier = amplifier;
        }

        void release(LivingEntity holder, MobEffect type) {
            MobEffectInstance current = holder.getEffect(type);
            if (current == null || current != applied || current.getAmplifier() != amplifier || !current.endsWithin(12)) return;
            holder.removeEffect(type);
            if (previous != null) {
                int duration = previous.isInfiniteDuration() ? -1 : (int)Math.max(0, previous.getDuration() - (holder.level().getGameTime() - began));
                if (duration != 0) holder.addEffect(new MobEffectInstance(type, duration, previous.getAmplifier(), previous.isAmbient(), previous.isVisible(), previous.showIcon()));
            }
        }
    }

    public static void syncHeld(LivingEntity holder) {
        if (holder.level().isClientSide) return;
        ItemStack main = holder.getMainHandItem(), off = holder.getOffhandItem();
        checkOwner(main, holder); checkOwner(off, holder);
        HeldState state = HELD.get(holder);
        if (state != null) {
            boolean changed = state.main != main || state.off != off || !holder.isAlive();
            if (state.main != main) {
                // A form can change and leave the hand before vanilla snapshots equipment.
                // Retain the previous form separately: inventoryTick may already have reset its stack.
                MODIFIERS.get()[state.mainForm].forEach( (attribute, modifier) -> {
                    var instance = holder.getAttribute(attribute);
                    if (instance != null) instance.removeModifier(modifier.getId());
                });
                main.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach( (attribute, modifier) -> {
                    var instance = holder.getAttribute(attribute);
                    if (instance != null) FingerEquipment.addModifier(instance,modifier);
                });
                clearHits(main); clearHits(off); state.pendingAttack = false;
            }
            if (!isHeld(holder, state.main)) reset(state.main, null);
            if (!isHeld(holder, state.off)) reset(state.off, null);
            if (changed && holder instanceof Player player) resetMenu(player, player.containerMenu);
        }
        ItemStack active = holder.isAlive() ? form(main) != 0 ? main : form(off) != 0 ? off : ItemStack.EMPTY : ItemStack.EMPTY;
        int setUnlocked = FingerEquipment.setUnlocked(holder), setQuick = FingerEquipment.setQuick(holder);
        if (active.isEmpty() && setUnlocked == 0 && setQuick == 0) {
            if (state != null) { state.buffs.forEach((type, lease) -> lease.release(holder, type)); HELD.remove(holder); }
            return;
        }
        if (state == null) { state = new HeldState(); HELD.put(holder, state); }
        state.main = main; state.off = off; state.mainForm = form(main);
        int form = form(active);
        MobEffect passive = form == 1 ? MobEffectInit.POWER.get() : form == 2 ? MobEffectInit.ENDURANCE.get() : form == 3 ? MobEffectInit.QUICK.get() : null;
        int unlocked = Math.max(unlockedLevel(active), setUnlocked);
        int quick = Math.max(passive != null && passive.equals(MobEffectInit.QUICK.get()) ? Math.min(255, ItemStackData.getInt(active, HITS)) : -1, setQuick - 1);
        var iterator = state.buffs.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            int wanted = entry.getKey().equals(MobEffectInit.UNLOCKED.get()) ? unlocked - 1
                    : entry.getKey().equals(MobEffectInit.QUICK.get()) ? quick : entry.getKey().equals(passive) ? 1 : -1;
            if (wanted < 0 || entry.getValue().amplifier > wanted) { entry.getValue().release(holder, entry.getKey()); iterator.remove(); }
        }
        if (passive != null && !passive.equals(MobEffectInit.QUICK.get())) refreshBuff(holder, state, passive, 1);
        if (quick >= 0) refreshBuff(holder, state, MobEffectInit.QUICK.get(), quick);
        if (unlocked > 0) refreshBuff(holder, state, MobEffectInit.UNLOCKED.get(), unlocked - 1);
    }

    private static void refreshBuff(LivingEntity holder, HeldState state, MobEffect type, int amplifier) {
        state.buffs.computeIfAbsent(type, ignored -> new BuffLease(holder, type)).refresh(holder, type, amplifier);
    }

    private static void resetMenu(Player player, AbstractContainerMenu menu) {
        for (var slot : menu.slots) if (!isHeld(player, slot.getItem())) reset(slot.getItem(), null);
        if (!isHeld(player, menu.getCarried())) reset(menu.getCarried(), null);
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        HeldState state = HELD.get(player);
        if (state != null && state.pendingAttack) resetCombo(player);
        else syncHeld(player);
    }
    @SubscribeEvent public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player)) syncHeld(event.getEntity());
    }
    @SubscribeEvent public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (!event.getEntity().level().isClientSide) { resetMenu(event.getEntity(), event.getContainer()); syncHeld(event.getEntity()); }
    }
    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        reset(player.getMainHandItem(), player); reset(player.getOffhandItem(), player); syncHeld(player);
    }
    @SubscribeEvent public static void onServerStop(ServerStoppedEvent event) { HELD.clear(); }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.caduceus.form." + form(stack)));
        tooltip.add(Component.translatable("tooltip.pm_equips.caduceus.controls"));
        tooltip.add(Component.translatable("tooltip.pm_equips.caduceus.unlocked", unlockedLevel(stack)));
        tooltip.add(Component.translatable("tooltip.pm_equips.caduceus.combo"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
