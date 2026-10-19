package poe.part.pkg2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


 
public class Message {

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MESSAGE_ID_LENGTH = 10;
    private static final String JSON_FILE_NAME = "messages.json";

    // Messages that were sent while the program is running
    private final ArrayList<String> sentIds = new ArrayList<>();
    private final ArrayList<String> sentHashes = new ArrayList<>();
    private final ArrayList<String> sentRecipients = new ArrayList<>();
    private final ArrayList<String> sentTexts = new ArrayList<>();

    // Messages stored to send later (each entry is one JSON object)
    private final ArrayList<String> storedJsonObjects = new ArrayList<>();

    // Every ID created so far, so that IDs stay unique
    private final ArrayList<String> createdIds = new ArrayList<>();

    private final Random random = new Random();
    private final Scanner input = new Scanner(System.in);

    private int maxMessages = 0;       // how many messages the user wants to enter
    private int numMessageSent = 0;    // how many messages were actually sent
    private boolean allMessagesEntered = false;

    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

   
    /* The message ID must not be more than ten characters. */
    public boolean checkMessageID(String messageID) {
        return messageID != null
                && !messageID.isEmpty()
                && messageID.length() <= MESSAGE_ID_LENGTH;
    }

    /* The recipient must start with an international code (+) and have no more than ten digits after the code. */
    public String checkRecipientCell(String cellphone) {
        if (cellphone != null && cellphone.matches("^\\+[0-9]{1,3}[0-9]{9,10}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    /* The message must not be more than 250 characters. */
    public String checkMessageLength(String messageText) {
        int length = (messageText == null) ? 0 : messageText.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_MESSAGE_LENGTH)
                + "; please reduce the size.";
    }

    
    /* Builds a unique ten-digit ID one random digit at a time (loop counter = digit position). */
    public String generateMessageID() {
        String messageID;
        do {
            StringBuilder digits = new StringBuilder();
            for (int position = 0; position < MESSAGE_ID_LENGTH; position++) {
                digits.append(random.nextInt(10));
            }
            messageID = digits.toString();
        } while (createdIds.contains(messageID));

        createdIds.add(messageID);
        return messageID;
    }

    public String messageIdGeneratedText(String messageID) {
        return "Message ID generated: " + messageID;
    }

    
    public String createMessageHash(String messageID, int messageNumber, String messageText) {
        String firstTwoDigits = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");

        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

        return (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

  
    /* Asks the user whether to send, disregard or store the message. */
    public String SentMessage() {
        System.out.println("\nChoose what you want to do with this message:");
        System.out.println("1. Send Message");
        System.out.println("2. Disregard Message");
        System.out.println("3. Store Message to send later");
        return SentMessage(readInt("Enter your choice: "));
    }

    /* Returns the text for the chosen option (separate from the keyboard so it can be unit tested). */
    public String SentMessage(int choice) {
        return switch (choice) {
            case 1 -> "Message successfully sent.";
            case 2 -> "Press 0 to delete the message.";
            case 3 -> "Message successfully stored.";
            default -> "Invalid option.";
        };
    }

    /* Carries out the user's choice for one message and returns the outcome text. */
    public String processMessage(String id, String hash, String recipient, String text, int choice) {
        if (choice == 1) {
            sentIds.add(id);
            sentHashes.add(hash);
            sentRecipients.add(recipient);
            sentTexts.add(text);
            numMessageSent++;
        } else if (choice == 3) {
            storeMessage(id, hash, recipient, text);
        }
        return SentMessage(choice);
    }

    /* Returns every message sent while the program is running (ID, Hash, Recipient, Message). */
    public String printMessages() {
        StringBuilder allMessages = new StringBuilder();
        for (int i = 0; i < sentIds.size(); i++) {
            allMessages.append("Message ID: ").append(sentIds.get(i)).append("\n")
                    .append("Message Hash: ").append(sentHashes.get(i)).append("\n")
                    .append("Recipient: ").append(sentRecipients.get(i)).append("\n")
                    .append("Message: ").append(sentTexts.get(i)).append("\n")
                    .append("----------------------------\n");
        }
        return allMessages.toString();
    }

    /* Returns the total number of messages sent. */
    public int returnTotalMessages() {
        return numMessageSent;
    }

    /*
     * Stores a message in messages.json.
     * Attribution: the JSON layout follows the format described at https://www.json.org/
     * and the file is written with java.io.FileWriter, Oracle Java documentation.
     * (https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/FileWriter.html).
     */
    public void storeMessage(String id, String hash, String recipient, String text) {
        storedJsonObjects.add(" {\n"
                + "    \"messageID\": \"" + escapeJson(id) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(hash) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(text) + "\"\n"
                + "  }");

        try (FileWriter writer = new FileWriter(JSON_FILE_NAME)) {
            writer.write("[\n" + String.join(",\n", storedJsonObjects) + "\n]\n");
        } catch (IOException e) {
            System.out.println("Could not write the JSON file: " + e.getMessage());
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

   
    public void sendingMessages() {
        if (allMessagesEntered) {
            System.out.println("You have already entered all " + maxMessages + " messages.");
            return;
        }

        // The loop counter is the message number (0, 1, 2 ...), used in the hash
        for (int messageNumber = 0; messageNumber < maxMessages; messageNumber++) {
            System.out.println("\nMESSAGE " + (messageNumber + 1) + " of " + maxMessages);

            String messageID = generateMessageID();
            System.out.println(messageIdGeneratedText(messageID));

            String recipient = askForRecipient();
            String messageText = askForMessageText();
            String messageHash = createMessageHash(messageID, messageNumber, messageText);

            System.out.println("Message Hash: " + messageHash);

            int choice = askForChoice();
            System.out.println(processMessage(messageID, messageHash, recipient, messageText, choice));

            if (choice == 1) {
                showMessageDetails(messageID, messageHash, recipient, messageText);
            } else if (choice == 2) {
                confirmDelete();
            }
        }

        allMessagesEntered = true;
        System.out.println("\n===== All sent messages =====");
        System.out.print(printMessages());
        System.out.println("Total messages sent: " + returnTotalMessages());
    }

    private String askForRecipient() {
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String recipient = input.nextLine().trim();
            String result = checkRecipientCell(recipient);
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) {
                return recipient;
            }
        }
    }

    private String askForMessageText() {
        while (true) {
            System.out.print("Enter your message: ");
            String messageText = input.nextLine();
            if (messageText.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }
            String result = checkMessageLength(messageText);
            System.out.println(result);
            if (result.equals("Message ready to send.")) {
                return messageText;
            }
        }
    }

    private int askForChoice() {
        System.out.println("\nChoose what you want to do with this message:");
        System.out.println("1. Send Message");
        System.out.println("2. Disregard Message");
        System.out.println("3. Store Message to send later");
        return readInt("Enter your choice: ");
    }

    private void confirmDelete() {
        if (readInt("") == 0) {
            System.out.println("Message deleted.");
        } else {
            System.out.println("Invalid option. The message was disregarded.");
        }
    }

    /** Full details in the required order: Message ID, Message Hash, Recipient, Message. */
    private void showMessageDetails(String id, String hash, String recipient, String text) {
        System.out.println("\n===== Message details =====");
        System.out.println("Message ID: " + id);
        System.out.println("Message Hash: " + hash);
        System.out.println("Recipient: " + recipient);
        System.out.println("Message: " + text);
    }

    /** Reads a whole number, asking again until the user types one. */
    private int readInt(String prompt) {
        String currentPrompt = prompt;
        while (true) {
            System.out.print(currentPrompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                currentPrompt = "Try again: ";
            }
        }
    }
}
        
    

