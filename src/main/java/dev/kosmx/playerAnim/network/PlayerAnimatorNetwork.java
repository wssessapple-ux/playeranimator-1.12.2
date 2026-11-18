package dev.kosmx.playerAnim.network;

import dev.kosmx.playerAnim.PlayerAnimatorMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Server to client animation packets.
 * <p>
 * The library still evaluates animations on the client (see the README); this channel only
 * carries "play this clip on that player" hints for cases where the server is the only side
 * that knows something happened. Packets are only sent when the API below is called, so a
 * server with the mod installed stays silent unless a mod asks for it.
 * <p>
 * The receiving side resolves the id through {@link dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry}
 * and drops it if the clip is not registered there, so the payload stays tiny (a resource location).
 */
public final class PlayerAnimatorNetwork {

    private static final byte ID_PLAY = 0;
    private static final byte ID_STOP = 1;

    private static final Logger LOGGER = LogManager.getLogger(PlayerAnimatorMod.MOD_ID);
    private static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(PlayerAnimatorMod.MOD_ID);
    private static boolean registered;

    private PlayerAnimatorNetwork() {
    }

    /** Called from preInit, on both sides. Safe to call twice. */
    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        WRAPPER.registerMessage(PlayAnimationHandler.class, MessagePlayAnimation.class, ID_PLAY, Side.CLIENT);
        WRAPPER.registerMessage(StopAnimationHandler.class, MessageStopAnimation.class, ID_STOP, Side.CLIENT);
        LOGGER.info("Player Animator network channel '{}' registered", PlayerAnimatorMod.MOD_ID);
    }

    // ------------------------------------------------------------------ server API

    /**
     * Make the given player's own client play a clip.
     *
     * @param target      player whose client should animate
     * @param animationId clip id registered in {@code PlayerAnimationRegistry} on that client
     * @param fadeIn      fade-in length in ticks (8 matches the upstream default)
     */
    public static void playAnimation(EntityPlayerMP target, ResourceLocation animationId, int fadeIn) {
        if (target == null || animationId == null) {
            return;
        }
        WRAPPER.sendTo(new MessagePlayAnimation(target.getUniqueID(), animationId, fadeIn), target);
    }

    /**
     * Tell every client that can see {@code source} to animate the source player. Use this for
     * animations a third person would notice; it is a dimension broadcast, which is fine for a
     * cosmetic packet and avoids depending on a chunk map.
     */
    public static void playAnimationForObservers(EntityPlayer source, ResourceLocation animationId, int fadeIn) {
        if (source == null || animationId == null) {
            return;
        }
        sendToObservers(source, new MessagePlayAnimation(source.getUniqueID(), animationId, fadeIn));
    }

    /** Stop the network animation on the target's client, fading out over {@code fadeOut} ticks. */
    public static void stopAnimation(EntityPlayerMP target, int fadeOut) {
        if (target == null) {
            return;
        }
        WRAPPER.sendTo(new MessageStopAnimation(target.getUniqueID(), fadeOut), target);
    }

    /** Same as {@link #stopAnimation}, but for everyone watching the player. */
    public static void stopAnimationForObservers(EntityPlayer source, int fadeOut) {
        if (source == null) {
            return;
        }
        sendToObservers(source, new MessageStopAnimation(source.getUniqueID(), fadeOut));
    }

    private static void sendToObservers(EntityPlayer source, IMessage message) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            // Client-side world (single player without an integrated server call path): nothing to do.
            return;
        }
        for (EntityPlayerMP observer : server.getPlayerList().getPlayers()) {
            if (observer.dimension == source.dimension) {
                WRAPPER.sendTo(message, observer);
            }
        }
    }
}
