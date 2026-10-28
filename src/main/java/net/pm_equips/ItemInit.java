package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pm_equips.items.*;
import net.pm_equips.items.materials.ArmorEquips;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PMEquipsMain.MOD_ID);


    //アイテム
    public static final DeferredHolder<Item, Item> FINGER2_CLOTH = ITEMS.register("item_finger2_cloth", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> MOB_NPC_1_SPAWN_EGG = ITEMS.register("mob_npc_1_spawn_egg",
            () -> new DeferredSpawnEggItem(EntityInit.MOB_NPC_1, 0x232831, 0x679eba, new Item.Properties()));
    public static final DeferredHolder<Item, Item> ITEM_ENKEPHALIN = ITEMS.register("item_enkephalin",
            () -> new ItemEnkephalin(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> BLADE_GEAR = ITEMS.register("item_blade_gear",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> LCORP_EBOX = ITEMS.register("item_lcorp_pebox",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> WCORP_BATTERY = ITEMS.register("item_wcorp_battery",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> RCORP_BATTERY = ITEMS.register("item_rcorp_battery",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> KCORP_AMPOULE = ITEMS.register("item_kcorp_ampoule",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> HCORP_BOLUS_MAO = ITEMS.register("item_hcorp_bolus_mao",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> HCORP_BOLUS_SI = ITEMS.register("item_hcorp_bolus_si",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> HCORP_BOLUS_YOU = ITEMS.register("item_hcorp_bolus_you",
            () -> new Item(new Item.Properties().stacksTo(64)));

    //弾薬アイテム
    public static final DeferredHolder<Item, Item> MAGIC_BULLET_AMMO = ITEMS.register("ammo_magic",
            () -> new ItemAmmoLobEGOMagic(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> RIFLE_BULLET_AMMO = ITEMS.register("ammo_large",
            () -> new ItemAmmoLarge(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> PISTOL_BULLET_AMMO = ITEMS.register("ammo_small",
            () -> new ItemAmmoSmall(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> SHOTGUN_BULLET_AMMO = ITEMS.register("ammo_shell",
            () -> new ItemAmmoShotShell(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> EXPLOSIVE_BULLET_AMMO = ITEMS.register("ammo_explosion",
            () -> new ItemAmmoExplosive(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> P_BULLET_LARV = ITEMS.register("ammo_logic_hg",
            () -> new ItemAmmoLogicHG(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> P_BULLET_LASG = ITEMS.register("ammo_logic_sg",
            () -> new ItemAmmoLogicSG(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> AMMO_THUMBS_IF = ITEMS.register("ammo_thumbs_if", ItemAmmoThumbsIF::new);
    public static final DeferredHolder<Item, Item> AMMO_THUMBS_FR = ITEMS.register("ammo_thumbs_fr", ItemAmmoThumbsFR::new);
    public static final DeferredHolder<Item, Item> AMMO_THUMBS_AP = ITEMS.register("ammo_thumbs_ap", ItemAmmoThumbsAP::new);
    public static final DeferredHolder<Item, Item> AMMO_THUMBS_KB = ITEMS.register("ammo_thumbs_kb", ItemAmmoThumbsKB::new);

    //L社EGO武器
    public static final DeferredHolder<Item, Item> W5_TWILIGHT = ITEMS.register("lobego5_weapon_twilight", LobEGOWeapon5Twilight::new);
    public static final DeferredHolder<Item, Item> W5_PARADISE_LOST = ITEMS.register("lobego5_weapon_whitenight", LobEGOWeapon5WhiteNight::new);
    public static final DeferredHolder<Item, Item> W5_MIMICRY = ITEMS.register("lobego5_weapon_mimicry", LobEGOWeapon5Mimicry::new);
    public static final DeferredHolder<Item, Item> W5_DA_CAPO = ITEMS.register("lobego5_weapon_dacapo", LobEGOWeapon5DaCapo::new);
    public static final DeferredHolder<Item, Item> W5_SMILE = ITEMS.register("lobego5_weapon_smile", LobEGOWeapon5Smile::new);
    public static final DeferredHolder<Item, Item> W5_SOUND_OF_A_STAR = ITEMS.register("lobego5_weapon_star", LobEGOWeapon5Star::new);
    public static final DeferredHolder<Item, Item> W5_PINK = ITEMS.register("lobego5_weapon_pink",
            () -> new LobEGOWeapon5Pink(new Item.Properties().durability(4000)));
    public static final DeferredHolder<Item, Item> W5_JUSTITIA = ITEMS.register("lobego4_weapon_justitia", LobEGOWeapon5Justitia::new);
    public static final DeferredHolder<Item, Item> W4_THE_SWORD_SHARPENED_WITH_TEARS = ITEMS.register("lobego4_weapon_tears", LobEGOWeapon4Tears::new);
    public static final DeferredHolder<Item, Item> W4_LAMP = ITEMS.register("lobego4_weapon_lamp", LobEGOWeapon4Lamp::new);
    public static final DeferredHolder<Item, Item> W4_HATRED = ITEMS.register("lobego4_weapon_hatred", LobEGOWeapon4Hatred::new);
    public static final DeferredHolder<Item, Item> W4_HEAVEN = ITEMS.register("lobego4_weapon_heaven", LobEGOWeapon4Heaven::new);
    public static final DeferredHolder<Item, Item> W4_SWAN = ITEMS.register("lobego4_weapon_swan", LobEGOWeapon4Swan::new);
    public static final DeferredHolder<Item, Item> W4_STEM = ITEMS.register("lobego4_weapon_stem", LobEGOWeapon4Stem::new);
    public static final DeferredHolder<Item, Item> W4_DIFFRACTION = ITEMS.register("lobego4_weapon_diffraction", LobEGOWeapon4Diffraction::new);
    public static final DeferredHolder<Item, Item> W4_BLUE_SCAR = ITEMS.register("lobego4_weapon_blue", LobEGOWeapon4BlueScar::new);
    public static final DeferredHolder<Item, Item> W4_CRIMSON_SCAR_R = ITEMS.register("lobego4_weapon_crimson_axe", LobEGOWeapon4CrimsonScarAxe::new);
    public static final DeferredHolder<Item, Item> W4_CRIMSON_SCAR_L = ITEMS.register("lobego4_weapon_crimson_gun",
            () -> new LobEGOWeapon4CrimsonScarGun(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_AROMA = ITEMS.register("lobego4_weapon_aroma",
            () -> new LobEGOWeapon4Aroma(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_HORNET = ITEMS.register("lobego4_weapon_hornet",
            () -> new LobEGOWeapon4Hornet(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_HORNET_ALTER = ITEMS.register("lobego4_weapon_hornet_alter",
            () -> new LobEGOWeapon4HornetAlter(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_SOLEMN_LAMENT_L = ITEMS.register("lobego3_weapon_lament_white",
            () -> new LobEGOWeapon4LamentWhite(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_SOLEMN_LAMENT_R = ITEMS.register("lobego3_weapon_lament_black",
            () -> new LobEGOWeapon4LamentBlack(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_MAGIC_BULLET = ITEMS.register("lobego3_weapon_magic",
            () -> new LobEGOWeapon4MagicBullet(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W3_BLOOD = ITEMS.register("lobego3_weapon_blood", LobEGOWeapon3Blood::new);
    public static final DeferredHolder<Item, Item> W3_BEAR = ITEMS.register("lobego3_weapon_bear", LobEGOWeapon3Bear::new);
    public static final DeferredHolder<Item, Item> W3_HARVEST = ITEMS.register("lobego3_weapon_harvest", LobEGOWeapon3Harvest::new);
    public static final DeferredHolder<Item, Item> W3_LOGGING = ITEMS.register("lobego3_weapon_logging", LobEGOWeapon3Logging::new);
    public static final DeferredHolder<Item, Item> W3_GALAXY = ITEMS.register("lobego3_weapon_galaxy", LobEGOWeapon3Galaxy::new);
    public static final DeferredHolder<Item, Item> W3_GRINDER_MK4 = ITEMS.register("lobego3_weapon_mk4", LobEGOWeapon3MK4::new);
    public static final DeferredHolder<Item, Item> W3_ICE_SHARD = ITEMS.register("lobego3_weapon_ice", LobEGOWeapon3IceShard::new);
    public static final DeferredHolder<Item, Item> W3_HARMONY = ITEMS.register("lobego3_weapon_harmony",
            () -> new LobEGOWeapon3Harmony(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> W3_LAETITIA = ITEMS.register("lobego3_weapon_laetitia",
            () -> new LobEGOWeapon3Laetitia(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> W2_REGLET = ITEMS.register("lobego2_weapon_reglet", LobEGOWeapon2Reglet::new);
    public static final DeferredHolder<Item, Item> W2_SOMEWHERE = ITEMS.register("lobego2_weapon_somewhere", LobEGOWeapon2SomeWhere::new);
    public static final DeferredHolder<Item, Item> W2_BATH = ITEMS.register("lobego2_weapon_bath", LobEGOWeapon2Bath::new);
    public static final DeferredHolder<Item, Item> W2_RED_EYE = ITEMS.register("lobego2_weapon_red", LobEGOWeapon2RedEye::new);
    public static final DeferredHolder<Item, Item> W2_LANTERN = ITEMS.register("lobego2_weapon_lantern", LobEGOWeapon2Lantern::new);
    public static final DeferredHolder<Item, Item> W2_BEAK = ITEMS.register("lobego2_weapon_beak",
            () -> new LobEGOWeapon2Beak(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W2_SOLITUDE = ITEMS.register("lobego2_weapon_solitude",
            () -> new LobEGOWeapon2Solitude(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W2_TODAY = ITEMS.register("lobego2_weapon_today",
            () -> new LobEGOWeapon2Today(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W2_FOURTH_MATCH_FIRE = ITEMS.register("lobego2_weapon_match",
            () -> new LobEGOWeapon2Match(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W1_PENITENCE = ITEMS.register("lobego1_weapon_penitence", LobEGOWeapon1Penitence::new);
    public static final DeferredHolder<Item, Item> W1_WINGBEAT = ITEMS.register("lobego1_weapon_wingbeat", LobEGOWeapon1WingBeat::new);
    public static final DeferredHolder<Item, Item> W1_SODA = ITEMS.register("lobego1_weapon_soda",
            () -> new LobEGOWeapon1Soda(new Item.Properties().durability(800)));
    //L社EGOスーツ
    public static final DeferredHolder<Item, Item> S5_TWILIGHT = ITEMS.register("lobego5_suit_twilight",
            () -> new LobEGOSuit5Twilight(ArmorEquips.A5_TWILIGHT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, Item> S5_PARADISE_LOST = ITEMS.register("lobego5_suit_whitenight",
            () -> new LobEGOSuit5WhiteNight(ArmorEquips.A5_PARADISE_LOST, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, Item> S5_MIMICRY = ITEMS.register("lobego5_suit_mimicry",
            () -> new LobEGOSuit5Mimicry(ArmorEquips.A5_MIMICRY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S5_DA_CAPO = ITEMS.register("lobego5_suit_dacapo",
            () -> new LobEGOSuit5DaCapo(ArmorEquips.A5_DA_CAPO, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S5_SMILE = ITEMS.register("lobego5_suit_smile",
            () -> new LobEGOSuit5Smile(ArmorEquips.A5_SMILE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S5_SOUND_OF_A_STAR = ITEMS.register("lobego5_suit_star",
            () -> new LobEGOSuit5Star(ArmorEquips.A5_SOUND_OF_A_STAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S5_PINK = ITEMS.register("lobego5_suit_pink",
            () -> new LobEGOSuit5Pink(ArmorEquips.A5_PINK, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S5_JUSTITIA = ITEMS.register("lobego4_suit_justitia",
            () -> new LobEGOSuit5Justitia(ArmorEquips.A5_JUSTITIA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_THE_SWORD_SHARPENED_WITH_TEARS = ITEMS.register("lobego4_suit_tears",
            () -> new LobEGOSuit4Tears(ArmorEquips.A4_THE_SWORD_SHARPENED_WITH_TEARS, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_LAMP = ITEMS.register("lobego4_suit_lamp",
            () -> new LobEGOSuit4Lamp(ArmorEquips.A4_LAMP, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_HORNET = ITEMS.register("lobego4_suit_hornet",
            () -> new LobEGOSuit4Hornet(ArmorEquips.A4_HORNET, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_HEAVEN = ITEMS.register("lobego4_suit_heaven",
            () -> new LobEGOSuit4Heaven(ArmorEquips.A4_HEAVEN, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_HATRED = ITEMS.register("lobego4_suit_hatred",
            () -> new LobEGOSuit4Hatred(ArmorEquips.A4_HATRED, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_BLUE_SCAR = ITEMS.register("lobego4_suit_blue",
            () -> new LobEGOSuit4BlueScar(ArmorEquips.A4_BLUE_SCAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_CRIMSON_SCAR = ITEMS.register("lobego4_suit_crimson",
            () -> new LobEGOSuit4CrimsonScar(ArmorEquips.A4_CRIMSON_SCAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_STEM = ITEMS.register("lobego4_suit_stem",
            () -> new LobEGOSuit4Stem(ArmorEquips.A4_STEM, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_SWAN = ITEMS.register("lobego4_suit_swan",
            () -> new LobEGOSuit4Swan(ArmorEquips.A4_SWAN, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S4_AROMA = ITEMS.register("lobego4_suit_aroma",
            () -> new LobEGOSuit4Aroma(ArmorEquips.A4_AROMA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_MAGIC_BULLET = ITEMS.register("lobego3_suit_magic",
            () -> new LobEGOSuit3MagicBullet(ArmorEquips.A3_MAGIC_BULLET, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_SOLEMN_LAMENT = ITEMS.register("lobego3_suit_lament",
            () -> new LobEGOSuit3Lament(ArmorEquips.A3_SOLEMN_LAMENT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_GALAXY = ITEMS.register("lobego3_suit_galaxy",
            () -> new LobEGOSuit3Galaxy(ArmorEquips.A3_GALAXY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_BLOOD = ITEMS.register("lobego3_suit_blood",
            () -> new LobEGOSuit3Blood(ArmorEquips.A3_BLOOD, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_LOGGING = ITEMS.register("lobego3_suit_logging",
            () -> new LobEGOSuit3Logging(ArmorEquips.A3_LOGGING, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_BEAR = ITEMS.register("lobego3_suit_bear",
            () -> new LobEGOSuit3Bear(ArmorEquips.A3_BEAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_HARVEST = ITEMS.register("lobego3_suit_harvest",
            () -> new LobEGOSuit3Harvest(ArmorEquips.A3_HARVEST, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_GRINDER_MK4 = ITEMS.register("lobego3_suit_mk4",
            () -> new LobEGOSuit3MK4(ArmorEquips.A3_GRINDER_MK4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_ICE_SHARD = ITEMS.register("lobego3_suit_ice",
            () -> new LobEGOSuit3IceShard(ArmorEquips.A3_ICE_SHARD, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S3_LAETITIA = ITEMS.register("lobego3_suit_laetitia",
            () -> new LobEGOSuit3Laetitia(ArmorEquips.A3_LAETITIA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_REGLET = ITEMS.register("lobego2_suit_reglet",
            () -> new LobEGOSuit2Reglet(ArmorEquips.A2_REGLET, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_RED_EYE = ITEMS.register("lobego2_suit_red",
            () -> new LobEGOSuit2RedEye(ArmorEquips.A2_RED_EYE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_BEAK = ITEMS.register("lobego2_suit_beak",
            () -> new LobEGOSuit2Beak(ArmorEquips.A2_BEAK, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_FOURTH_MATCH_FIRE = ITEMS.register("lobego2_suit_match",
            () -> new LobEGOSuit2Match(ArmorEquips.A2_FOURTH_MATCH_FIRE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_SOMEWHERE = ITEMS.register("lobego2_suit_somewhere",
            () -> new LobEGOSuit2SomeWhere(ArmorEquips.A2_SOMEWHERE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_SOLITUDE = ITEMS.register("lobego2_suit_solitude",
            () -> new LobEGOSuit2Solitude(ArmorEquips.A2_SOLITUDE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_TODAY = ITEMS.register("lobego2_suit_today",
            () -> new LobEGOSuit2Today(ArmorEquips.A2_TODAY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_BATH = ITEMS.register("lobego2_suit_bath",
            () -> new LobEGOSuit2Bath(ArmorEquips.A2_BATH, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S2_LANTERN = ITEMS.register("lobego2_suit_lantern",
            () -> new LobEGOSuit2Lantern(ArmorEquips.A2_LANTERN, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S1_SODA = ITEMS.register("lobego1_suit_soda",
            () -> new LobEGOSuit1Soda(ArmorEquips.A1_SODA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S1_WINGBEAT = ITEMS.register("lobego1_suit_wingbeat",
            () -> new LobEGOSuit1WingBeat(ArmorEquips.A1_WINGBEAT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> S1_PENITENCE = ITEMS.register("lobego1_suit_penitence",
            () -> new LobEGOSuit1Penitence(ArmorEquips.A1_PENITENCE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //L社装備+図書館
    public static final DeferredHolder<Item, Item> LCORP_BATON = ITEMS.register("wing_lcorp_baton", WingLCorpBaton::new);
    public static final DeferredHolder<Item, Item> LCORP_PISTOL = ITEMS.register("wing_lcorp_pistol",
            () -> new WingLCorpPistol(new Item.Properties()));
    public static final DeferredHolder<Item, Item> LIBRARY_SWORD_1 = ITEMS.register("library_sword_1", LibrarianSword::new);

    //フィクサー武器 近距離
    public static final DeferredHolder<Item, Item> G_MIMICRY = ITEMS.register("fixer_color_kali", FixerColorKaliWeapon::new);
    public static final DeferredHolder<Item, Item> EX_DURANDAL = ITEMS.register("fixer_color_roland_durandal", FixerColorRolandWeaponDurandal::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_WHEELS = ITEMS.register("fixer_color_roland_10", FixerColorRolandWeapon10::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_CRYSTAL = ITEMS.register("fixer_color_roland_9", FixerColorRolandWeapon9::new);
    public static final DeferredHolder<Item, Item> WEAPON_ROLAND_SHOTGUN = ITEMS.register("fixer_color_roland_8",
            () -> new FixerColorRolandWeapon8(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> WEAPON_ROLAND_REVOLVER = ITEMS.register("fixer_color_roland_7",
            () -> new FixerColorRolandWeapon7(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_MOOK = ITEMS.register("fixer_color_roland_6", FixerColorRolandWeapon6::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_ALLAS = ITEMS.register("fixer_color_roland_5", FixerColorRolandWeapon5::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_OLD_BOYS = ITEMS.register("fixer_color_roland_4", FixerColorRolandWeapon4::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_RANGA = ITEMS.register("fixer_color_roland_3", FixerColorRolandWeapon3::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_ZELKOVA_AXE = ITEMS.register("fixer_color_roland_2", FixerColorRolandWeapon2::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_ZELKOVA_MACE = ITEMS.register("fixer_color_roland_1", FixerColorRolandWeapon1::new);
    public static final DeferredHolder<Item, Item> EX_ARGALIA = ITEMS.register("fixer_color_argalia", FixerColorArgaliaWeapon::new);

    public static final DeferredHolder<Item, Item> ASC2_SOUTH_W1 = ITEMS.register("asc2_south_weapon1", FixerASC2SouthWeapon1::new);
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_W2 = ITEMS.register("asc2_south_weapon2", FixerASC2SouthWeapon2::new);
    public static final DeferredHolder<Item, Item> ASC2_WALTER = ITEMS.register("asc2_south_walter", FixerASC2Walter::new);
    public static final DeferredHolder<Item, Item> ASC2_WEST_WEAPON = ITEMS.register("asc2_west_weapon", FixerASC2WestWeapon::new);
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S6 = ITEMS.register("asc2_south_armor_6",
            () -> new FixerASC2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC6, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S5 = ITEMS.register("asc2_south_armor_5",
            () -> new FixerASC2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S4 = ITEMS.register("asc2_south_armor_4",
            () -> new FixerASC2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S3 = ITEMS.register("asc2_south_armor_3",
            () -> new FixerASC2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S2 = ITEMS.register("asc2_south_armor_2",
            () -> new FixerASC2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S1 = ITEMS.register("asc2_south_armor_1",
            () -> new FixerASC2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_WEST_ARMOR_S6 = ITEMS.register("asc2_west_armor_6",
            () -> new FixerASC2WestArmor(ArmorEquips.ASC2_ARMOR_SEC6, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_WEST_ARMOR_S5 = ITEMS.register("asc2_west_armor_5",
            () -> new FixerASC2WestArmor(ArmorEquips.ASC2_ARMOR_SEC5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_WEST_ARMOR_S4 = ITEMS.register("asc2_west_armor_4",
            () -> new FixerASC2WestArmor(ArmorEquips.ASC2_ARMOR_SEC4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_WEST_ARMOR_S3 = ITEMS.register("asc2_west_armor_3",
            () -> new FixerASC2WestArmor(ArmorEquips.ASC2_ARMOR_SEC3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_WEST_ARMOR_S2 = ITEMS.register("asc2_west_armor_2",
            () -> new FixerASC2WestArmor(ArmorEquips.ASC2_ARMOR_SEC2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ASC2_WEST_ARMOR_S1 = ITEMS.register("asc2_west_armor_1",
            () -> new FixerASC2WestArmor(ArmorEquips.ASC2_ARMOR_SEC1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    //W社装備
    public static final DeferredHolder<Item, Item> WCORP_WEAPON_1 = ITEMS.register("wing_wcorp_w1", WingWCorpWeapon1::new);
    public static final DeferredHolder<Item, Item> WCORP_WEAPON_2 = ITEMS.register("wing_wcorp_w2", WingWCorpWeapon2::new);
    public static final DeferredHolder<Item, Item> WCORP_WEAPON_3 = ITEMS.register("wing_wcorp_w3", WingWCorpWeapon3::new);

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L1 = ITEMS.register("wing_wcorp_armor_l1",
            () -> new WingWCorpArmor(ArmorEquips.WCORP_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L2 = ITEMS.register("wing_wcorp_armor_l2",
            () -> new WingWCorpArmor(ArmorEquips.WCORP_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L3 = ITEMS.register("wing_wcorp_armor_l3",
            () -> new WingWCorpArmor(ArmorEquips.WCORP_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L4 = ITEMS.register("wing_wcorp_armor_l4",
            () -> new WingWCorpArmor(ArmorEquips.WCORP_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L5 = ITEMS.register("wing_wcorp_armor_l5",
            () -> new WingWCorpArmor(ArmorEquips.WCORP_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //R社装備
    public static final DeferredHolder<Item, Item> RCORP_RABBIT_RIFLE = ITEMS.register("wing_rcorp04_rabbit_w1",
            () -> new WingRCorp4thRabbitWeapon1(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> RCORP_RABBIT_KNIFE = ITEMS.register("wing_rcorp04_rabbit_w2", WingRCorp4thRabbitWeapon2::new);

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L1 = ITEMS.register("wing_rcorp04_rabbit_armor_l1",
            () -> new WingRCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L2 = ITEMS.register("wing_rcorp04_rabbit_armor_l2",
            () -> new WingRCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L3 = ITEMS.register("wing_rcorp04_rabbit_armor_l3",
            () -> new WingRCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L4 = ITEMS.register("wing_rcorp04_rabbit_armor_l4",
            () -> new WingRCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L5 = ITEMS.register("wing_rcorp04_rabbit_armor_l5",
            () -> new WingRCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //K社装備
    public static final DeferredHolder<Item, Item> KCORP_WEAPON_1 = ITEMS.register("wing_kcorp_w1", WingKCorpWeapon1::new);
    public static final DeferredHolder<Item, Item> KCORP_WEAPON_2 = ITEMS.register("wing_kcorp_w2", WingKCorpWeapon2::new);
    public static final DeferredHolder<Item, Item> KCORP_WEAPON_3 = ITEMS.register("wing_kcorp_w3", WingKCorpWeapon3::new);

    public static final DeferredHolder<Item, Item> KCORP_OFFICER_ARMOR_L1 = ITEMS.register("wing_kcorp_officer_armor_l1",
            () -> new WingKCorpOfficerArmor(ArmorEquips.KCORP_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> KCORP_OFFICER_ARMOR_L2 = ITEMS.register("wing_kcorp_officer_armor_l2",
            () -> new WingKCorpOfficerArmor(ArmorEquips.KCORP_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> KCORP_AGENT_ARMOR_L3 = ITEMS.register("wing_kcorp_agent_armor_l3",
            () -> new WingKCorpAgentArmor(ArmorEquips.KCORP_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> KCORP_AGENT_ARMOR_L4 = ITEMS.register("wing_kcorp_agent_armor_l4",
            () -> new WingKCorpAgentArmor(ArmorEquips.KCORP_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> KCORP_AGENT_ARMOR_L5 = ITEMS.register("wing_kcorp_agent_armor_l5",
            () -> new WingKCorpAgentArmor(ArmorEquips.KCORP_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //H社装備
    public static final DeferredHolder<Item, Item> HCORP_MAO_WEAPON = ITEMS.register("wing_hcorp_mao_weapon", WingHCorpMaoWeapon::new);
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L1 = ITEMS.register("wing_hcorp_mao_armor_l1",
            () -> new WingHCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L2 = ITEMS.register("wing_hcorp_mao_armor_l2",
            () -> new WingHCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L3 = ITEMS.register("wing_hcorp_mao_armor_l3",
            () -> new WingHCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L4 = ITEMS.register("wing_hcorp_mao_armor_l4",
            () -> new WingHCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L5 = ITEMS.register("wing_hcorp_mao_armor_l5",
            () -> new WingHCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> HCORP_SI_WEAPON = ITEMS.register("wing_hcorp_si_weapon", WingHCorpSiWeapon::new);
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L1 = ITEMS.register("wing_hcorp_si_armor_l1",
            () -> new WingHCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L2 = ITEMS.register("wing_hcorp_si_armor_l2",
            () -> new WingHCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L3 = ITEMS.register("wing_hcorp_si_armor_l3",
            () -> new WingHCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L4 = ITEMS.register("wing_hcorp_si_armor_l4",
            () -> new WingHCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L5 = ITEMS.register("wing_hcorp_si_armor_l5",
            () -> new WingHCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> HCORP_YOU_WEAPON = ITEMS.register("wing_hcorp_you_weapon", WingHCorpYouWeapon::new);
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L1 = ITEMS.register("wing_hcorp_you_armor_l1",
            () -> new WingHCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L2 = ITEMS.register("wing_hcorp_you_armor_l2",
            () -> new WingHCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L3 = ITEMS.register("wing_hcorp_you_armor_l3",
            () -> new WingHCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L4 = ITEMS.register("wing_hcorp_you_armor_l4",
            () -> new WingHCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L5 = ITEMS.register("wing_hcorp_you_armor_l5",
            () -> new WingHCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //五本指装備
    public static final DeferredHolder<Item, Item> FINGER1_WEAPON1 = ITEMS.register("finger1_weapon1", Finger1Weapon1::new);
    public static final DeferredHolder<Item, Item> FINGER1_WEAPON2 = ITEMS.register("finger1_weapon2", Finger1Weapon2::new);
    public static final DeferredHolder<Item, Item> FINGER1_ARMOR1 = ITEMS.register("finger1_armor1", Finger1Armor1::new);
    public static final DeferredHolder<Item, Item> FINGER1_ARMOR2 = ITEMS.register("finger1_armor2", Finger1Armor2::new);
    public static final DeferredHolder<Item, Item> FINGER1_ARMOR3 = ITEMS.register("finger1_armor3", Finger1Armor3::new);
    public static final DeferredHolder<Item, Item> FINGER2_WEAPON1 = ITEMS.register("finger2_weapon1", Finger2Weapon1::new);
    public static final DeferredHolder<Item, Item> FINGER2_WEAPON2 = ITEMS.register("finger2_weapon2", Finger2Weapon2::new);
    public static final DeferredHolder<Item, Item> FINGER2_WEAPON3 = ITEMS.register("finger2_weapon3", Finger2Weapon3::new);
    public static final DeferredHolder<Item, Item> FINGER2_WEAPON_SP_CADUCEUS = ITEMS.register("finger2_weapon_sp_caduceus", Finger2WeaponSP::new);
    public static final DeferredHolder<Item, Item> FINGER2_ARMOR1 = ITEMS.register("finger2_armor1", Finger2Armor1::new);
    public static final DeferredHolder<Item, Item> FINGER2_ARMOR2 = ITEMS.register("finger2_armor2", Finger2Armor2::new);
    public static final DeferredHolder<Item, Item> FINGER2_ARMOR3 = ITEMS.register("finger2_armor3", Finger2Armor3::new);

    //リンバス社装備
    public static final DeferredHolder<Item, Item> LCA_AUTO_RIFLE = ITEMS.register("lca_auto_rifle",
                () -> new LCAAutoRifle(new Item.Properties().durability(4000)));
    public static final DeferredHolder<Item, Item> LCA_KHOPESH = ITEMS.register("lca_khopesh", LCAKhopesh::new);
    public static final DeferredHolder<Item, Item> LCA_ARMOR = ITEMS.register("lca_udjat_armor",
            () -> new LCAUdjatArmor(ArmorEquips.LCA_UDJAT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> LCCB_PISTOL = ITEMS.register("lccb_pistol",
            () -> new LCCBPistol(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> LCCB_SHOTGUN = ITEMS.register("lccb_shotgun",
            () -> new LCCBShotgun(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> LCCB_BATON = ITEMS.register("lccb_baton", LCCBBaton::new);
    public static final DeferredHolder<Item, Item> LCCB_ARMOR = ITEMS.register("lccb_armor",
            () -> new LCCBArmor(ArmorEquips.LCC_BEFORE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> LCD_WEAPON_MOOK = ITEMS.register("lcd_weapon_mook",
            () -> new LCDWeaponMook(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> LCD_ARMOR = ITEMS.register("lcd_armor",
            () -> new LCDArmor(ArmorEquips.LCD_AENG_DU, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> LCE_EGO_DIMENSION_SHREDDER_WEAPON = ITEMS.register("lce_ego_dimension_shredder_weapon", LCEEGODimensionShredderWeapon::new);
    public static final DeferredHolder<Item, Item> LCE_EGO_DIMENSION_SHREDDER_ARMOR = ITEMS.register("lce_ego_dimension_shredder_armor",
            () -> new LCEEGODimensionShredderArmor(ArmorEquips.LCE_EGO_DIMENSION_SHREDDER, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> LAMENT_YISANG = ITEMS.register("item_lament_yisang",
            () -> new LimbusEGOYisangLament(new Item.Properties().durability(2000)));

    //頭装備
    public static final DeferredHolder<Item, Item> HEAD_CLAW = ITEMS.register("head_claw", HeadClawGauntlet::new);
    public static final DeferredHolder<Item, Item> HEAD_ARBITER_ARMOR = ITEMS.register("head_arbiter_armor", HeadArbiterArmor::new);
}
