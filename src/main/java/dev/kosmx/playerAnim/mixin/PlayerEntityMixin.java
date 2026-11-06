package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds the animation stack to every client player. The stack is created lazily
 * (on the first tick/render) so the factory list is fully populated by then.
 */
@Mixin(EntityPlayer.class)
public abstract class PlayerEntityMixin implements IAnimatedPlayer {

    @Unique
    private Map<ResourceLocation, IAnimation> playeranimator$modAnimationData = new HashMap<>();

    @Unique
    private AnimationStack playeranimator$animationStack;

    @Unique
    private AnimationApplier playeranimator$animationApplier;

    @Unique
    private void playeranimator$init() {
        if (playeranimator$animationStack != null) return;
        AnimationStack stack = new AnimationStack();
        // Publish both fields before any callback: factories / listeners may
        // re-enter getAnimationStack() or playerAnimator_getAnimation().
        this.playeranimator$animationApplier = new AnimationApplier(stack);
        this.playeranimator$animationStack = stack;
        if ((Object) this instanceof AbstractClientPlayer) {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.prepareAnimations(
                    (AbstractClientPlayer) (Object) this, stack, playeranimator$modAnimationData);
            PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.invoker()
                    .registerAnimation((AbstractClientPlayer) (Object) this, stack);
        }
    }

    @Override
    public AnimationStack getAnimationStack() {
        playeranimator$init();
        return playeranimator$animationStack;
    }

    @Override
    public AnimationApplier playerAnimator_getAnimation() {
        playeranimator$init();
        return playeranimator$animationApplier;
    }

    @Override
    public @Nullable IAnimation playerAnimator_getAnimation(@NotNull ResourceLocation id) {
        return playeranimator$modAnimationData.get(id);
    }

    @Override
    public @Nullable IAnimation playerAnimator_setAnimation(@NotNull ResourceLocation id, @Nullable IAnimation animation) {
        if (animation == null) {
            return playeranimator$modAnimationData.remove(id);
        } else {
            return playeranimator$modAnimationData.put(id, animation);
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void playeranimator$tick(CallbackInfo ci) {
        if ((Object) this instanceof AbstractClientPlayer) {
            getAnimationStack().tick();
        }
    }
}
