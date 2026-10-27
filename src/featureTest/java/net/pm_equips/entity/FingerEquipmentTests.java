package net.pm_equips.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.nbt.CompoundTag;
import net.pm_equips.MobEffectInit;
import net.pm_equips.TagInit;
import net.pm_equips.items.Finger2Weapon1;
import net.pm_equips.items.Finger2WeaponSP;
import net.pm_equips.util.FingerEquipment;
import net.pm_equips.util.ItemStackData;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class FingerEquipmentTests {
    private static Player player(GameTestHelper h, Item item) {
        Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        player.setPos(h.absoluteVec(new Vec3(2.5, 2, 5.5))); player.setYRot(-90); player.setXRot(0); player.setNoGravity(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item)); player.tick();
        for (int y=2;y<=6;y++) for (int z=4;z<=6;z++) h.setBlock(new BlockPos(14,y,z),Blocks.STONE);
        return player;
    }
    private static Villager target(GameTestHelper h, int x) {
        Villager target=h.spawn(EntityType.VILLAGER,x,2,5); target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); target.setHealth(200); return target;
    }
    private static void ammo(Player player,int mode,int count) { player.getInventory().setItem(10+mode,new ItemStack(FingerEquipment.ammoItem(mode),count)); }
    private static boolean fire(Player player,int mode) {
        ItemStackData.putInt(player.getMainHandItem(),"ThumbAmmo",mode);
        player.getCooldowns().removeCooldown(ItemInit.FINGER1_WEAPON1.get()); player.getCooldowns().removeCooldown(ItemInit.FINGER1_WEAPON2.get());
        return FingerEquipment.shootRifle(player,InteractionHand.MAIN_HAND,false);
    }
    private static void draw(Player player,int heldTicks) {
        ItemStack stack=player.getMainHandItem();
        stack.getItem().use(player.level(),player,InteractionHand.MAIN_HAND);
        stack.getItem().releaseUsing(stack,player.level(),player,stack.getUseDuration()-heldTicks);
        player.stopUsingItem(); player.tick();
    }
    private static void near(GameTestHelper h,double actual,double expected,String message) {
        h.assertTrue(Math.abs(actual-expected)<.002,message+": "+actual+" vs "+expected);
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void freezeScalesAllThreeRealAttributes(GameTestHelper h) {
        Zombie target=h.spawn(EntityType.ZOMBIE,5,2,5);target.setNoAi(true);
        target.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1);
        target.getAttribute(Attributes.ARMOR).setBaseValue(20);target.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30);
        for(int amplifier=0;amplifier<4;amplifier++) {
            target.removeEffect(MobEffectInit.FREEZE.get());target.addEffect(new MobEffectInstance(MobEffectInit.FREEZE.get(),100,amplifier));
            near(h,target.getAttributeValue(Attributes.MOVEMENT_SPEED),1-.1*(amplifier+1),"Freeze movement penalty");
            near(h,target.getArmorValue(),20-(amplifier+1),"Freeze armor penalty");
            near(h,target.getAttributeValue(Attributes.ATTACK_DAMAGE),30-3*(amplifier+1),"Freeze attack penalty");
        }
        target.removeEffect(MobEffectInit.FREEZE.get());near(h,target.getAttributeValue(Attributes.MOVEMENT_SPEED),1,"Removing Freeze restores speed");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void sixUniformsHaveExactLegStatsAndDurability(GameTestHelper h) {
        Player player=player(h,Items.AIR);
        Item[] items={ItemInit.FINGER1_ARMOR1.get(),ItemInit.FINGER1_ARMOR2.get(),ItemInit.FINGER1_ARMOR3.get(),
                ItemInit.FINGER2_ARMOR1.get(),ItemInit.FINGER2_ARMOR2.get(),ItemInit.FINGER2_ARMOR3.get()};
        int[] defense={12,15,20,15,18,20};
        for(int index=0;index<items.length;index++) {
            ItemStack stack=new ItemStack(items[index]);player.setItemSlot(EquipmentSlot.LEGS,stack);player.tick();
            h.assertTrue(((ArmorItem)items[index]).getEquipmentSlot()==EquipmentSlot.LEGS,"Uniform is worn in legs");
            near(h,player.getArmorValue(),defense[index],"Uniform defense");
            near(h,player.getAttributeValue(Attributes.ARMOR_TOUGHNESS),defense[index],"Uniform toughness");
            h.assertTrue(stack.getMaxDamage()==(index%3+1)*1000,"Uniform durability");
        }
        player.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);player.tick();near(h,player.getArmorValue(),0,"Removing armor clears its defense");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void rifleMeleeStatsBleedAndDurabilityAreIndependentFromAmmo(GameTestHelper h) {
        for(int version=1;version<=2;version++) {
            Player player=player(h,version==1?ItemInit.FINGER1_WEAPON1.get():ItemInit.FINGER1_WEAPON2.get());
            Villager target=target(h,7);
            near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),version==1?3:8,"Rifle melee damage");
            near(h,player.getAttributeValue(Attributes.ATTACK_SPEED),1.2,"Rifle melee attack speed");
            for(int i=0;i<25;i++)player.tick();player.attack(target);
            h.assertTrue(player.getMainHandItem().getMaxDamage()==2000 && player.getMainHandItem().getDamageValue()==1,"Melee costs one durability without ammunition");
            if(version==1)h.assertTrue(!target.hasEffect(MobEffectInit.BLEED.get()),"Normal rifle melee has no special effect");
            else h.assertTrue(target.getEffect(MobEffectInit.BLEED.get()).getAmplifier()==1 && target.getEffect(MobEffectInit.BLEED.get()).getDuration()==100,"Bayonet melee gives Bleed II for five seconds");
            target.discard();
        }
        h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void normalShotConsumesOneAmmoAndHonorsFortyTickCooldown(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON1.get());Villager near=target(h,7),far=target(h,10);ammo(player,0,3);
        h.assertTrue(fire(player,0),"Normal shot succeeds with large ammo");near(h,near.getHealth(),193,"Normal shot deals seven");near(h,far.getHealth(),200,"Only nearest target is hit");
        var bleed=near.getEffect(MobEffectInit.BLEED.get());h.assertTrue(bleed!=null && bleed.getAmplifier()==0 && bleed.getDuration()==100,"Normal ranged shot gives Bleed I for five seconds");
        h.assertTrue(player.getInventory().getItem(10).getCount()==2 && player.getMainHandItem().getDamageValue()==1,"Normal shot spends one ammo and one durability");
        h.assertTrue(!FingerEquipment.shootRifle(player,InteractionHand.MAIN_HAND,false),"Repeated packet is rejected during cooldown");
        for(int i=0;i<39;i++)player.getCooldowns().tick();h.assertTrue(!FingerEquipment.shootRifle(player,InteractionHand.MAIN_HAND,false),"Cooldown lasts through tick 39");
        player.getCooldowns().tick();h.assertTrue(FingerEquipment.shootRifle(player,InteractionHand.MAIN_HAND,false),"The gun fires at tick 40");
        h.assertTrue(h.getLevel().getEntitiesOfClass(Projectile.class,player.getBoundingBox().inflate(15)).isEmpty(),"Particle shots create no projectile entity");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void shotsStopAtWallsAndMissingAmmoDoesNotSpendDurability(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON2.get());Villager target=target(h,8);ammo(player,1,1);
        for(int y=2;y<6;y++)h.setBlock(new BlockPos(5,y,5),Blocks.STONE);
        h.assertTrue(fire(player,1),"A blocked shot still consumes ammo");near(h,target.getHealth(),200,"Shots cannot damage through walls");
        h.assertTrue(!target.isOnFire(),"Blocked fire ammo cannot ignite through walls");
        int damage=player.getMainHandItem().getDamageValue();h.assertTrue(!fire(player,1),"No ammunition means no shot");
        h.assertTrue(player.getMainHandItem().getDamageValue()==damage,"Rejected shots spend no durability");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void fireAndFreezeAmmoApplyOnlyTheirOwnEffects(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON1.get());Villager fire=target(h,7);ammo(player,1,2);
        fire(player,1);h.assertTrue(fire.getRemainingFireTicks()==100 && !fire.hasEffect(MobEffectInit.BLEED.get()) && !fire.hasEffect(MobEffectInit.FREEZE.get()),"Fire ammo ignites for five seconds without normal-ammo Bleed");
        fire.discard();Villager freeze=target(h,7);ammo(player,2,2);fire(player,2);
        h.assertTrue(freeze.getEffect(MobEffectInit.FREEZE.get()).getAmplifier()==0 && freeze.getEffect(MobEffectInit.FREEZE.get()).getDuration()==100 && !freeze.isOnFire(),"Freeze ammo applies Freeze I for five seconds");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void armorPiercingAddsTenPercentOfActualDamageThroughResistance(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON1.get());ammo(player,0,2);ammo(player,3,2);
        Villager normal=target(h,7);normal.getAttribute(Attributes.ARMOR).setBaseValue(20);normal.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,200,1));
        fire(player,0);float dealt=200-normal.getHealth();normal.discard();
        Villager piercing=target(h,7);piercing.getAttribute(Attributes.ARMOR).setBaseValue(20);piercing.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,200,1));
        fire(player,3);near(h,200-piercing.getHealth(),dealt*1.1,"Additional piercing damage bypasses armor, resistance and I-frames");
        h.assertTrue(!piercing.hasEffect(MobEffectInit.BLEED.get()),"Piercing ammo does not inherit normal-ammo Bleed");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void knockbackMovesFiveBlocksAndStopsAtSolidWalls(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON1.get());ammo(player,4,2);Villager target=target(h,7);double start=target.getX();
        fire(player,4);near(h,target.getX()-start,5,"Knockback ammo displaces five blocks in open space");
        target.discard();target=target(h,7);start=target.getX();
        for(int y=2;y<6;y++)h.setBlock(new BlockPos(9,y,5),Blocks.STONE);
        fire(player,4);h.assertTrue(target.getX()>start && target.getX()<h.absolutePos(new BlockPos(9,2,5)).getX(),"Knockback cannot push a target through a block");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void combinedShotConsumesEveryAvailableSpecialTypeExactlyOnce(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON2.get());for(int mode=0;mode<5;mode++)ammo(player,mode,3);
        Villager target=target(h,7);double x=target.getX();
        h.assertTrue(FingerEquipment.specialShot(player),"Combined shot succeeds");near(h,200-target.getHealth(),23.1,"Four types produce 21 damage plus 2.1 piercing damage");
        h.assertTrue(target.isOnFire() && target.hasEffect(MobEffectInit.FREEZE.get()) && !target.hasEffect(MobEffectInit.BLEED.get()),"Combined shot applies every consumed special and no normal effect");
        near(h,target.getX()-x,5,"Combined shot includes knockback");
        for(int mode=1;mode<5;mode++)h.assertTrue(player.getInventory().getItem(10+mode).getCount()==2,"Each special type is consumed once");
        h.assertTrue(player.getInventory().getItem(10).getCount()==3 && player.getMainHandItem().getDamageValue()==1,"Combined shot does not consume normal ammo or extra durability");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void partialCombinedShotAndAmmoSelectionArePerStack(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER1_WEAPON1.get());Villager target=target(h,7);
        h.assertTrue(!FingerEquipment.specialShot(player),"No special types cannot produce a combined shot");ammo(player,1,2);
        h.assertTrue(FingerEquipment.specialShot(player),"One available special type is sufficient");near(h,200-target.getHealth(),18,"One type produces 17 plus one damage");
        for(int mode=1;mode<=5;mode++){h.assertTrue(FingerEquipment.cycleAmmo(player),"Ammo cycling recognizes the gun");h.assertTrue(FingerEquipment.ammoMode(player.getMainHandItem())==mode%5,"Cycle includes normal and four special types");}
        Player other=player(h,ItemInit.FINGER1_WEAPON1.get());h.assertTrue(FingerEquipment.ammoMode(other.getMainHandItem())==0,"Selection is stored per stack");
        for(int mode=1;mode<5;mode++)h.assertTrue(new ItemStack(FingerEquipment.ammoItem(mode)).is(TagInit.Items.BULLET),"Special ammo is registered in the bullet tag");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=160)
    public static void proselyteDrawUsesFortyTicksAndAbilityCooldownOnly(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER2_WEAPON1.get());ItemStack sword=player.getMainHandItem();Villager target=target(h,7);
        h.assertTrue(sword.getMaxDamage()==1000,"Proselyte durability is 1000, not its template tier's durability");
        near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),4,"Sheathed damage is half of eight");near(h,player.getAttributeValue(Attributes.ATTACK_SPEED),1.5,"Proselyte speed");
        for(int i=0;i<20;i++)player.tick();player.attack(target);h.assertTrue(sword.getDamageValue()==0,"Sheathed attacks consume no durability");
        draw(player,39);h.assertTrue(Finger2Weapon1.drawn(sword) && !player.hasEffect(MobEffectInit.POWER.get()),"Thirty-nine ticks draws without charge buff");
        near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),8,"Drawn damage is eight");
        target.invulnerableTime=0;player.attack(target);h.assertTrue(sword.getDamageValue()==1,"Drawn attacks consume durability");
        draw(player,0);draw(player,40);h.assertTrue(player.getEffect(MobEffectInit.POWER.get()).getAmplifier()==3 && player.getEffect(MobEffectInit.POWER.get()).getDuration()==99,"Forty ticks grants Power IV for five seconds (one tick elapsed)");
        player.removeEffect(MobEffectInit.POWER.get());draw(player,0);draw(player,40);
        h.assertTrue(Finger2Weapon1.drawn(sword) && !player.hasEffect(MobEffectInit.POWER.get()),"During ability cooldown ordinary drawing still works but grants no buff");
        draw(player,0);h.runAfterDelay(100,()->{draw(player,40);h.assertTrue(player.hasEffect(MobEffectInit.POWER.get()),"The charge ability returns after five seconds");h.succeed();});
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void proxyBonusUsesThePreHitFiftyPercentBoundary(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER2_WEAPON2.get());Villager low=target(h,7),high=target(h,9);low.setHealth(100);high.setHealth(101);
        h.assertTrue(player.getMainHandItem().getMaxDamage()==2000,"Proxy durability is 2000");
        near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),10,"Proxy attack damage");near(h,player.getAttributeValue(Attributes.ATTACK_SPEED),1.8,"Proxy attack speed");
        low.hurt(player.damageSources().playerAttack(player),10);high.hurt(player.damageSources().playerAttack(player),10);
        near(h,low.getHealth(),88,"At fifty percent damage increases by two");near(h,high.getHealth(),91,"Above fifty percent damage is unchanged");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void messengerGuardFullyBlocksAndCannotRepeatBeforeSixtyTicks(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER2_WEAPON3.get());Item item=player.getMainHandItem().getItem();
        h.assertTrue(player.getMainHandItem().getMaxDamage()==2000,"Messenger durability is 2000");
        near(h,player.getAttributeValue(Attributes.ATTACK_DAMAGE),12,"Messenger attack damage");near(h,player.getAttributeValue(Attributes.ATTACK_SPEED),.8,"Messenger attack speed");
        item.use(player.level(),player,InteractionHand.MAIN_HAND);player.hurt(player.damageSources().generic(),20);
        near(h,player.getHealth(),20,"Guard blocks the full incoming hit");h.assertTrue(!player.isUsingItem() && player.getCooldowns().isOnCooldown(item),"A successful guard stops and starts cooldown");
        item.use(player.level(),player,InteractionHand.MAIN_HAND);h.assertTrue(!player.isUsingItem(),"Cooldown prevents re-entering guard");
        player.hurt(player.damageSources().generic(),2);near(h,player.getHealth(),18,"Cooldown does not leave invulnerability behind");
        for(int i=0;i<59;i++)player.getCooldowns().tick();h.assertTrue(player.getCooldowns().isOnCooldown(item),"Cooldown remains at tick 59");
        player.getCooldowns().tick();item.use(player.level(),player,InteractionHand.MAIN_HAND);player.hurt(player.damageSources().generic(),20);
        near(h,player.getHealth(),18,"Guard works again at sixty ticks");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void armorSetBuffsCoexistWithCaduceusAndClearOnRemoval(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER2_WEAPON2.get());player.setItemSlot(EquipmentSlot.LEGS,new ItemStack(ItemInit.FINGER2_ARMOR2.get()));
        Finger2WeaponSP.syncHeld(player);h.assertTrue(player.getEffect(MobEffectInit.UNLOCKED.get()).getAmplifier()==0,"Proxy uniform and sword give Unlocked I");
        player.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);Finger2WeaponSP.syncHeld(player);h.assertTrue(!player.hasEffect(MobEffectInit.UNLOCKED.get()),"Removing uniform removes its set buff");
        ItemStack caduceus=new ItemStack(ItemInit.FINGER2_WEAPON_SP_CADUCEUS.get());player.setItemInHand(InteractionHand.MAIN_HAND,caduceus);
        player.setItemSlot(EquipmentSlot.LEGS,new ItemStack(ItemInit.FINGER2_ARMOR3.get()));Finger2WeaponSP.syncHeld(player);
        h.assertTrue(player.getEffect(MobEffectInit.QUICK.get()).getAmplifier()==0,"Oracle uniform gives Quick I even with an inactive Caduceus");
        for(int i=0;i<3;i++){caduceus.getItem().use(player.level(),player,InteractionHand.MAIN_HAND);player.stopUsingItem();}
        Villager target=target(h,7);target.hurt(player.damageSources().playerAttack(player),1);target.hurt(player.damageSources().playerAttack(player),1);
        h.assertTrue(player.getEffect(MobEffectInit.QUICK.get()).getAmplifier()==2,"Rapier can strengthen Quick above the uniform's minimum");
        caduceus.getItem().use(player.level(),player,InteractionHand.MAIN_HAND);player.stopUsingItem();
        h.assertTrue(player.getEffect(MobEffectInit.QUICK.get()).getAmplifier()==0,"Changing form returns Quick to the uniform's floor");
        player.setItemSlot(EquipmentSlot.LEGS,ItemStack.EMPTY);Finger2WeaponSP.syncHeld(player);h.assertTrue(!player.hasEffect(MobEffectInit.QUICK.get()),"Removing the set clears its Quick");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void messengerUniformBuffsOnlyAlliesInsideSixteenBlocks(GameTestHelper h) {
        Player player=player(h,ItemInit.FINGER2_WEAPON3.get());player.setItemSlot(EquipmentSlot.LEGS,new ItemStack(ItemInit.FINGER2_ARMOR2.get()));
        MobNPC1 ally=h.spawn(EntityInit.MOB_NPC_1.get(),6,2,5),far=h.spawn(EntityInit.MOB_NPC_1.get(),21,2,5);
        for(MobNPC1 npc:List.of(ally,far)){npc.setNoAi(true);npc.setOwnerUUID(player.getUUID());npc.setTame(true);}
        Zombie enemy=h.spawn(EntityType.ZOMBIE,8,2,5);enemy.setNoAi(true);
        h.assertTrue(FingerEquipment.distributeBuff(player),"Messenger set activates its ability");
        List<MobEffect> choices=List.of(MobEffectInit.POWER.get(),MobEffectInit.ENDURANCE.get(),MobEffectInit.QUICK.get());
        MobEffect selected=choices.stream().filter(player::hasEffect).findFirst().orElseThrow();
        h.assertTrue(player.getEffect(selected).getAmplifier()==0 && player.getEffect(selected).getDuration()==400 && ally.hasEffect(selected),"One random level-I buff lasts twenty seconds and includes self/allies");
        h.assertTrue(choices.stream().noneMatch(far::hasEffect) && choices.stream().noneMatch(enemy::hasEffect),"Outside allies and hostile mobs are excluded");
        h.assertTrue(!FingerEquipment.distributeBuff(player),"Repeated requests cannot bypass thirty-second cooldown");
        for(int i=0;i<599;i++)player.getCooldowns().tick();h.assertTrue(!FingerEquipment.distributeBuff(player),"Cooldown lasts through tick 599");
        player.getCooldowns().tick();h.assertTrue(FingerEquipment.distributeBuff(player),"Ability returns at tick 600");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void companionsShootSelectedSpecialAmmoWithoutInventoryAmmo(GameTestHelper h) {
        MobNPC1 npc=h.spawn(EntityInit.MOB_NPC_1.get(),2,2,5);npc.setNoAi(true);
        ItemStack gun=new ItemStack(ItemInit.FINGER1_WEAPON1.get());ItemStackData.putInt(gun,"ThumbAmmo",2);npc.setItemSlot(EquipmentSlot.MAINHAND,gun);
        Zombie target=h.spawn(EntityType.ZOMBIE,8,2,5);target.setNoAi(true);float before=target.getHealth();
        MobNPC3.shoot(npc,target,InteractionHand.MAIN_HAND);
        h.assertTrue(target.getHealth()<before && target.hasEffect(MobEffectInit.FREEZE.get()),"NPC gets virtual ammo for the selected rifle mode");
        h.assertTrue(gun.getDamageValue()==1 && npc.getOffhandItem().isEmpty(),"NPC shooting costs one durability and creates no carried ammo");h.succeed();
    }

    @GameTest(template="npc_empty",timeoutTicks=80)
    public static void leveledCompanionAddsItsLevelBonusOnlyOnceToPiercingShots(GameTestHelper h) {
        MobNPC1 npc=h.spawn(EntityInit.MOB_NPC_1.get(),2,2,5);npc.setNoAi(true);npc.gainLevels(9);
        ItemStack gun=new ItemStack(ItemInit.FINGER1_WEAPON1.get());npc.setItemSlot(EquipmentSlot.MAINHAND,gun);
        Zombie normal=h.spawn(EntityType.ZOMBIE,8,2,5);normal.setNoAi(true);
        normal.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);normal.setHealth(200);
        MobNPC3.shoot(npc,normal,InteractionHand.MAIN_HAND);float base=200-normal.getHealth();normal.discard();
        npc.getWeaponUser(InteractionHand.MAIN_HAND).getCooldowns().removeCooldown(gun.getItem());
        ItemStackData.putInt(gun,"ThumbAmmo",3);
        Zombie piercing=h.spawn(EntityType.ZOMBIE,8,2,5);piercing.setNoAi(true);
        piercing.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);piercing.setHealth(200);
        MobNPC3.shoot(npc,piercing,InteractionHand.MAIN_HAND);
        near(h,200-piercing.getHealth(),base*1.1,"NPC level damage is included in the original shot, not added again to its ten-percent supplement");h.succeed();
    }
}
