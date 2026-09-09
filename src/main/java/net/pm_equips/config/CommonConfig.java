package net.pm_equips.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.BooleanValue ALLOW_TERRAIN_DAMAGE;

    public static final ModConfigSpec.BooleanValue ALLOW_FRIENDLY_FIRE;

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

        COMMON_CONFIG =
                builder.build();
    }
}