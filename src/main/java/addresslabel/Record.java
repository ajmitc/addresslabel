package addresslabel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.*;

import addresslabel.util.Logger;

public class Record {
    public static final String TITLE = "title";
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first name";
    public static final String MIDDLE_NAME = "middle name";
    public static final String LAST_NAME = "last name";
    public static final String SUFFIX = "suffix";
    public static final String ADDRESS = "home address";
    public static final String ADDRESS_STREET_1 = "home street";
    public static final String ADDRESS_STREET_2 = "home street 2";
    public static final String ADDRESS_CITY = "home city";
    public static final String ADDRESS_STATE = "home state";
    public static final String ADDRESS_ZIP = "home postal code";
    public static final String ADDRESS_COUNTRY = "home country";
    public static final String ADDRESS_COUNTRY_NOT_USA = "home country not usa";

    public static final String[] LABELS = {
            TITLE,
            NAME,
            FIRST_NAME,
            MIDDLE_NAME,
            LAST_NAME,
            SUFFIX,
            ADDRESS,
            ADDRESS_STREET_1,
            ADDRESS_STREET_2,
            ADDRESS_CITY,
            ADDRESS_STATE,
            ADDRESS_ZIP,
            ADDRESS_COUNTRY,
            ADDRESS_COUNTRY_NOT_USA
    };

    // These labels do not have corresponding values, but add functionality to existing labels
    public static final List<String> LABEL_IGNORE = new ArrayList<>();

    // Mapping of an imported header to a standard label
    public static final Map<String, String[]> LABEL_MAPPING = new HashMap<>();

    // Labels values that match USA
    public static final List<String> USA_VALUES = new ArrayList<>();

    public static final List<String> PARSE_ADDR_REGEXPS = new ArrayList<>();
    public static final List<Pattern> PARSE_ADDR_PATTERNS = new ArrayList<>();


    static {
        // Ignored labels
        LABEL_IGNORE.add(ADDRESS_COUNTRY_NOT_USA);

        // Label mapping
        LABEL_MAPPING.put(ADDRESS, new String[]{"address"});
        LABEL_MAPPING.put(ADDRESS_STREET_1, new String[]{"street", "street 1", "Address 1 - Street", "Address 2 - Street", "Other Street"});
        LABEL_MAPPING.put(ADDRESS_STREET_2, new String[]{"street 2", "Address 1 - Street 2", "Address 2 - Street 2", "Other Street 2"});
        LABEL_MAPPING.put(ADDRESS_CITY, new String[]{"city", "Address 1 - City", "Address 2 - City", "Other City"});
        LABEL_MAPPING.put(ADDRESS_STATE, new String[]{"state", "Address 1 - Region", "Address 2 - Region", "Other State"});
        LABEL_MAPPING.put(ADDRESS_ZIP, new String[]{"zip", "zipcode", "Address 1 - Postal Code", "Address 2 - Postal Code", "Other Postal Code"});
        LABEL_MAPPING.put(ADDRESS_COUNTRY, new String[]{"country", "Address 1 - Country", "Other Country"});

        USA_VALUES.add("");
        USA_VALUES.add("us");
        USA_VALUES.add("usa");
        USA_VALUES.add("united states of america");

        // USA Address (Default)
        PARSE_ADDR_REGEXPS.add("(?<name>[a-zA-Z \\.]+)\n(?<street1>[0-9a-zA-Z \\.]+)\n(?<city>[a-zA-Z ]+), *(?<state>[A-Z]{2}) +(?<zip>[0-9-]{5,10})");
        PARSE_ADDR_REGEXPS.add("(?<name>[a-zA-Z \\.]+)\n(?<street1>[0-9a-zA-Z \\.]+)\n(?<street2>[0-9a-zA-Z \\.]+)\n(?<city>[a-zA-Z ]+), *(?<state>[A-Z]{2}) +(?<zip>[0-9-]{5,10})");
        // Germany
        PARSE_ADDR_REGEXPS.add("(?<name>[a-zA-Z \\.]+)\n(?<street1>[0-9a-zA-Z \\.]+)\n(?<street2>[0-9a-zA-Z \\.]+)\n(?<zip>[0-9-]{5}), (?<city>[a-zA-Z ]+)\n(?<country>(?i)germany(?-i))");

        for (String regex : PARSE_ADDR_REGEXPS) {
            PARSE_ADDR_PATTERNS.add(Pattern.compile(regex));
        }
    }


