package dev.kosmx.playerAnim.mixin;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Early mixin loader for Player Animator, mirrors the loader used by TaCZ-Legacy
 * so both mods register their configurations during FML coremod loading.
 * <p>
 * The mod can be installed on a dedicated server so it can send animation packets, but every
 * mixin in {@code mixins.playeranimator.json} targets a client class ({@code ModelBiped},
 * {@code RenderPlayer}, {@code AbstractClientPlayer}). Applying them on a server fails at
 * class-load time, so the configuration is only registered on the client. On a server the mod
 * then consists of the config and the network channel only, which is exactly what a server
 * needs to drive animations.
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
        if (isClient()) {
            Mixins.addConfiguration("mixins.playeranimator.json");
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    /**
     * The side is already resolved when coremods are constructed. The fallback assumes client:
     * skipping the mixins on a client only disables the rendering, while registering them on a
     * server would crash it, and a server that cannot be identified is less likely than a client.
     */
    private static boolean isClient() {
        try {
            return FMLCommonHandler.instance().getSide().isClient();
        } catch (Throwable ignored) {
            return true;
        }
    }
}
