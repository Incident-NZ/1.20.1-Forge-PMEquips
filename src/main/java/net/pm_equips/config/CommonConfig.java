package net.pm_equips.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.BooleanValue ALLOW_TERRAIN_DAMAGE;

    public static final ModConfigSpec.BooleanValue ALLOW_FRIENDLY_FIRE;
    public static final ModConfigSpec.BooleanValue NPC_DEFAULT_SKINS;
    public static final ModConfigSpec.ConfigValue<String> NPC_FIXED_SKIN;
    public static final ModConfigSpec.EnumValue<NPCDisposition> NPC_PVP_DISPOSITION;

    public enum NPCDisposition { NEUTRAL, HOSTILE }

    static {

        ModConfigSpec.Builder builder =
                new ModConfigSpec.Builder();

        builder.comment("PM Equips Config")
                .push("weapons");

        ALLOW_TERRAIN_DAMAGE =
                builder
                        .comment("Enable terrain destruction from projectile weapons")
                        .define(
                                "allowTerrainDamage",
                                true
                        );

        ALLOW_FRIENDLY_FIRE =
                builder
                        .comment("Allow friendly fire")
                        .define(
                                "allowFriendlyFire",
                                false
                        );

        builder.pop();

        builder.push("npc");
        NPC_DEFAULT_SKINS = builder.comment("Include the two bundled NPC skins in random selection.")
                .define("includeDefaultSkins", true);
        NPC_FIXED_SKIN = builder.comment("A PNG filename in config/pm_equips/npc_skins. Empty = random. Append .slim.png for slim arms.")
                .define("fixedSkin", "");
        NPC_PVP_DISPOSITION = builder.comment("Only active when server PvP is enabled. NEUTRAL retaliates; HOSTILE also seeks other owners and their NPCs.")
                .defineEnum("pvpDisposition", NPCDisposition.NEUTRAL);
        builder.pop();

        COMMON_CONFIG =
                builder.build();
    }
}
