package addresslabel.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addresslabel.Record;

public class AddressParser {
    private static Map<String, Pattern> PATTERNS = Map.of(
            // 123 Main St, Anytown, CA 12345
            "ADDR1", Pattern.compile("(\\d+) (.*), (.*), (.*) (\\d+)"),
            // PO Box 42015 Phoenix,  AZ 85080 United States of America
            "POBOX", Pattern.compile("PO Box (\\d+) (.*), (.*) (\\d+) (.*)")
    );

    public static Map<String, String> parse(String address) {
        Map<String, String> map = new HashMap<>();
        for (String key: PATTERNS.keySet()){
            Pattern pattern = PATTERNS.get(key);
            Matcher matcher = pattern.matcher(address);

            if (matcher.find()) {
                if (key.startsWith("ADDR")) {
                    // Number + Street Name
                    map.put(Record.ADDRESS_STREET_1, matcher.group(1) + " " + matcher.group(2));
                    map.put(Record.ADDRESS_CITY, matcher.group(3));
                    map.put(Record.ADDRESS_STATE, matcher.group(4));
                    map.put(Record.ADDRESS_ZIP, matcher.group(5));
                }
                else if (key.startsWith("POBOX")) {
                    map.put(Record.ADDRESS_STREET_1, "PO Box " + matcher.group(1));
                    map.put(Record.ADDRESS_CITY, matcher.group(2));
                    map.put(Record.ADDRESS_STATE, matcher.group(3));
                    map.put(Record.ADDRESS_ZIP, matcher.group(4));
                    map.put(Record.ADDRESS_COUNTRY, matcher.group(5));
                }
                return map;
            }
        }
        return null;
    }
}
