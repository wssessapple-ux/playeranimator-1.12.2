package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface injected into every client-side {@code EntityPlayer} by the mod mixins.
 */
public interface IAnimatedPlayer extends IPlayer {

    AnimationApplier playerAnimator_getAnimation();

    /**
     * Get an animation associated with the player.
     *
     * @param id animation identifier, please start with your modid to avoid collision
     * @return animation or {@code null} if not exists
     */
    @Nullable
    IAnimation playerAnimator_getAnimation(@NotNull ResourceLocation id);

    /**
     * Set an animation associated with the player.
     *
     * @param id        animation identifier
     * @param animation animation to store, {@code null} to clear
     * @return the previously stored animation
     */
    @Nullable
    IAnimation playerAnimator_setAnimation(@NotNull ResourceLocation id, @Nullable IAnimation animation);
}
