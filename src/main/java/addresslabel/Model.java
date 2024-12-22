package addresslabel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

import addresslabel.util.GoogleApi;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.*;

import addresslabel.template.*;
import addresslabel.util.Logger;
import addresslabel.util.SearchResult;
import addresslabel.util.Util;


public class Model {
    public static final String VERSION = "1.0.0";

    public static final LabelSheetTemplate[] LABEL_SHEET_TEMPLATES = {
            new Avery5160LabelSheetTemplate()
    };

    private static final Logger logger = Logger.getLogger(Model.class);

    private LabelSheetTemplate labelSheetTemplate; // selected template
    private List<Record> records;
    private String loadedFilepath;
    private String loadedProjectFilepath;
    private int page;
    private String defLabelTemplate;
    private List<SearchResult> searchResults;
    private int searchResultsIdx;
    private List<CountryLabelTemplate> countryLabelTemplates;

    private GoogleApi googleApi;

    public Model() {
        labelSheetTemplate = LABEL_SHEET_TEMPLATES[0];
        records = new ArrayList<>();
        loadedFilepath = null;
        loadedProjectFilepath = null;
        page = 0;  // Currently displayed page

        // default format template
        defLabelTemplate = "{" + Record.TITLE + "} {" + Record.FIRST_NAME + "} {" + Record.MIDDLE_NAME + "} {" + Record.LAST_NAME + "} {" + Record.SUFFIX + "}\n";
        defLabelTemplate += "{" + Record.ADDRESS_STREET_1 + "}\n";
        defLabelTemplate += "{" + Record.ADDRESS_STREET_2 + "}\n";
        defLabelTemplate += "{" + Record.ADDRESS_CITY + "}, {" + Record.ADDRESS_STATE + "} {" + Record.ADDRESS_ZIP + "}\n";
        defLabelTemplate += "{" + Record.ADDRESS_COUNTRY_NOT_USA + "}\n";

        searchResults = new ArrayList<>();
        searchResultsIdx = 0;

        countryLabelTemplates = new ArrayList<>();
        countryLabelTemplates.add(new GermanyLabelTemplate());

        addEmptyRecords(getRecordsPerPage());

        googleApi = new GoogleApi(this);
    }

    public boolean loadContactsFromFile(String filename) {
        if (filename.endsWith(".csv"))
            return loadContactsCsv(filename);
        logger.error("Unsupported file format");
        return false;
    }


