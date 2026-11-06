package dev.kosmx.playerAnim.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EaseTest {

    @Test
    public void endpoints() {
        for (Ease ease : Ease.values()) {
            if (ease == Ease.CONSTANT) continue;
            assertEquals("ease " + ease + " at 0", 0f, ease.invoke(0f), 1e-4f);
            assertEquals("ease " + ease + " at 1", 1f, ease.invoke(1f), 1e-4f);
            assertEquals("id round trip " + ease, ease, Ease.getEase(ease.getId()));
        }
    }

    @Test
    public void knownValues() {
        assertEquals(0.5f, Ease.LINEAR.invoke(0.5f), 1e-6f);
        assertEquals(0.5f, Ease.INOUTSINE.invoke(0.5f), 1e-6f);
        assertEquals(0f, Ease.CONSTANT.invoke(0.5f), 1e-6f);
        // Upstream maps OUTBOUNCE to Easing::outBack (known upstream quirk, kept for parity)
        assertEquals(Easing.outBack(0.25f), Ease.OUTBOUNCE.invoke(0.25f), 1e-6f);
    }

    @Test
    public void stringLookup() {
        assertEquals(Ease.INOUTSINE, Easing.easeFromString("easeInOutSine"));
        assertEquals(Ease.LINEAR, Easing.easeFromString("linear"));
        assertEquals(Ease.CONSTANT, Easing.easeFromString("step"));
        assertEquals(Ease.LINEAR, Easing.easeFromString("unknown_ease"));
        assertTrue(Ease.values().length > 30);
    }
}
