package dev.kosmx.playerAnim.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Early mixin loader for Player Animator, mirrors the loader used by TaCZ-Legacy
 * so both mods register their configurations during FML coremod loading.
 */
@IFMLLoadingPlugin.Name("PlayerAnimatorMixinLoader")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(0)
@IFMLLoadingPlugin.TransformerExclusions({"dev.kosmx.playerAnim.mixin.PlayerAnimatorMixinLoader"})
@SuppressWarnings("unused")
public class PlayerAnimatorMixinLoader implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data) {
        Mixins.addConfiguration("mixins.playeranimator.json");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
