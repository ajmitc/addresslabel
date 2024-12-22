package addresslabel.util;

import addresslabel.Model;
import addresslabel.Record;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Strings;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GoogleApi {
    private static final Logger logger = Logger.getLogger(GoogleApi.class.getName());

    private static final String APPLICATION_NAME = "AddressLabel/1.0";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + "/.addresslabel/tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(PeopleServiceScopes.CONTACTS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/client_secrets.json";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final Model model;
    private PeopleService service = null;

    public GoogleApi(Model model) {
        this.model = model;
    }

    private PeopleService initService() throws GeneralSecurityException, IOException, ExecutionException, InterruptedException, TimeoutException {
        if (service == null) {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service =
                    new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();


             /*
            SwingWorker<PeopleService, Void> worker = new SwingWorker<PeopleService, Void>() {
                @Override
                protected PeopleService doInBackground() throws Exception {
                    PeopleService service =
                            new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                                    .setApplicationName(APPLICATION_NAME)
                                    .build();
                    return service;
                }
            };
            try {
                service = worker.get(2, TimeUnit.MINUTES);
            }
            catch(TimeoutException timeoutException){
                logger.severe("Received timeout authenticating with Google");
                service = null;
            }
             */
        }
        return service;
    }

    public String getLoggedInUserEmail() throws IOException, GeneralSecurityException, ExecutionException, InterruptedException, TimeoutException {
        initService();

        if (service == null)
            return null;

        Person profile =
                service.people().get("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();
        return profile.getEmailAddresses().get(0).getValue();
    }

    public List<Record> pullGoogleContacts(String contactGroup){
        List<Record> records = new ArrayList<>();

        try {
            List<Person> contacts = requestContacts(contactGroup);

            for (Person person : contacts) {
                Record record = person2Record(person);
                if (record != null)
                    records.add(record);
            }
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Unable to request Google Contacts", e);
        }

        return records;
    }

    public Map<String, String> requestContactGroups() throws GeneralSecurityException, IOException, ExecutionException, InterruptedException, TimeoutException {
        initService();

        if (service == null)
            return new HashMap<>();

        ListContactGroupsResponse contactGroups =
                service.contactGroups()
                        .list()
                        .execute();

        Map<String, String> retMap = new HashMap<>();
        for (ContactGroup contactGroup: contactGroups.getContactGroups()){
            logger.info("Contact Group: '" + contactGroup.getName() + "' (" + contactGroup.getResourceName() + ")");
            /*
            if (contactGroup.getMemberResourceNames() != null && contactGroup.getMemberResourceNames().size() > 0) {
                for (String resName : contactGroup.getMemberResourceNames()) {
                    logger.info("   Member Resource Name: '" + resName + "'");
                }
            }
             */
            retMap.put(contactGroup.getName(), contactGroup.getResourceName());
        }

        return retMap;
    }

    public List<Person> requestContacts(final String contactGroup) throws GeneralSecurityException, IOException, ExecutionException, InterruptedException, TimeoutException {
        // Build a new authorized API client service.
        initService();

        if (service == null)
            return new ArrayList<>();

        List<Person> allContacts = new ArrayList<>();
        try {
            ListConnectionsResponse response = service.people().connections()
                    .list("people/me")
                    //.setPageSize(10)
                    .setPersonFields("names,addresses,memberships")
                    .setSources(List.of("READ_SOURCE_TYPE_CONTACT"))
                    .execute();

            for (Person contact : response.getConnections()) {
                allContacts.add(contact);
            }
            logger.info(String.format("=====  Received %s/%s Contacts for group people/me =====", allContacts.size(), response.getTotalItems(), contactGroup));

            while (response.getNextPageToken() != null) {
                response = service.people().connections()
                        .list("people/me")
                        //.setPageSize(10)
                        .setPersonFields("names,addresses,memberships")
                        .setSources(List.of("READ_SOURCE_TYPE_CONTACT"))
                        .setPageToken(response.getNextPageToken())
                        .execute();

                for (Person contact : response.getConnections()) {
                    allContacts.add(contact);
                }
                logger.info(String.format("=====  Received %s/%s Contacts for group people/me =====", allContacts.size(), response.getTotalItems(), contactGroup));
            }
        }
        catch(GoogleJsonResponseException e){
            logger.severe(e.getMessage());
        }

        // Print display name of connections if available.
        if (!allContacts.isEmpty()) {
            for (Person person : allContacts) {
                List<Name> names = person.getNames();
                if (names != null && !names.isEmpty()) {
                    logger.info("Name: " + person.getNames().get(0).getDisplayName());
                } else {
                    logger.info("No names available for connection.");
                }
                List<Address> addresses = person.getAddresses();
                if (addresses != null && !addresses.isEmpty()){
                    logger.info("  Address: " + person.getAddresses().get(0).getFormattedValue());
                }
                List<Membership> memberships = person.getMemberships();
                if (memberships != null && !memberships.isEmpty()){
                    logger.info("  Memberships: " +
                            memberships.stream()
                                    .map(m ->
                                            m.getContactGroupMembership().getContactGroupId() + ": " +
                                            m.getContactGroupMembership().getContactGroupResourceName()
                                    )
                                    .collect(Collectors.joining(", ")));
                }
            }
            allContacts =
                    allContacts.stream()
                            .filter(connection ->
                                    connection.getMemberships().stream()
                                            .anyMatch(m ->
                                                    m.getContactGroupMembership().getContactGroupResourceName().equalsIgnoreCase(contactGroup)))
                            .collect(Collectors.toList());
            logger.info(String.format("=====  Filtered Contacts for %s =====", contactGroup));
            for (Person person : allContacts) {
                List<Name> names = person.getNames();
                if (names != null && names.size() > 0) {
                    logger.info("Name: " + person.getNames().get(0).getDisplayName());
                } else {
                    logger.info("No names available for connection.");
                }
                List<Address> addresses = person.getAddresses();
                if (addresses != null && addresses.size() > 0){
                    logger.info("  Address: " + person.getAddresses().get(0).getFormattedValue());
                }
                List<Membership> memberships = person.getMemberships();
                if (memberships != null && memberships.size() > 0){
                    logger.info("  Memberships: " + String.join(", ", memberships.stream().map(m -> m.getContactGroupMembership().getContactGroupResourceName()).collect(Collectors.toList())));
                }
            }
        } else {
            System.out.println("No connections found.");
        }
        return allContacts;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleApi.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Record person2Record(Person person){
        Record record = new Record(model.getDefaultTemplate());

        List<Name> names = person.getNames();
        List<Address> addresses = person.getAddresses();

        if (names == null){
            return null;
        }

        for (Name name : names) {
            record.getData().put(Record.TITLE, name.getHonorificPrefix());
            record.getData().put(Record.NAME, name.getDisplayName());
            record.getData().put(Record.FIRST_NAME, name.getGivenName());
            record.getData().put(Record.LAST_NAME, name.getFamilyName());
            record.getData().put(Record.SUFFIX, name.getHonorificSuffix());
            break;
        }

        if (addresses != null) {
            for (Address address : addresses) {
                record.getData().put(Record.ADDRESS, address.getFormattedValue());
                record.getData().put(Record.ADDRESS_STREET_1, address.getStreetAddress());
                record.getData().put(Record.ADDRESS_STREET_2, address.getExtendedAddress());
                record.getData().put(Record.ADDRESS_CITY, address.getCity());
                record.getData().put(Record.ADDRESS_STATE, address.getRegion());
                record.getData().put(Record.ADDRESS_COUNTRY, address.getCountry());
                record.getData().put(Record.ADDRESS_ZIP, address.getPostalCode());
                break;
            }
        }

        // Make sure the values are well-formed
        for (String key: record.getData().keySet()){
            if (record.getData().get(key) != null)
                record.getData().put(key, record.getData().get(key).trim());
        }

        // check if the city is blank, but the state is "city, state"
        if (Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_CITY)) &&
                !Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_STATE)) &&
                record.getData().get(Record.ADDRESS_STATE).contains(",")) {
            String citystate = record.getData().get(Record.ADDRESS_STATE);
            String[] parts = citystate.split(",");
            record.getData().put(Record.ADDRESS_CITY, parts[0].trim());
            record.getData().put(Record.ADDRESS_STATE, parts[1].trim());
        }

        // check if street1 is blank, but address is not, then try to parse the address
        if (Strings.isNullOrEmpty(Record.ADDRESS_STREET_1) &&
                !Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS))) {
            Map<String, String> parsedAddress = AddressParser.parse(record.getData().get(Record.ADDRESS));
            if (parsedAddress != null){
                record.getData().put(Record.ADDRESS_STREET_1, parsedAddress.get(Record.ADDRESS_STREET_1).trim());

                if (Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_CITY)) && parsedAddress.containsKey(Record.ADDRESS_CITY)) {
                    record.getData().put(Record.ADDRESS_CITY, parsedAddress.get(Record.ADDRESS_CITY).trim());
                }
                if (Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_STATE)) && parsedAddress.containsKey(Record.ADDRESS_STATE)) {
                    record.getData().put(Record.ADDRESS_STATE, parsedAddress.get(Record.ADDRESS_STATE).trim());
                }
                if (Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_ZIP)) && parsedAddress.containsKey(Record.ADDRESS_ZIP)) {
                    record.getData().put(Record.ADDRESS_ZIP, parsedAddress.get(Record.ADDRESS_ZIP).trim());
                }
                if (Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_COUNTRY)) && parsedAddress.containsKey(Record.ADDRESS_COUNTRY)) {
                    record.getData().put(Record.ADDRESS_COUNTRY, parsedAddress.get(Record.ADDRESS_COUNTRY).trim());
                }
            }
        }

        // check if the street1 contains more than just the street ie. "123 myroad st, city, state zip"
        if (!Strings.isNullOrEmpty(record.getData().get(Record.ADDRESS_STREET_1)) &&
                record.getData().get(Record.ADDRESS_STREET_1).indexOf(',') > 0) {
            record.getData().put(
                    Record.ADDRESS_STREET_1,
                    record.getData().get(Record.ADDRESS_STREET_1)
                            .substring(0, record.getData().get(Record.ADDRESS_STREET_1).indexOf(',')).trim());
        }

        return record;
    }

    public void logout() throws IOException {
        // Remove tokens directory
        Path pathToBeDeleted = new File(TOKENS_DIRECTORY_PATH).toPath();
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        service = null;
    }
}

