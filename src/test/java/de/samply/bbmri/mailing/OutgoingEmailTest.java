package de.samply.bbmri.mailing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test OutgoingEmail building for sending")
class OutgoingEmailTest {

    OutgoingEmail mail_;

    @BeforeEach
    void setUp() {
        mail_ = new OutgoingEmail();
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Test setting mail subject")
    @ParameterizedTest(name = "Test Mail Subject \"{0}\".")
    @ValueSource(strings = { "Negotiator Test Email", "", "Text with special characters: ä,ü,ö,$,%,&,[,],{,},?,ß,€,;,*,+,#,-,_,/,:,.;\\" })
    void testSettingSubject(String subject) {
        mail_.setSubject(subject);
        assertEquals(subject, mail_.getSubject());
    }

    @Disabled("Validation of mail address not working")
    @DisplayName("Test setting reply address exception")
    @ParameterizedTest(name = "Test Reply Address \"{0}\".")
    @ValueSource(strings = { "", "abc.def", "abc-@mail.com", "abc..def@mail.com", ".abc@mail.com", "abc#def@mail.com", "abc.def@mail.c", "abc.def@mail#archive.com", "abc.def@mail", "abc.def@mail..com" })
    void shouldThrowCorrectExceptionForSettingReply(String address) {
        try {
            mail_.addReplyTo(address);
            fail("Expected addReplyTo(address) to throw, but it didn't");
        } catch (Exception e) {
            assertEquals(e.getClass(), IllegalArgumentException.class);
            assertEquals("Illegal address", e.getMessage());
        }
    }

    @DisplayName("Test setting reply address List function")
    @ParameterizedTest(name = "Test Reply Address \"{0}\".")
    @ValueSource(strings = { "", "abc.def", "abc-@mail.com", "abc..def@mail.com", ".abc@mail.com", "abc#def@mail.com", "abc.def@mail.c", "abc.def@mail#archive.com", "abc.def@mail", "abc.def@mail..com" })
    void testSettingReplyList(String address) {

    }

    @Test
    void addReplyTo() {
    }

    @Test
    void getReplyTo() {
    }

    @Test
    void getReplyToArray() {
    }

    @Test
    void addCcRecipient() {
    }

    @Test
    void getCcRecipient() {
    }

    @Test
    void getText() {
    }

    @Test
    void setBuilder() {
    }

    @Test
    void getBuilder() {
    }

    @Test
    void addAddressee() {
    }

    @Test
    void getAddressees() {
    }

    @Test
    void putParameter() {
    }
}