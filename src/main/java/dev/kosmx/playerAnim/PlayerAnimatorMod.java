package dev.kosmx.playerAnim;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 1.12.2 entry point. The library is client-only: animations are evaluated by
 * every client for all visible players, no packets are involved.
 */
@Mod(
        modid = PlayerAnimatorMod.MOD_ID,
        name = "Player Animator",
        version = Tags.VERSION,
        clientSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class PlayerAnimatorMod {

    public static final String MOD_ID = "playeranimator";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public PlayerAnimatorMod() {
        LOGGER.info("Player Animator {} initialized (client-only, no network layer)", Tags.VERSION);
    }
}
