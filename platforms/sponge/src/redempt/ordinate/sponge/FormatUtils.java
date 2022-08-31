package redempt.ordinate.sponge;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class FormatUtils {

    /**
     *
     * @param input The input string
     * @return The colored string, replacing color codes using ampersands with proper codes
     */
    public static String color(String input) {
        return LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.AMPERSAND_CHAR)
                .build().deserialize(input).content();
    }
}
