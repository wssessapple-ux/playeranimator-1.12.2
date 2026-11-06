package dev.kosmx.playerAnim.impl.animation;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.MathHelper;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.model.ModelRenderer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Applies the animation transforms to a 1.12.2 {@link ModelRenderer}.
 * <p>
 * The {@code body} part is intentionally not written into a ModelRenderer: its
 * position/rotation are applied to the model root matrix by
 * {@code RenderPlayerMixin#applyRotations}, matching upstream {@code PlayerRendererMixin}.
 * <p>
 * Bend is intentionally not supported on 1.12.2: the upstream implementation
 * relies on bendy-lib and custom vertex consumers which do not exist here.
 */
@ApiStatus.Internal
public class AnimationApplier extends AnimationProcessor {

    /**
     * Default pivot of the 1.12.2 biped parts, matching the values upstream uses
     * as the transform input ({@code ModelPart.x/y/z} on 1.20).
     */
    private static final float[] HEAD_DEFAULT = {0f, 0f, 0f};
    private static final float[] BODY_DEFAULT = {0f, 0f, 0f};
    private static final float[] RIGHT_ARM_DEFAULT = {-5f, 2f, 0f};
    private static final float[] LEFT_ARM_DEFAULT = {5f, 2f, 0f};
    private static final float[] RIGHT_LEG_DEFAULT = {-1.9f, 12f, 0.1f};
    private static final float[] LEFT_LEG_DEFAULT = {1.9f, 12f, 0.1f};

    public AnimationApplier(IAnimation animation) {
        super(animation);
    }

    /**
     * Reset the pivots to their upstream defaults. Part rotations are not touched:
     * vanilla {@code setRotationAngles} has just written them.
     */
    public void resetPivot(String partName, ModelRenderer part) {
        float[] def = getDefault(partName);
        if (def != null) {
            part.rotationPointX = def[0];
            part.rotationPointY = def[1];
            part.rotationPointZ = def[2];
        }
    }

    public void updatePart(String partName, ModelRenderer part) {
        float[] def = getDefault(partName);
        if (def == null) return;

        Vec3f pos = this.get3DTransform(partName, TransformType.POSITION, new Vec3f(def[0], def[1], def[2]));
        part.rotationPointX = pos.getX();
        part.rotationPointY = pos.getY();
        part.rotationPointZ = pos.getZ();

        Vec3f rot = this.get3DTransform(partName, TransformType.ROTATION, new Vec3f(
                MathHelper.clampToRadian(part.rotateAngleX),
                MathHelper.clampToRadian(part.rotateAngleY),
                MathHelper.clampToRadian(part.rotateAngleZ)));
        part.rotateAngleX = rot.getX();
        part.rotateAngleY = rot.getY();
        part.rotateAngleZ = rot.getZ();
    }

    private static float[] getDefault(String partName) {
        switch (partName) {
            case "head":
                return HEAD_DEFAULT;
            case "torso":
            case "body":
                return BODY_DEFAULT;
            case "rightArm":
                return RIGHT_ARM_DEFAULT;
            case "leftArm":
                return LEFT_ARM_DEFAULT;
            case "rightLeg":
                return RIGHT_LEG_DEFAULT;
            case "leftLeg":
                return LEFT_LEG_DEFAULT;
            default:
                return null;
        }
    }
}
