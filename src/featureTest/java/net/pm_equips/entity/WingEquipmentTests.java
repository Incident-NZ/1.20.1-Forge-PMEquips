package net.pm_equips.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.events.CombatEvents;
import net.pm_equips.items.ClassEnergySupport;
import net.pm_equips.items.HeadExecutioner;
import net.pm_equips.items.WingRCorpWeapon3;
import net.pm_equips.items.WingRCorpWeapon4;
import net.pm_equips.util.ItemStackData;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class WingEquipmentTests {
    private static Player player(GameTestHelper h, Item weapon, Item armor) {
        Player player = ForgeTestPlayers.makeMockPlayer(h, GameType.SURVIVAL);
        player.setPos(h.absoluteVec(new Vec3(2.5, 2, 5.5))); player.setYRot(-90); player.setXRot(0); player.setNoGravity(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(weapon));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(armor)); player.tick(); return player;
    }
    private static Villager target(GameTestHelper h, int x) {
        Villager target = h.spawn(EntityType.VILLAGER, x, 2, 5); target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000); target.setHealth(1000); return target;
    }
    private static void near(GameTestHelper h, double actual, double expected, String message) {
        h.assertTrue(Math.abs(actual - expected) < .002, message + ": " + actual + " vs " + expected);
    }
    private static void use(Player player, InteractionHand hand) { player.getItemInHand(hand).getItem().use(player.level(), player, hand); }
    private static void charge(Player player, int energy) { ClassEnergySupport.energy(player.getItemBySlot(EquipmentSlot.LEGS)).orElseThrow().receiveEnergy(energy, false); }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void newWeaponsHaveExactBaseStatsAndProjectileInheritance(GameTestHelper h) {
        Item[] weapons = {ItemInit.TCORP_WEAPON_1.get(),ItemInit.TCORP_WEAPON_2.get(),ItemInit.TCORP_WEAPON_3.get(),
                ItemInit.TCORP_WEAPON_4.get(),ItemInit.RCORP_WEAPON_3.get(),ItemInit.RCORP_WEAPON_4.get()};
        int[] damage={11,14,16,17,8,10},durability={2000,1000,1000,3000,3000,4000};
        double[] speed={1.1,.8,4,1.8,1.2,1.8};
        for(int i=0;i<weapons.length;i++){
            Player player=player(h,weapons[i],Items.AIR);
            near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),damage[i],"Weapon damage");
            near(h,player.getAttributeValue(Attributes.ATTACK_SPEED),speed[i],"Weapon speed");
            h.assertTrue(player.getMainHandItem().getMaxDamage()==durability[i],"Exact durability");
        }
        h.assertTrue(ItemInit.TCORP_WEAPON_3.get() instanceof ProjectileWeaponItem && ItemInit.RCORP_WEAPON_3.get() instanceof ProjectileWeaponItem,
                "Ranged weapons inherit the native projectile weapon class");h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void twelveNewArmorsUseLegsAndExactRankStats(GameTestHelper h) {
        Item[] time={ItemInit.TCORP_ARMOR_L1.get(),ItemInit.TCORP_ARMOR_L2.get(),ItemInit.TCORP_ARMOR_L3.get(),ItemInit.TCORP_ARMOR_L4.get(),ItemInit.TCORP_ARMOR_L5.get()};
        Item[] reindeer={ItemInit.RCORP_ARMOR2_L1.get(),ItemInit.RCORP_ARMOR2_L2.get(),ItemInit.RCORP_ARMOR2_L3.get(),ItemInit.RCORP_ARMOR2_L4.get(),ItemInit.RCORP_ARMOR2_L5.get()};
        int[] defense={12,14,16,18,20},toughness={4,8,10,12,15},durability={300,500,700,850,1000};
        for(int i=0;i<5;i++) for(Item item:new Item[]{time[i],reindeer[i]}){
            Player player=player(h,Items.AIR,item);ItemStack armor=player.getItemBySlot(EquipmentSlot.LEGS);
            h.assertTrue(((ArmorItem)item).getEquipmentSlot()==EquipmentSlot.LEGS,"Every new uniform is leg armor");
            near(h,player.getArmorValue(),defense[i],"Rank defense");near(h,player.getAttributeValue(Attributes.ARMOR_TOUGHNESS),toughness[i],"Rank toughness");
            near(h,player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE),.1*(i+1),"Rank knockback resistance");
            h.assertTrue(armor.getMaxDamage()==durability[i],"Rank durability");
            if(item==reindeer[i])h.assertTrue(ClassEnergySupport.energy(armor).orElseThrow().getMaxEnergyStored()==10000,"Reindeer FE matches Rabbit");
        }
        for(Item item:new Item[]{ItemInit.RCORP_ARMOR_3.get(),ItemInit.HEAD_EXECUTIONER.get()}){
            Player player=player(h,Items.AIR,item);near(h,player.getArmorValue(),30,"Unique armor defense");
            near(h,player.getAttributeValue(Attributes.ARMOR_TOUGHNESS),item==ItemInit.HEAD_EXECUTIONER.get()?30:20,"Unique toughness");
            near(h,player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE),1,"Complete knockback resistance");
        }
        h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void armorDisplaysAndMobsWithoutAttackSpeedDoNotCrash(GameTestHelper h) {
        for(Item item:new Item[]{ItemInit.TCORP_ARMOR_L1.get(),ItemInit.HEAD_EXECUTIONER.get()}) {
            var stand=h.spawn(EntityType.ARMOR_STAND,4,2,5);stand.setItemSlot(EquipmentSlot.LEGS,new ItemStack(item));
            stand.tick();
            Zombie mob=h.spawn(EntityType.ZOMBIE,7,2,5);mob.setNoAi(true);mob.setItemSlot(EquipmentSlot.LEGS,new ItemStack(item));
            mob.tick();
            near(h,mob.getAttributeValue(Attributes.ATTACK_DAMAGE),item==ItemInit.HEAD_EXECUTIONER.get()?14:9,"Mob armor weapon works without an attack-speed attribute");
        }
        h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void builtInWeaponsRequireEmptyHandsAndTimeArmorWearsOnHits(GameTestHelper h) {
        for(Item armor:new Item[]{ItemInit.TCORP_ARMOR_L1.get(),ItemInit.TCORP_ARMOR_L2.get(),ItemInit.HEAD_EXECUTIONER.get()}){
            Player player=player(h,Items.AIR,armor);boolean executioner=armor==ItemInit.HEAD_EXECUTIONER.get();
            near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),executioner?14:9,"Built-in attack damage");
            near(h,player.getAttributeValue(Attributes.ATTACK_SPEED),executioner?2:1.5,"Built-in attack speed");
            ItemStack worn=player.getItemBySlot(EquipmentSlot.LEGS);Villager target=target(h,8);
            target.hurt(player.damageSources().playerAttack(player),(float)player.getAttributeValue(Attributes.ATTACK_DAMAGE));
            h.assertTrue(executioner?!worn.isDamageableItem():worn.getDamageValue()==1,"Only breakable built-in armor wears when hitting");
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.IRON_SWORD));player.tick();
            near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),6,"Held weapons do not stack with armor weapons");
            if(executioner)h.assertTrue(!HeadExecutioner.activate(player),"Serum also requires empty hands");
            player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(Items.STICK));player.tick();
            near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),1,"An occupied offhand disables the built-in weapon");
        }h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void timeAbilityUsesOneDayAndOneNightChargeAcrossArmorSwaps(GameTestHelper h) {
        long time=h.getLevel().getDayTime();
        try{
            Player player=player(h,Items.AIR,ItemInit.TCORP_ARMOR_L1.get());h.getLevel().setDayTime(0);
            h.assertTrue(CombatEvents.activateArmor(player),"First daytime activation succeeds");
            h.assertTrue(player.getEffect(MobEffectInit.TCORP_SIN.get()).getDuration()==12000,"T effect lasts ten minutes");
            near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),9.9,"T increases attack by ten percent");
            near(h,player.getAttributeValue(Attributes.MOVEMENT_SPEED),.12,"T increases movement by twenty percent");
            player.setItemSlot(EquipmentSlot.LEGS,new ItemStack(ItemInit.TCORP_ARMOR_L5.get()));
            h.assertTrue(!CombatEvents.activateArmor(player),"Changing armor cannot bypass the daytime allowance");
            h.getLevel().setDayTime(12000);h.assertTrue(CombatEvents.activateArmor(player),"Night has a separate activation");
            h.assertTrue(!CombatEvents.activateArmor(player),"Night cannot activate twice");
            h.getLevel().setDayTime(24000);h.assertTrue(CombatEvents.activateArmor(player),"A new day restores its allowance");
        }finally{h.getLevel().setDayTime(time);}h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void timeSlowHasExactRangeAndCleansUpWithoutStacking(GameTestHelper h) {
        Player player=player(h,Items.AIR,ItemInit.TCORP_ARMOR_L3.get());CombatEvents.activateArmor(player);player.tick();
        Zombie hostile=h.spawn(EntityType.ZOMBIE,8,2,5);hostile.setNoAi(true);hostile.setNoGravity(true);
        double base=hostile.getAttributeValue(Attributes.MOVEMENT_SPEED);hostile.tick();near(h,hostile.getAttributeValue(Attributes.MOVEMENT_SPEED),base*.8,"Enemy aura slows exactly twenty percent");
        hostile.setPos(player.position().add(33,0,0));hostile.tick();near(h,hostile.getAttributeValue(Attributes.MOVEMENT_SPEED),base,"Leaving radius removes time slow");
        hostile.setPos(player.position().add(4,0,0));hostile.tick();player.removeEffect(MobEffectInit.TCORP_SIN.get());hostile.tick();
        near(h,hostile.getAttributeValue(Attributes.MOVEMENT_SPEED),base,"Removing the source effect removes its slow");h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void tcorpOffhandAttacksRejectOtherWeaponsAndConsumeDurability(GameTestHelper h) {
        Player player=player(h,ItemInit.TCORP_WEAPON_1.get(),Items.AIR);Villager target=target(h,5);
        ItemStack spinning=new ItemStack(ItemInit.TCORP_WEAPON_2.get());player.setItemInHand(InteractionHand.OFF_HAND,spinning);
        use(player,InteractionHand.OFF_HAND);spinning.getItem().onUseTick(player.level(),player,spinning,71999);
        target.invulnerableTime=999;spinning.getItem().onUseTick(player.level(),player,spinning,71998);
        near(h,target.getHealth(),994,"MK4-style attacks hit each tick through iframes");h.assertTrue(spinning.getDamageValue()==2,"Each accepted spinning hit wears the weapon");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.BOW));spinning.getItem().onUseTick(player.level(),player,spinning,71997);
        h.assertTrue(!player.isUsingItem(),"Switching the paired weapon to a bow stops the attack");
        use(player,InteractionHand.OFF_HAND);h.assertTrue(!player.isUsingItem(),"A bow is also rejected at activation");h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void tcorpParticleShotWorksWithoutAmmoButStopsAtWalls(GameTestHelper h) {
        Player player=player(h,ItemInit.TCORP_WEAPON_1.get(),Items.AIR);ItemStack gun=new ItemStack(ItemInit.TCORP_WEAPON_3.get());
        player.setItemInHand(InteractionHand.OFF_HAND,gun);Villager target=target(h,8);use(player,InteractionHand.OFF_HAND);
        near(h,target.getHealth(),984,"Particle shot deals sixteen damage");
        h.assertTrue(target.getEffect(MobEffectInit.BIND.get()).getAmplifier()==1 && target.getEffect(MobEffectInit.BIND.get()).getDuration()==100,"Shot applies Bind II for five seconds");
        h.assertTrue(gun.getDamageValue()==1&&player.getCooldowns().isOnCooldown(gun.getItem()),"Shot wears once and starts cooldown");
        player.getCooldowns().removeCooldown(gun.getItem());for(int y=2;y<=5;y++)for(int z=4;z<=6;z++)h.setBlock(new BlockPos(6,y,z),Blocks.STONE);
        use(player,InteractionHand.OFF_HAND);near(h,target.getHealth(),984,"A solid wall blocks the particle shot");
        player.getCooldowns().removeCooldown(gun.getItem());player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.CROSSBOW));int before=gun.getDamageValue();use(player,InteractionHand.OFF_HAND);
        h.assertTrue(gun.getDamageValue()==before,"An incompatible paired weapon prevents firing and durability loss");h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void rcorpBuffRequiresEnergyAndCommanderBuffLastsThirtyMinutes(GameTestHelper h) {
        Player player=player(h,Items.AIR,ItemInit.RCORP_ARMOR_3.get());ItemStack armor=player.getItemBySlot(EquipmentSlot.LEGS);
        h.assertTrue(ClassEnergySupport.energy(armor).orElseThrow().getMaxEnergyStored()==20000,"Commander capacity is 20000FE");
        h.assertTrue(!CombatEvents.activateArmor(player),"Empty armor cannot activate");charge(player,2000);h.assertTrue(CombatEvents.activateArmor(player),"2000FE activates the buff");
        h.assertTrue(ClassEnergySupport.energy(armor).orElseThrow().getEnergyStored()==0,"Ability uses exactly 2000FE");
        h.assertTrue(player.getEffect(MobEffectInit.RCORP_SIN.get()).getDuration()==36000,"Commander Singularity lasts thirty minutes");h.succeed();
    }
    @GameTest(template="npc_empty",timeoutTicks=100)
    public static void laserTapFiresThreeSecondsAndUsesTwoHundredFE(GameTestHelper h) {
        Player player=player(h,ItemInit.RCORP_WEAPON_3.get(),ItemInit.RCORP_ARMOR2_L1.get());ItemStack gun=player.getMainHandItem();
        charge(player,1999);use(player,InteractionHand.MAIN_HAND);h.assertTrue(!player.isUsingItem(),"Laser requires at least 2000FE");
        charge(player,1);Villager target=target(h,8);use(player,InteractionHand.MAIN_HAND);player.releaseUsingItem();
        for(int tick=1;tick<=61;tick++)h.runAtTickTime(tick,player::tick);
        h.runAtTickTime(62,()->{
            near(h,target.getHealth(),931,"A tap produces three 23-damage pulses");
            h.assertTrue(!WingRCorpWeapon3.isFiring(gun)&&gun.getDamageValue()==3,"A tap stops after three seconds");
            h.assertTrue(ClassEnergySupport.energy(player.getItemBySlot(EquipmentSlot.LEGS)).orElseThrow().getEnergyStored()==1800,"Three seconds uses exactly 200FE");
            h.assertTrue(player.getCooldowns().isOnCooldown(gun.getItem()),"Normal laser stop starts its five-second cooldown");h.succeed();
        });
    }
    @GameTest(template="npc_empty",timeoutTicks=340)
    public static void laserHoldOverheatsAfterFifteenSeconds(GameTestHelper h) {
        Player player=player(h,ItemInit.RCORP_WEAPON_3.get(),ItemInit.RCORP_ARMOR2_L5.get());charge(player,3000);ItemStack gun=player.getMainHandItem();
        Villager target=target(h,8);target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);use(player,InteractionHand.MAIN_HAND);
        for(int tick=1;tick<=301;tick++)h.runAtTickTime(tick,()->{target.invulnerableTime=999;player.tick();});
        h.runAtTickTime(302,()->{
            near(h,target.getHealth(),655,"Fifteen pulses ignore target iframes");near(h,player.getHealth(),16,"Overheat deals four armor-bypassing damage");
            h.assertTrue(!WingRCorpWeapon3.isFiring(gun)&&!player.isUsingItem(),"Overheat stops the laser");
            h.assertTrue(ClassEnergySupport.energy(player.getItemBySlot(EquipmentSlot.LEGS)).orElseThrow().getEnergyStored()==2000,"Fifteen seconds uses 1000FE");
            h.assertTrue(player.getCooldowns().isOnCooldown(gun.getItem()),"Overheat starts cooldown");h.succeed();
        });
    }
    @GameTest(template="npc_empty",timeoutTicks=80,batch="zz_commander")
    public static void marksAreSharedSingleTargetAndMultiplyFinalDamage(GameTestHelper h) {
        Player player=player(h,ItemInit.RCORP_WEAPON_4.get(),ItemInit.RCORP_ARMOR_3.get());
        try {
        var team=h.getLevel().getScoreboard().addPlayerTeam("rmark_"+player.getUUID().toString().substring(0,8));
        h.getLevel().getScoreboard().addPlayerToTeam(player.getScoreboardName(),team);
        Player ally=player(h,ItemInit.RCORP_WEAPON_4.get(),ItemInit.RCORP_ARMOR_3.get());h.getLevel().getScoreboard().addPlayerToTeam(ally.getScoreboardName(),team);
        Zombie first=h.spawn(EntityType.ZOMBIE,5,2,5),second=h.spawn(EntityType.ZOMBIE,8,2,5);first.setNoAi(true);second.setNoAi(true);
        WingRCorpWeapon4.mark(player,first);h.assertTrue(first.hasEffect(MobEffectInit.TARGET.get()),"First enemy is marked");
        WingRCorpWeapon4.mark(ally,second);h.assertTrue(!first.hasEffect(MobEffectInit.TARGET.get())&&second.hasEffect(MobEffectInit.TARGET.get()),"Allied commanders share one target");
        MobNPC1 companion=h.spawn(EntityInit.MOB_NPC_1.get(),6,2,5);companion.setOwnerUUID(player.getUUID());companion.setTame(true);companion.setNoAi(true);companion.setTarget(first);
        WingRCorpWeapon4.prioritizeTarget(companion);h.assertTrue(companion.getTarget()==second,"An owned NPC prioritizes the shared mark even while its owner is unloaded");
        first.getAttribute(Attributes.ARMOR).setBaseValue(0);second.getAttribute(Attributes.ARMOR).setBaseValue(0);
        first.hurt(player.damageSources().magic(),4);second.hurt(player.damageSources().magic(),4);
        near(h,first.getHealth(),16,"Unmarked target receives ordinary damage");near(h,second.getHealth(),14,"Marked target receives 1.5x final damage");player.discard();ally.discard();h.succeed();
            } finally { player.discard(); }
}
    @GameTest(template="npc_empty",timeoutTicks=80,batch="zz_commander")
    public static void commanderAuraIsRemovedImmediatelyAndDoesNotReplaceStrongerBuffs(GameTestHelper h) {
        Player player=player(h,ItemInit.RCORP_WEAPON_4.get(),ItemInit.RCORP_ARMOR_3.get());
        try {
        MobNPC1 ally=h.spawn(EntityInit.MOB_NPC_1.get(),5,2,5);ally.setOwnerUUID(player.getUUID());ally.setTame(true);ally.setNoAi(true);ally.tick();
        h.assertTrue(ally.hasEffect(MobEffectInit.POWER.get()),"Owned NPC receives Power I");
        ally.setPos(player.position().add(129,0,0));ally.tick();h.assertTrue(!ally.hasEffect(MobEffectInit.POWER.get()),"Leaving 128 blocks removes the aura");
        ally.setPos(player.position().add(5,0,0));ally.tick();player.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);ally.tick();
        h.assertTrue(!ally.hasEffect(MobEffectInit.POWER.get()),"Unequipping the source removes its aura");player.discard();h.succeed();
            } finally { player.discard(); }
}
    @GameTest(template="npc_empty",timeoutTicks=80,batch="zz_commander")
    public static void executionUsesTwentyKillsAndKeepsAllRareLootAtMaximumCounts(GameTestHelper h) {
        Player player=player(h,ItemInit.RCORP_WEAPON_4.get(),ItemInit.RCORP_ARMOR_3.get());ItemStack sword=player.getMainHandItem();
        try {
        for(int i=0;i<21;i++){Zombie target=h.spawn(EntityType.ZOMBIE,5,2,5);target.setNoAi(true);target.hurt(player.damageSources().playerAttack(player),1000);}
        h.assertTrue(WingRCorpWeapon4.kills(sword)==20,"Kill counter caps at twenty");
        Zombie close=h.spawn(EntityType.ZOMBIE,6,2,5),far=h.spawn(EntityType.ZOMBIE,7,2,7);close.setNoAi(true);far.setNoAi(true);far.setPos(player.position().add(33,0,0));
        Villager neutral=target(h,9);h.assertTrue(WingRCorpWeapon4.execute(player,sword),"Twenty kills enables execution");
        h.assertTrue(!close.isAlive()&&far.isAlive()&&neutral.isAlive(),"Execution affects hostile mobs inside 32 blocks only");
        h.assertTrue(WingRCorpWeapon4.kills(sword)==0,"Execution consumes all kills and cannot refill itself");
        LootParams params=new LootParams.Builder(h.getLevel()).withParameter(LootContextParams.THIS_ENTITY,close).withParameter(LootContextParams.ORIGIN,close.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE,player.damageSources().playerAttack(player)).withParameter(LootContextParams.KILLER_ENTITY,player)
                .withParameter(LootContextParams.DIRECT_KILLER_ENTITY,player).withParameter(LootContextParams.LAST_DAMAGE_PLAYER,player).create(LootContextParamSets.ENTITY);
        List<ItemStack> maximum=WingRCorpWeapon4.maximumLoot(h.getLevel(),h.getLevel().getServer().getLootData().getLootTable(close.getLootTable()),params);
        for(Item item:new Item[]{Items.IRON_INGOT,Items.CARROT,Items.POTATO})h.assertTrue(maximum.stream().anyMatch(stack->stack.is(item)&&stack.getCount()==1),"All conditional rare zombie drops are present: "+maximum);
        h.assertTrue(maximum.stream().anyMatch(stack->stack.is(Items.ROTTEN_FLESH)&&stack.getCount()==5),"Normal loot also reaches its Looting III maximum");player.discard();h.succeed();
            } finally { player.discard(); }
}
    @GameTest(template="npc_empty",timeoutTicks=80,batch="zz_commander")
    public static void commanderChargeActivatesExactlyAtFortyTicksAndWorksOffhand(GameTestHelper h) {
        Player player=player(h,Items.AIR,ItemInit.RCORP_ARMOR_3.get());
        try {
            ItemStack sword=new ItemStack(ItemInit.RCORP_WEAPON_4.get());player.setItemInHand(InteractionHand.OFF_HAND,sword);
            ItemStackData.putInt(sword,"rcorp_commander_kills",20);
            Zombie target=h.spawn(EntityType.ZOMBIE,7,2,5);target.setNoAi(true);target.setNoGravity(true);
            use(player,InteractionHand.OFF_HAND);
            for(int i=0;i<39;i++)player.tick();
            h.assertTrue(target.isAlive()&&WingRCorpWeapon4.kills(sword)==20,"Thirty-nine ticks do not execute or consume charges");
            player.tick();
            h.assertTrue(!target.isAlive()&&WingRCorpWeapon4.kills(sword)==0&&!player.isUsingItem(),"The fortieth tick performs the offhand execution");
            h.succeed();
        } finally {player.discard();}
    }

    @GameTest(template="npc_empty",timeoutTicks=100)
    public static void npcLaserCompletesItsBurstAndConsumesTheRealArmorEnergy(GameTestHelper h) {
        MobNPC1 npc=h.spawn(EntityInit.MOB_NPC_1.get(),2,2,5);npc.setNoAi(true);npc.setNoGravity(true);
        ItemStack gun=new ItemStack(ItemInit.RCORP_WEAPON_3.get()),armor=new ItemStack(ItemInit.RCORP_ARMOR2_L1.get());
        npc.setItemSlot(EquipmentSlot.MAINHAND,gun);npc.setItemSlot(EquipmentSlot.LEGS,armor);
        ClassEnergySupport.energy(armor).orElseThrow().receiveEnergy(2000,false);
        Zombie target=h.spawn(EntityType.ZOMBIE,8,2,5);target.setNoAi(true);target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000);target.setHealth(1000);target.getAttribute(Attributes.ARMOR).setBaseValue(0);
        npc.setTarget(target);npc.performRangedAttack(target,1);
        h.runAfterDelay(65,()->{
            near(h,target.getHealth(),931,"NPCs maintain all three pulses of their laser burst");
            h.assertTrue(ClassEnergySupport.energy(armor).orElseThrow().getEnergyStored()==1800,"NPCs spend FE from their actual equipped armor");
            h.assertTrue(gun.getDamageValue()==3&&!WingRCorpWeapon3.isFiring(gun),"NPC laser stops normally after its burst");
            h.succeed();
        });
    }

}
