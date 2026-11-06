package dev.kosmx.playerAnim.minecraftApi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Client-side animation registry. Animation identifier:
 * namespace = mod id, path = animation name (not the filename).
 * <p>
 * TaCZ loads its gun-pack animations through its own asset manager, this registry
 * is the public API for other mods.
 */
public final class PlayerAnimationRegistry {

    private static final HashMap<ResourceLocation, KeyframeAnimation> animations = new HashMap<>();

    private PlayerAnimationRegistry() {
    }

    /**
     * Get an animation from the registry.
     *
     * @param identifier identifier
     * @return animation, {@code null} if no animation
     */
    @Nullable
    public static KeyframeAnimation getAnimation(@NotNull ResourceLocation identifier) {
        return animations.get(identifier);
    }

    /**
     * Get an Optional animation from the registry.
     */
    @NotNull
    public static Optional<KeyframeAnimation> getAnimationOptional(@NotNull ResourceLocation identifier) {
        return Optional.ofNullable(getAnimation(identifier));
    }

    /**
     * @return an unmodifiable map of all the animations
     */
    public static Map<ResourceLocation, KeyframeAnimation> getAnimations() {
        return Collections.unmodifiableMap(new HashMap<>(animations));
    }

    /**
     * Returns the animations of a specific mod/namespace.
     *
     * @param modid namespace (assets/modid)
     * @return map of path and animations
     */
    public static Map<String, KeyframeAnimation> getModAnimations(String modid) {
        HashMap<String, KeyframeAnimation> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, KeyframeAnimation> entry : animations.entrySet()) {
            if (entry.getKey().getNamespace().equals(modid)) {
                map.put(entry.getKey().getPath(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * Register an animation, mostly useful for mods that load their own files.
     */
    public static void registerAnimation(@NotNull ResourceLocation identifier, @NotNull KeyframeAnimation animation) {
        animations.put(identifier, animation);
    }

    /**
     * Remove all registered animations.
     */
    public static void clear() {
        animations.clear();
    }

    /**
     * Helper function to convert an animation name (a JSON string, possibly quoted)
     * to a plain string. 1.12.2 has no text component JSON serializer, so a
     * primitive JSON string is used when possible and quotes are stripped otherwise.
     */
    public static String serializeTextToString(String arg) {
        if (arg == null) return null;
        try {
            JsonElement element = new JsonParser().parse(arg);
            if (element != null && element.isJsonPrimitive()) {
                return element.getAsString();
            }
        } catch (Exception ignored) {
        }
        return arg.replace("\"", "");
    }
}
