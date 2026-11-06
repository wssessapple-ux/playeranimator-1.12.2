package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * First-person pass marker + {@code body} root-matrix transform.
 * <p>
 * 1.12.2 的 {@code RenderPlayer.applyRotations} 对应上游 {@code PlayerRenderer#setupRotations}：
 * 在它 RETURN 时身体朝向/鞘翅姿态已经全部算完，模型与手持物品随后都在同一矩阵里渲染，
 * 因此 body 的 position/rotation 在这里加到矩阵上与上游语义一致。
 */
@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin {

    @Inject(method = "renderRightArm", at = @At("HEAD"))
    private void playeranimator$firstPersonRightStart(AbstractClientPlayer player, CallbackInfo ci) {
        FirstPersonMode.setFirstPersonPass(true);
    }

    @Inject(method = "renderRightArm", at = @At("RETURN"))
    private void playeranimator$firstPersonRightEnd(AbstractClientPlayer player, CallbackInfo ci) {
        FirstPersonMode.setFirstPersonPass(false);
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"))
    private void playeranimator$firstPersonLeftStart(AbstractClientPlayer player, CallbackInfo ci) {
        FirstPersonMode.setFirstPersonPass(true);
    }

    @Inject(method = "renderLeftArm", at = @At("RETURN"))
    private void playeranimator$firstPersonLeftEnd(AbstractClientPlayer player, CallbackInfo ci) {
        FirstPersonMode.setFirstPersonPass(false);
    }

    @Inject(method = "applyRotations", at = @At("RETURN"))
    private void playeranimator$applyBodyTransform(AbstractClientPlayer player, float ageInTicks,
                                                   float rotationYaw, float partialTicks, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.isGamePaused()) return;

        AnimationApplier applier = ((IAnimatedPlayer) player).playerAnimator_getAnimation();
        applier.setTickDelta(partialTicks);
        if (!applier.isActive()) return;

        // These are additive properties (upstream semantics)
        Vec3f position = applier.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO);
        Vec3f rotation = applier.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);

        GlStateManager.translate(position.getX(), position.getY() + 0.7f, position.getZ());
        GlStateManager.rotate((float) Math.toDegrees(rotation.getZ()), 0f, 0f, 1f); // roll
        GlStateManager.rotate((float) Math.toDegrees(rotation.getY()), 0f, 1f, 0f); // yaw
        GlStateManager.rotate((float) Math.toDegrees(rotation.getX()), 1f, 0f, 0f); // pitch
        GlStateManager.translate(0f, -0.7f, 0f);
    }
}
