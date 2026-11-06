package dev.kosmx.playerAnim.minecraftApi.layers;

import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;

/**
 * 左手玩家辅助 modifier：玩家主手为左手时自动镜像全部动画。
 * 与上游 {@code LeftHandedHelperModifier} 语义一致。
 */
public class LeftHandedHelperModifier extends MirrorModifier {
    private final EntityPlayer player;

    public LeftHandedHelperModifier(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && player.getPrimaryHand() == EnumHandSide.LEFT;
    }
}
