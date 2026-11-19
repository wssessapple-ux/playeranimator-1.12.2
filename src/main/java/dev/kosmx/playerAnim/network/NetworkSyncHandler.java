package dev.kosmx.playerAnim.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Server-side sync hooks: catch players up with animations that started before they joined
 * or entered the dimension, and drop tracked state when a source player leaves.
 * <p>
 * Registered on the Forge event bus from {@code PlayerAnimatorMod#preInit}. The handlers are
 * no-ops off the dedicated server path; the active-animation table lives server-side only.
 */
public final class NetworkSyncHandler {

    public static final NetworkSyncHandler INSTANCE = new NetworkSyncHandler();

    private NetworkSyncHandler() {
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (player instanceof EntityPlayerMP) {
            PlayerAnimatorNetwork.resendToObserver((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        if (player instanceof EntityPlayerMP) {
            PlayerAnimatorNetwork.resendToObserver((EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerAnimatorNetwork.clearSource(event.player.getUniqueID());
    }
}
