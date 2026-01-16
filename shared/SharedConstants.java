package shared;

public class SharedConstants {
    public static final int SERVER_PORT = 12345;
    public static final String SERVER_HOST = "localhost";
    public static final String MSG_JOIN = "JOIN";
    public static final String MSG_LEAVE = "LEAVE";
    public static final String MSG_EDIT = "EDIT";
    public static final String MSG_DELETE = "DELETE";
    public static final String MSG_USER_LIST = "USER_LIST";
    public static final String MSG_SYNC = "SYNC";
    public static final String MSG_ERROR = "ERROR";
    public static final String PROTOCOL_DELIMITER = "|";
    public static final String USER_LIST_DELIMITER = ",";
    public static final String DEFAULT_USERNAME = "Anonymous";
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MAX_MESSAGE_LENGTH = 10000;

    private SharedConstants() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }

    public static String buildMessage(String type, String... data) {
        StringBuilder message = new StringBuilder(type);
        for (String part : data) {
            message.append(PROTOCOL_DELIMITER).append(escapeText(part));
        }
        String result = message.toString();
        System.out.println("[SharedConstants] Built message: " + result);
        return result;
    }

    public static String[] parseMessage(String message) {
        System.out.println("[SharedConstants] Parsing message: " + message);
        String[] parts = message.split("\\" + PROTOCOL_DELIMITER, -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = unescapeText(parts[i]);
        }
        System.out.println("[SharedConstants] Parsed into " + parts.length + " parts");
        return parts;
    }

    public static String escapeText(String text) {
        if (text == null) return "";
        String escaped = text
                .replace("\\", "\\\\")  
                .replace("\n", "\\n")   
                .replace("\r", "\\r")    
                .replace("\t", "\\t")   
                .replace("|", "\\p");  
        return escaped;
    }
    public static String unescapeText(String text) {
        if (text == null) return "";
        String unescaped = text
                .replace("\\p", "|")    
                .replace("\\t", "\t")    
                .replace("\\r", "\r")    
                .replace("\\n", "\n")   
                .replace("\\\\", "\\"); 
        return unescaped;
    }
}
