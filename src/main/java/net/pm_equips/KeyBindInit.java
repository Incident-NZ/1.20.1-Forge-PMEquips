package net.pm_equips;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindInit {
    public static final KeyMapping RELOAD_KEY =
            new KeyMapping(
                    "key.categories.pm_equips.reload",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    "key.categories.pm_equips"
            );

    public static final KeyMapping SCOPE_KEY =
            new KeyMapping(
                    "key.categories.pm_equips.scope",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_SHIFT,
                    "key.categories.pm_equips"
            );

    public static final KeyMapping CORE_PAGE_ABILITY_KEY =
            new KeyMapping(
                    "key.categories.pm_equips.core_page_ability",
                    KeyConflictContext.IN_GAME,
                    KeyModifier.SHIFT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_V,
                    "key.categories.pm_equips"
            );

    public static final KeyMapping WEAPON_ABILITY_KEY =
            new KeyMapping(
                    "key.categories.pm_equips.weapon_ability",
                    KeyConflictContext.IN_GAME,
                    KeyModifier.SHIFT,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_C,
                    "key.categories.pm_equips"
            );
}
