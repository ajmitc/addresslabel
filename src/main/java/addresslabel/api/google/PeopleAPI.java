package addresslabel.api.google;

import addresslabel.Model;
import addresslabel.Record;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class PeopleAPI {
    private static Logger logger = Logger.getLogger(PeopleAPI.class.getName());

    private static final String APPLICATION_NAME = "Address Label Easy Button";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String PERSON_FIELDS = "names,addresses,memberships,userDefined,locations";
    //private static final String REQUEST_MASK_FIELDS = "person.names,person.addresses";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(PeopleServiceScopes.CONTACTS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "./credentials.json";

    public static List<Record> getContacts(Model model, String username, List<String> requiredMemberships) throws IOException, GeneralSecurityException{
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        PeopleService service = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(username, HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<Person> allPersons = new ArrayList<>();
        while (true) {
            ListConnectionsResponse response = service.people().connections()
                    .list("people/me")
                    .setPageSize(50)
                    .setPersonFields(PERSON_FIELDS)
                    .execute();

            List<Person> connections = response.getConnections();
            if (connections != null && connections.size() > 0) {
                allPersons.addAll(connections);
                if (connections.size() < 50)
                    break;
            }
            else
                break;
        }
        if (!allPersons.isEmpty())
            return toRecords(model, service, allPersons, requiredMemberships);
        logger.warning("No connections found.");
        return null;
    }

    private static List<Record> toRecords(Model model, PeopleService service, List<Person> connections, List<String> requiredMemberships){
        List<Record> records = new ArrayList<>();
        for (Person person: connections){
            Record record = toRecord(model, person, requiredMemberships);
            if (record != null)
                records.add(record);
        }
            /*
        try {
            GetPeopleResponse getPeopleResponse =
                    service.people().getBatchGet()
                            //.setPersonFields(PERSON_FIELDS)
                            .setRequestMaskIncludeField(REQUEST_MASK_FIELDS)
                            .setResourceNames(connections.stream().map(p -> p.getResourceName()).collect(Collectors.toList()))
                            .execute();
            for (PersonResponse personResponse : getPeopleResponse.getResponses()) {
                records.add(toRecord(model, service, personResponse.getPerson()));
            }
        }
        catch (IOException ioException){
            logger.severe("Unable to get People info: " + ioException.getMessage());
        }
             */
        return records;
    }

    private static Record toRecord(Model model, Person person, List<String> requiredMemberships){
        if (requiredMemberships != null && !requiredMemberships.isEmpty()) {
            List<Membership> memberships = person.getMemberships();
            if (memberships != null) {
                for (Membership membership : memberships) {
                    ContactGroupMembership contactGroupMembership = membership.getContactGroupMembership();
                    // ie. "myContacts"
                    String groupId = contactGroupMembership.getContactGroupId();
                    // ie. "contactGroups/myContacts"
                    String groupResourceName = contactGroupMembership.getContactGroupResourceName();

                    if (!requiredMemberships.contains(groupId) &&
                            !requiredMemberships.contains(groupResourceName)){
                        // This person does not belong to a required membership, so do not create a record for them
                        return null;
                    }
                }
            }
        }

        Record record = new Record(model.getDefaultTemplate());

        List<Address> addresses = person.getAddresses();
        if (addresses != null) {
            for (Address address : addresses) {
                record.getData().put(Record.ADDRESS, address.getFormattedValue());
                record.getData().put(Record.ADDRESS_STREET_1, address.getStreetAddress());
                record.getData().put(Record.ADDRESS_CITY, address.getCity());
                record.getData().put(Record.ADDRESS_STATE, address.getRegion());
                record.getData().put(Record.ADDRESS_ZIP, address.getPostalCode());
                if (!Record.USA_VALUES.contains(address.getCountry().toLowerCase()))
                    record.getData().put(Record.ADDRESS_COUNTRY_NOT_USA, address.getCountry());
                break;
            }
        }

        List<Name> names = person.getNames();
        if (names != null && names.size() > 0) {
            for (Name name: person.getNames()) {
                record.getData().put(Record.NAME, name.getDisplayName());
                record.getData().put(Record.FIRST_NAME, name.getGivenName());
                record.getData().put(Record.LAST_NAME, name.getFamilyName());
                if (name.getMiddleName() != null && !name.getMiddleName().isEmpty())
                    record.getData().put(Record.MIDDLE_NAME, name.getMiddleName());
                logger.info("Name: " + person.getNames().get(0).getDisplayName());
            }
        } else {
            logger.warning("No names available for connection.");
        }

        return record;
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(String username, final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = PeopleAPI.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(new File(CREDENTIALS_FILE_PATH));
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(username);
    }

}