    /**
     * Load a CSV Contact list.  The first row must be a header, the delimiter must be a comma ',' and record delimiter must be newline
     */
    public boolean loadContactsCsv(String filepath) {
        logger.info("Loading " + filepath);

        try {
            records = getRecordsFromCsv(filepath);

            // Add Record objects to fill the page
            fillPageWithEmptyRecords();

            page = 0;
            loadedFilepath = filepath;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Load a CSV Contact list.  The first row must be a header, the delimiter must be a comma ',' and record delimiter must be newline
     */
    public List<Record> getRecordsFromCsv(String filepath) throws Exception {
        logger.info("Loading " + filepath);

        List<Record> records = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(filepath));
        List<String> header = null;
        String[] entry;
        while ((entry = reader.readNext()) != null) {
            List<String> fields = Arrays.asList(entry);
            if (header == null)
                header = fields;
            else {
                Map<String, String> data = Util.zipToMap(header, fields);
                Record record = new Record(data, defLabelTemplate);
                records.add(record);

                // attempt to set a different country label template, if appropriate
                for (CountryLabelTemplate clt : countryLabelTemplates) {
                    if (clt.matches(record.get(Record.ADDRESS_COUNTRY))) {
                        record.setDefaultLabelTemplate(clt.template);
                        break;
                    }
                }
            }
        }

        return records;
    }

    public boolean writeCsv() {
        try {
            logger.info("Writing CSV file: " + loadedFilepath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(loadedFilepath));
            CSVWriter csvWriter = new CSVWriter(writer);
            // Write the header
            Set<String> headerSet = records.size() > 0 ? records.get(0).getData().keySet() : new HashSet<>();
            List<String> header = new ArrayList<>(headerSet);
            csvWriter.writeNext(header.toArray(new String[0]));
            // Write the values
            for (Record record : records) {
                String[] fields = record.getValues(header);
                csvWriter.writeNext(fields);
            }
            csvWriter.close();
            logger.info("   Wrote " + records.size() + " records");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public boolean writeProject() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(loadedProjectFilepath));
            for (Record record : records) {
                writer.write("::Record::\n");
                writer.write("::data::\n");
                for (String key : record.getData().keySet()) {
                    writer.write("[");
                    writer.write(key);
                    writer.write("] ");
                    writer.write(record.getData().get(key));
                    writer.write("\n");
                }
                writer.write("::template::\n");
                writer.write(record.getTemplate());
                writer.write("\n");
            }
            writer.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public boolean loadProject(String projectFilepath) {
        try {
            clearRecords();
            BufferedReader reader = new BufferedReader(new FileReader(projectFilepath));
            Record record = null;
            boolean readFields = false;
            boolean readTemplate = false;
            String fieldKey = null;
            String fieldValue = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("::Record::")) {
                    record = new Record(defLabelTemplate);
                    records.add(record);
                } else if (line.equals("::data::")) {
                    readFields = true;
                    readTemplate = false;
                } else if (line.equals("::template::")) {
                    readFields = false;
                    readTemplate = true;
                    record.setTemplate("");
                } else if (readFields) {
                    if (line.startsWith("[")) {
                        fieldKey = line.substring(1, line.indexOf("]")).trim();
                        fieldValue = line.substring(line.indexOf("]") + 1).trim();
                        record.getData().put(fieldKey, fieldValue);
                    } else {
                        fieldValue = record.getData().get(fieldKey);
                        record.getData().put(fieldKey, fieldValue + "\n" + line.trim());
                    }
                } else if (readTemplate) {
                    record.setTemplate(record.getTemplate() + line + "\n");
                }
            }
            loadedFilepath = projectFilepath;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public void addPageOfEmptyRecords() {
        addEmptyRecords(getRecordsPerPage());
    }

    /**
     * Add record objects to fill the page
     */
    public void fillPageWithEmptyRecords() {
        int recordsPerPage = getTemplate().getRows() * getTemplate().getColumns();
        int numPages = (int) (Math.ceil((float) records.size() / (float) recordsPerPage));
        int totalRecords = recordsPerPage * numPages;
        int missingRecords = totalRecords - records.size();
        addEmptyRecords(missingRecords);
    }

    public void addEmptyRecords(int num) {
        for (int i = 0; i < num; ++i)
            records.add(new Record(defLabelTemplate));
    }

    public List<Record> getRecords() {
        return records;
    }

    public List<Record> getUsedRecords() {
        List<Record> used = new ArrayList<Record>();
        for (Record r : records) {
            if (r.isUsed())
                used.add(r);
        }
        return used;
    }

    public void clearRecords() {
        records.clear();
        loadedFilepath = null;
        loadedProjectFilepath = null;
        page = 0;
    }

    public void sortRecords(final String recordKey) {
        Collections.sort(records, new Comparator<Record>() {
            public int compare(Record r1, Record r2) {
                if (!r1.isUsed()) {
                    if (!r2.isUsed())
                        return 0;
                    return 1;
                } else if (!r2.isUsed()) {
                    return -1;
                }
                return r1.get(recordKey).compareTo(r2.get(recordKey));
            }

            public boolean equals(Object obj) {
                return false;
            }
        });
    }

    public int getPage() {
        return page;
    }

    public void setPage(int p) {
        page = p;
        if (page < 0) page = 0;
        if (page >= getNumPagesToFitRecords()) page = getNumPagesToFitRecords() - 1;
    }

    public void adjPage(int v) {
        setPage(page + v);
    }

    public int getRecordsPerPage() {
        return getTemplate().getRows() * getTemplate().getColumns();
    }

    public int getNumPagesToFitRecords() {
        int pages = (int) Math.ceil((float) records.size() / (float) getRecordsPerPage());
        if (pages == 0) {
            pages = 1;
        }
        return pages;
    }

    public String getDefaultTemplate() {
        return defLabelTemplate;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public int getSearchResultsIndex() {
        return searchResultsIdx;
    }

    public void setSearchResultsIndex(int i) {
        searchResultsIdx = i;
    }

    public void setTemplate(LabelSheetTemplate t) {
        labelSheetTemplate = t;
    }

    public LabelSheetTemplate getTemplate() {
        return labelSheetTemplate;
    }

    public String getLoadedFilepath() {
        return loadedFilepath;
    }

    public void setLoadedFilepath(String fp) {
        loadedFilepath = fp;
    }

    public String getLoadedProjectFilepath() {
        return loadedProjectFilepath;
    }

    public void setLoadedProjectFilepath(String fp) {
        loadedProjectFilepath = fp;
    }


    public static class CountryLabelTemplate {
        public List<String> matchCountry = new ArrayList<String>();
        public String template;

        public CountryLabelTemplate() {

        }

        public boolean matches(String country) {
            for (String c : matchCountry) {
                if (c.equalsIgnoreCase(country))
                    return true;
            }
            return false;
        }
    }


    public static class GermanyLabelTemplate extends CountryLabelTemplate {
        public GermanyLabelTemplate() {
            super();
            matchCountry.add("Germany");
            matchCountry.add("Deutschland");
            template = "{" + Record.TITLE + "} {" + Record.FIRST_NAME + "} {" + Record.MIDDLE_NAME + "} {" + Record.LAST_NAME + "} {" + Record.SUFFIX + "}\n";
            template += "{" + Record.ADDRESS_STREET_1 + "}\n";
            template += "{" + Record.ADDRESS_STREET_2 + "}\n";
            template += "{" + Record.ADDRESS_ZIP + "}, {" + Record.ADDRESS_CITY + "}\n";
            template += "{" + Record.ADDRESS_COUNTRY + "}\n";
        }
    }

    public GoogleApi getGoogleApi() {
        return googleApi;
    }
}

