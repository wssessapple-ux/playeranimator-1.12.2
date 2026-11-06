package dev.kosmx.playerAnim.minecraftApi;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.impl.event.Event;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerAnimationAccess {

    private PlayerAnimationAccess() {
    }

    /**
     * Get the animation stack for a player entity on the client.
     * <p>
     * Or you can use {@code ((IPlayer) player).getAnimationStack();}
     *
     * @param player the client player object
     * @return the player's animation stack
     */
    public static AnimationStack getPlayerAnimLayer(AbstractClientPlayer player) throws IllegalArgumentException {
        if (player instanceof IPlayer) {
            return ((IPlayer) player).getAnimationStack();
        } else throw new IllegalArgumentException(player + " is not a player or library mixins failed");
    }

    /**
     * Allows mods to store animation layers associated with a player.
     * Stored data does not get automatically registered.
     *
     * @param player player entity
     * @return data accessor type, you can use get() and set() on it
     * @throws IllegalArgumentException if the given argument is not a player, or api mixins have failed
     */
    public static PlayerAssociatedAnimationData getPlayerAssociatedData(@NotNull AbstractClientPlayer player) {
        if (player instanceof IAnimatedPlayer) {
            return new PlayerAssociatedAnimationData((IAnimatedPlayer) player);
        } else throw new IllegalArgumentException(player + " is not a player or library mixins failed");
    }

    /**
     * If you don't want to create your own mixin, you can use this event to add animation to players.
     * <b>The event will fire for every player</b> and if the player reloads, it will fire again.
     * <hr>
     * NOTE: When the event fires, {@link IPlayer#getAnimationStack()} will be <code>null</code>,
     * you'll have to use the given stack.
     */
    public static final Event<AnimationRegister> REGISTER_ANIMATION_EVENT = new Event<>(AnimationRegister.class, listeners -> (player, animationStack) -> {
        for (AnimationRegister listener : listeners) {
            listener.registerAnimation(player, animationStack);
        }
    });

    @FunctionalInterface
    public interface AnimationRegister {
        /**
         * Player object is in construction, it will be invoked when you can register animation.
         * It will be invoked for every player only ONCE (it isn't a tick function).
         *
         * @param player         client player object, can be the main player or another player
         * @param animationStack the player's AnimationStack, unique for every player
         */
        void registerAnimation(@NotNull AbstractClientPlayer player, @NotNull AnimationStack animationStack);
    }

    public static class PlayerAssociatedAnimationData {
        @NotNull
        private final IAnimatedPlayer player;

        public PlayerAssociatedAnimationData(@NotNull IAnimatedPlayer player) {
            this.player = player;
        }

        /**
         * Get an animation associated with the player.
         *
         * @param id animation identifier
         * @return animation or {@code null} if not exists
         */
        @Nullable
        public IAnimation get(@NotNull ResourceLocation id) {
            return player.playerAnimator_getAnimation(id);
        }

        /**
         * Set an animation associated with the player.
         *
         * @param id        animation identifier
         * @param animation animation to store, {@code null} to clear
         * @return the previously stored animation
         */
        @Nullable
        public IAnimation set(@NotNull ResourceLocation id, @Nullable IAnimation animation) {
            return player.playerAnimator_setAnimation(id, animation);
        }
    }
}
