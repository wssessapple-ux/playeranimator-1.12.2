package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.config.PlayerAnimatorConfig;
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

    @Unique
    private static int playeranimator$frameLogs = 0;

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

        if (!applier.isActive()) return;
        // First person pass: the layer itself decides whether it exists there. AnimationStack
        // skips layers whose FirstPersonMode is NONE, so a clip that doesn't opt in stays vanilla.
        if (FirstPersonMode.isFirstPersonPass() && !PlayerAnimatorConfig.isFirstPersonAnimations()) return;

        // Cached at preInit: this used to be a System.getProperty lookup per model per frame.
        boolean debugFrames = PlayerAnimatorConfig.isDebugFrames();

        if (debugFrames && playeranimator$frameLogs++ < 4000) {
            org.apache.logging.log4j.LogManager.getLogger("playeranimator").info(
                    "[frames] t={} partial={} R=({}, {}, {}) L=({}, {}, {}) body=({}, {}, {}) head=({}, {}, {})",
                    System.currentTimeMillis(),
                    net.minecraft.client.Minecraft.getMinecraft().getRenderPartialTicks(),
                    bipedRightArm.rotateAngleX, bipedRightArm.rotateAngleY, bipedRightArm.rotateAngleZ,
                    bipedLeftArm.rotateAngleX, bipedLeftArm.rotateAngleY, bipedLeftArm.rotateAngleZ,
                    bipedBody.rotateAngleX, bipedBody.rotateAngleY, bipedBody.rotateAngleZ,
                    bipedHead.rotateAngleX, bipedHead.rotateAngleY, bipedHead.rotateAngleZ);
        }

        boolean debugLocal = debugFrames && entityIn == Minecraft.getMinecraft().player;
        float baseRx = bipedRightArm.rotateAngleX, baseRy = bipedRightArm.rotateAngleY, baseRz = bipedRightArm.rotateAngleZ;
        float baseLx = bipedLeftArm.rotateAngleX, baseLy = bipedLeftArm.rotateAngleY, baseLz = bipedLeftArm.rotateAngleZ;
        float baseHx = bipedHead.rotateAngleX, baseHy = bipedHead.rotateAngleY, baseHz = bipedHead.rotateAngleZ;
        float baseTx = bipedBody.rotateAngleX, baseTy = bipedBody.rotateAngleY, baseTz = bipedBody.rotateAngleZ;

        if (PlayerAnimatorConfig.isDebugModel()) {
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

        if (debugLocal) {
            float r = 180f / (float) Math.PI;
            org.apache.logging.log4j.LogManager.getLogger("playeranimator").info(
                    "[pose] t={} p={} netHeadYaw={} headPitch={} limb={} | base R=({},{},{}) L=({},{},{}) H=({},{},{}) T=({},{},{}) | fin R=({},{},{}) L=({},{},{}) H=({},{},{}) T=({},{},{})",
                    System.currentTimeMillis(),
                    String.format("%.3f", net.minecraft.client.Minecraft.getMinecraft().getRenderPartialTicks()),
                    String.format("%.2f", netHeadYaw), String.format("%.2f", headPitch),
                    String.format("%.2f", limbSwingAmount),
                    String.format("%.1f", baseRx * r), String.format("%.1f", baseRy * r), String.format("%.1f", baseRz * r),
                    String.format("%.1f", baseLx * r), String.format("%.1f", baseLy * r), String.format("%.1f", baseLz * r),
                    String.format("%.1f", baseHx * r), String.format("%.1f", baseHy * r), String.format("%.1f", baseHz * r),
                    String.format("%.1f", baseTx * r), String.format("%.1f", baseTy * r), String.format("%.1f", baseTz * r),
                    String.format("%.1f", bipedRightArm.rotateAngleX * r), String.format("%.1f", bipedRightArm.rotateAngleY * r), String.format("%.1f", bipedRightArm.rotateAngleZ * r),
                    String.format("%.1f", bipedLeftArm.rotateAngleX * r), String.format("%.1f", bipedLeftArm.rotateAngleY * r), String.format("%.1f", bipedLeftArm.rotateAngleZ * r),
                    String.format("%.1f", bipedHead.rotateAngleX * r), String.format("%.1f", bipedHead.rotateAngleY * r), String.format("%.1f", bipedHead.rotateAngleZ * r),
                    String.format("%.1f", bipedBody.rotateAngleX * r), String.format("%.1f", bipedBody.rotateAngleY * r), String.format("%.1f", bipedBody.rotateAngleZ * r));
        }

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
