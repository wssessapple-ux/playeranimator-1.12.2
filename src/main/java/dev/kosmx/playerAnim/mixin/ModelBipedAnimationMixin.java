package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies the animation to the vanilla biped model right after vanilla computed
 * the pose. This also covers armor models because {@code ModelBase.render}
 * invokes {@code setRotationAngles} on every model instance.
 */
@Mixin(ModelBiped.class)
public abstract class ModelBipedAnimationMixin {

    @Unique
    private static long playeranimator$lastDebugLog = 0L;

    @Shadow
    public ModelRenderer bipedHead;
    @Shadow
    public ModelRenderer bipedHeadwear;
    @Shadow
    public ModelRenderer bipedBody;
    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;
    @Shadow
    public ModelRenderer bipedRightLeg;
    @Shadow
    public ModelRenderer bipedLeftLeg;

    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    private void playeranimator$applyAnimation(float limbSwing, float limbSwingAmount, float ageInTicks,
                                               float netHeadYaw, float headPitch, float scaleFactor,
                                               Entity entityIn, CallbackInfo ci) {
        // Only players are animated, never zombies or other bipeds.
        if (!(entityIn instanceof AbstractClientPlayer)) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.isGamePaused()) return;

        IAnimatedPlayer animated = (IAnimatedPlayer) entityIn;
        AnimationApplier applier = animated.playerAnimator_getAnimation();

        // Never leave animated pivots behind when the animation stops.
        playeranimator$resetPivot(applier, "head", bipedHead);
        playeranimator$resetPivot(applier, "torso", bipedBody);
        playeranimator$resetPivot(applier, "rightArm", bipedRightArm);
        playeranimator$resetPivot(applier, "leftArm", bipedLeftArm);
        playeranimator$resetPivot(applier, "rightLeg", bipedRightLeg);
        playeranimator$resetPivot(applier, "leftLeg", bipedLeftLeg);

        if (!applier.isActive() || FirstPersonMode.isFirstPersonPass()) return;

        if (Boolean.getBoolean("playeranimator.debugModel")) {
            long now = System.currentTimeMillis();
            if (now - playeranimator$lastDebugLog > 1000L) {
                playeranimator$lastDebugLog = now;
                org.apache.logging.log4j.LogManager.getLogger("playeranimator").info(
                        "[playeranimator] model active R=({}, {}, {}) L=({}, {}, {}) body=({}, {}, {}) head=({}, {}, {})",
                        bipedRightArm.rotateAngleX, bipedRightArm.rotateAngleY, bipedRightArm.rotateAngleZ,
                        bipedLeftArm.rotateAngleX, bipedLeftArm.rotateAngleY, bipedLeftArm.rotateAngleZ,
                        bipedBody.rotateAngleX, bipedBody.rotateAngleY, bipedBody.rotateAngleZ,
                        bipedHead.rotateAngleX, bipedHead.rotateAngleY, bipedHead.rotateAngleZ);
            }
        }

        applier.updatePart("head", bipedHead);
        applier.updatePart("torso", bipedBody);
        applier.updatePart("rightArm", bipedRightArm);
        applier.updatePart("leftArm", bipedLeftArm);
        applier.updatePart("rightLeg", bipedRightLeg);
        applier.updatePart("leftLeg", bipedLeftLeg);

        // ModelBiped copied head -> headwear before this injection, redo it.
        bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
        bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
        bipedHeadwear.rotateAngleZ = bipedHead.rotateAngleZ;
        bipedHeadwear.rotationPointX = bipedHead.rotationPointX;
        bipedHeadwear.rotationPointY = bipedHead.rotationPointY;
        bipedHeadwear.rotationPointZ = bipedHead.rotationPointZ;
    }

    @Unique
    private static void playeranimator$resetPivot(AnimationApplier applier, String partName, ModelRenderer part) {
        applier.resetPivot(partName, part);
    }
}
