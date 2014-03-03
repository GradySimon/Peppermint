package com.gradysimon.peppermint;

import com.gradysimon.peppermint.datatype.Conversation;
import com.gradysimon.peppermint.datatype.Message;
import com.gradysimon.peppermint.datatype.Topic;
import com.gradysimon.peppermint.datatype.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by grady on 2/22/14.
 * <p/>
 * For testing purposes. Generates fake data to be used during development for testing stuff.
 */
public class DataFaker {
    private static Random random = new Random(1); // Get predictable results

    private static char getRandomCaptialLetter() {
        return (char) (random.nextInt(26) + 65);
    }

    private static String getNRandomLowerLetters(int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append(getRandomLowerLetter());
        }
        return builder.toString();
    }

    private static char getRandomLowerLetter() {
        return (char) (random.nextInt(26) + 97);
    }

    private static String getRandomName() {
        return getRandomCapitalWord();
    }

    private static String getRandomCapitalWord() {
        StringBuilder builder = new StringBuilder();
        builder.append(getRandomCaptialLetter());
        int lengthAfterFirst = random.nextInt(8) + 2;
        builder.append(getNRandomLowerLetters(lengthAfterFirst));
        return builder.toString();
    }

    private static String getRandomLowerWord() {
        StringBuilder builder = new StringBuilder();
        int length = random.nextInt(9) + 1;
        builder.append(getNRandomLowerLetters(length));
        return builder.toString();
    }

    private static String getRandomSentence() {
        StringBuilder builder = new StringBuilder();
        int length = random.nextInt(8) + 3;
        builder.append(getRandomCapitalWord());
        for (int i = 0; i < length - 1; i++) {
            builder.append(getRandomLowerWord());
            if (i != length - 2) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    public static UserProfile getFakeUserProfile() {
        int id = random.nextInt();
        StringBuilder builder = new StringBuilder();
        String firstName = getRandomName();
        String lastName = getRandomName();
        return new UserProfile(id, firstName, lastName);
    }

    public static Topic getFakeTopic() {
        int id = random.nextInt();
        UserProfile author = getFakeUserProfile();
        String text = getRandomSentence();
        return null;
        // return new Topic(id, author, text);
    }

    public static Message getFakeMessage(Conversation conversation) {
        if (random.nextBoolean()) {
            return new OutboundMessage(conversation, getRandomSentence());
        } else {
            return new InboundMessage(conversation, getRandomSentence());
        }
    }

    public static List<Message> getFakeMessageList(Conversation conversation) {
        List<Message> messageList = new ArrayList<>();
        int length = random.nextInt(20) + 2;
        for (int i = 0; i < length; i++) {
            messageList.add(getFakeMessage(conversation));
        }
        return messageList;
    }

    public static Conversation getFakeConversation() {
        UserProfile counterParty = getFakeUserProfile();
        Topic topic = getFakeTopic();
        Conversation conversation = new Conversation(topic, counterParty);
        conversation.setMessages(getFakeMessageList(conversation));
        return conversation;
    }

    public static List<Conversation> getFakeConversationList() {
        List<Conversation> conversationList = new ArrayList<>();
        int length = random.nextInt(10) + 2;
        for (int i = 0; i < length; i++) {
            conversationList.add(getFakeConversation());
        }
        return conversationList;
    }
}
