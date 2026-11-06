package dev.kosmx.playerAnim.core.data;

import dev.kosmx.playerAnim.core.data.gson.GeckoLibSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringTest {

    @Test
    public void camelTest() {
        String str = "camel_case_string";
        String converted = "camelCaseString";
        assertEquals(converted, GeckoLibSerializer.snake2Camel(str));
    }
}
