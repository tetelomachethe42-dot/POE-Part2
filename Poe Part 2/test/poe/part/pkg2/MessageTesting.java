package poe.part.pkg2;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Message class, using the test data from the brief.
 */
public class MessageTesting {

    private final Message message = new Message();

    // Test data for message 1 (send)
    private static final String ID_1 = "0012345678";
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    // Test data for message 2 (discard)
    private static final String ID_2 = "0098765432";
    private static final String RECIPIENT_2 = "+27857597588";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    // ---------- Message length ----------

    @Test
    public void messageLengthSuccess() {
        assertEquals("Message ready to send.", message.checkMessageLength(TEXT_1));
    }

    @Test
    public void messageLengthFailure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength(tooLong));
    }

    // ---------- Recipient number ----------

    @Test
    public void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell(RECIPIENT_1));
    }

    @Test
    public void recipientWithoutInternationalCodeFails() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                message.checkRecipientCell("0718693002"));
    }

    // ---------- Message hash ----------

    @Test
    public void messageHashCorrectForTestCase1() {
        assertEquals("00:0:HITONIGHT", message.createMessageHash(ID_1, 0, TEXT_1));
    }

    @Test
    public void messageHashesCorrectInLoop() {
        String[] ids = {ID_1, ID_2};
        String[] texts = {TEXT_1, TEXT_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < ids.length; i++) {
            assertEquals(expectedHashes[i], message.createMessageHash(ids[i], i, texts[i]));
        }
    }

    // ---------- Message ID ----------

    @Test
    public void messageIdIsCreated() {
        String id = message.generateMessageID();
        assertEquals(10, id.length());
        assertTrue(message.checkMessageID(id));
        assertEquals("Message ID generated: " + id, message.messageIdGeneratedText(id));
    }

    @Test
    public void messageIdsAreUnique() {
        assertNotEquals(message.generateMessageID(), message.generateMessageID());
    }

    @Test
    public void messageIdLongerThanTenFails() {
        assertFalse(message.checkMessageID("12345678901"));
    }

    // ---------- Send / disregard / store ----------

    @Test
    public void sentMessageOptions() {
        assertEquals("Message successfully sent.", message.SentMessage(1));
        assertEquals("Press 0 to delete the message.", message.SentMessage(2));
        assertEquals("Message successfully stored.", message.SentMessage(3));
    }

    @Test
    public void totalSentCountsOnlySentMessages() {
        String hash1 = message.createMessageHash(ID_1, 0, TEXT_1);
        String hash2 = message.createMessageHash(ID_2, 1, TEXT_2);

        // Message 1: user selects Send. Message 2: user selects Discard.
        assertEquals("Message successfully sent.",
                message.processMessage(ID_1, hash1, RECIPIENT_1, TEXT_1, 1));
        assertEquals("Press 0 to delete the message.",
                message.processMessage(ID_2, hash2, RECIPIENT_2, TEXT_2, 2));

        assertEquals(1, message.returnTotalMessages());
    }

    @Test
    public void printMessagesShowsDetailsInCorrectOrder() {
        String hash1 = message.createMessageHash(ID_1, 0, TEXT_1);
        message.processMessage(ID_1, hash1, RECIPIENT_1, TEXT_1, 1);

        String output = message.printMessages();
        int idPosition = output.indexOf("Message ID: " + ID_1);
        int hashPosition = output.indexOf("Message Hash: " + hash1);
        int recipientPosition = output.indexOf("Recipient: " + RECIPIENT_1);
        int textPosition = output.indexOf("Message: " + TEXT_1);

        assertTrue(idPosition >= 0);
        assertTrue(idPosition < hashPosition);
        assertTrue(hashPosition < recipientPosition);
        assertTrue(recipientPosition < textPosition);
    }

    @Test
    public void storedMessageIsWrittenToJsonFile() {
        String hash1 = message.createMessageHash(ID_1, 0, TEXT_1);
        assertEquals("Message successfully stored.",
                message.processMessage(ID_1, hash1, RECIPIENT_1, TEXT_1, 3));

        File jsonFile = new File("messages.json");
        assertTrue(jsonFile.exists());
        jsonFile.delete(); // clean up after the test
    }
}

