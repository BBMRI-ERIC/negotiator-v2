package de.samply.bbmri.negotiator.notification;

import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import de.samply.common.mailing.EmailBuilder;

import java.util.*;

/**
 * Created by paul on 01.12.16.
 */
public class Notification {

    private List<Person> addressees;

    private String locale;

    private String subject;

    private Map<String, String> parameters = new HashMap<>();

    public Notification() {
        addressees = new ArrayList<>();
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public List<Person> getAddressees() {
        return Collections.unmodifiableList(addressees);
    }

    public void addAddressee(Person person) {
        addressees.add(person);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