    public static final String REGEXP_MATCH_LABEL_TAG = "\\{[a-zA-Z0-9 _]+\\}";


    private static final Logger logger = Logger.getLogger(Record.class);
    private Map<String, String> data;
    // If null, use default label template in app 
    private String template;
    private String defTemplate;
    // Displayed text
    private String display;


    /**
     * @param record      Map of header to value
     * @param defTemplate String defining the default template to use for this record
     */
    public Record(Map<String, String> record, String defTemplate) {
        data = new HashMap<>();
        clearData();
        _mapRecord(record);
        template = null;
        this.defTemplate = defTemplate;
        display = null;
    }

    public Record(String defTemplate) {
        this(new HashMap<>(), defTemplate);
    }

    /**
     * Two records are equal if their data is equal.
     *
     * @param other
     * @return
     */
    public boolean equals(final Record other) {
        for (String key : data.keySet()) {
            if (!other.getData().containsKey(key)) {
                return false;
            }
            String myValue = data.get(key);
            String otherValue = other.getData().get(key);
            if (!myValue.equals(otherValue)) {
                return false;
            }
        }

        for (String key : other.getData().keySet()) {
            if (!data.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    public void clearData() {
        data.clear();

        // We still want the standard labels
        for (String lbl : LABELS) {
            if (!LABEL_IGNORE.contains(lbl)) // { FIRST_NAME: "Aaron", LAST_NAME: "Mitchell" }
            {
                data.put(lbl, "");
            }
        }
    }


    /**
     * Return True if 'text' is found within this Record, False otherwise
     */
    public boolean search(String text) {
        return getDisplay().toLowerCase().indexOf(text.toLowerCase()) >= 0;
    }


    private void _mapRecord(Map<String, String> record) {
        for (String key : record.keySet()) {
            String value = record.get(key);
            String mappedKey = mapRecordKey(key);
            if (value != null && !value.trim().isEmpty())
                data.put(mappedKey, value);
            data.put(key, value);
        }
        _autoFillInFields();
    }


    private String mapRecordKey(String header) {
        header = header.trim();
        for (String lbl : LABELS) {
            if (lbl.equalsIgnoreCase(header) || lbl.replace(" ", "_").equalsIgnoreCase(header))
                return lbl;
        }
        for (String key : LABEL_MAPPING.keySet()) {
            String[] options = LABEL_MAPPING.get(key);
            for (String option : options) {
                if (option.equalsIgnoreCase(header))
                    return key;
            }
        }
        return header;
    }


    /**
     * Some record fields are messed up, attempt to fix them
     */
    private void _autoFillInFields() {
        // Set default fields
        if (get(ADDRESS_COUNTRY).equals("")) {
            data.put(ADDRESS_COUNTRY, "United States of America");
        }


        // Detect bad record
        // Every record should have a street
        List<String> missing = new ArrayList<String>();
        if (get(ADDRESS_STREET_1).equals("")) {
            missing.add(ADDRESS_STREET_1);
        }

        if (get(ADDRESS_CITY).equals("")) {
            missing.add(ADDRESS_CITY);
        }

        if (get(ADDRESS_ZIP).equals("")) {
            missing.add(ADDRESS_ZIP);
        }

        // Mandatory fields are not empty, do nothing
        if (missing.size() == 0)
            return;

        // Check if the ADDRESS field is not empty
        String address = get(ADDRESS);
        if (!address.equals("")) {
            if (_parseAddressString(address))
                return;
        }

        // Check if OTHER ADDRESS field(s) are not empty
        address = get("other address");
        if (address != null && !address.equals("")) {
            if (_parseAddressString(address))
                return;
        }
    }


    /**
     * @return true if address parsed successfully, false otherwise
     */
    private boolean _parseAddressString(String address) {
        for (Pattern pattern : PARSE_ADDR_PATTERNS) {
            Matcher matcher = pattern.matcher(address);
            if (matcher != null) {
                if (matcher.matches()) {
                    if (data.get(ADDRESS_STREET_1).equals("")) {
                        data.put(ADDRESS_STREET_1, matcher.group("street1"));
                    }

                    if (data.get(ADDRESS_STREET_2).equals("") && matcher.group("street2") != null) {
                        data.put(ADDRESS_STREET_2, matcher.group("street2"));
                    }

                    if (data.get(ADDRESS_CITY).equals("") && matcher.group("city") != null) {
                        data.put(ADDRESS_CITY, matcher.group("city"));
                    }

                    if (data.get(ADDRESS_STATE).equals("") && matcher.group("state") != null) {
                        data.put(ADDRESS_STATE, matcher.group("state"));
                    }

                    if (data.get(ADDRESS_ZIP).equals("") && matcher.group("zip") != null) {
                        data.put(ADDRESS_ZIP, matcher.group("zip"));
                    }
                    return true;
                }
            }
        }
        return false;
    }


    public String format(String template) {
        for (String lbl : LABELS) {
            if (lbl.equals(ADDRESS_COUNTRY_NOT_USA)) {
                if (data.containsKey(ADDRESS_COUNTRY) &&
                        data.get(ADDRESS_COUNTRY) != null &&
                        !USA_VALUES.contains(data.get(ADDRESS_COUNTRY).toLowerCase()))
                    template = template.replaceAll("\\{" + lbl + "\\}", data.get(ADDRESS_COUNTRY));
                else
                    template = template.replaceAll("\\{" + lbl + "\\}", "");
            } else {
                String replacementValue = data.containsKey(lbl) ? data.get(lbl) : "";
                if (replacementValue == null)
                    replacementValue = "";
                template = template.replaceAll("\\{" + lbl + "\\}", replacementValue);
            }
        }
        template = template.replaceAll(REGEXP_MATCH_LABEL_TAG, "");
        while (template.indexOf("  ") >= 0) {
            template = template.replaceAll("  +", " ");
        }
        template = template.replaceAll("\n\n", "\n");
        return template.trim();
    }


    public String getDisplay() {
        if (display == null)
            display = isUsed() ? format(template != null ? template : defTemplate) : "";
        return display;
    }


    public void setDisplay(String d) {
        display = d;
    }


    public String getTemplate() {
        return template != null ? template : defTemplate;
    }


    public void setTemplate(String t) {
        template = t;
        display = null;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String get(String key) {
        if (!data.containsKey(key))
            return null;
        return data.get(key);
    }

    /**
     * Return the values of this Record in a String[] with the order given by header
     *
     * @param header
     * @return
     */
    public String[] getValues(List<String> header) {
        String[] ret = new String[header.size()];
        int i = 0;
        for (String h : header) {
            if (data.containsKey(h)) {
                ret[i++] = data.get(h);
            } else {
                ret[i++] = "";
            }
        }
        return ret;
    }

    /**
     * Determines if this record has non-default data
     *
     * @return
     */
    public boolean isUsed() {
        for (String key : data.keySet()) {
            if (!key.equals(ADDRESS_COUNTRY) && data.get(key) != null && !data.get(key).equals("")) {
                return true;
            }
        }
        return false;
    }

    public void setDefaultLabelTemplate(String defLabelTemplate) {
        defTemplate = defLabelTemplate;
    }

    public String getString() {
        return data.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", "));
    }
}

