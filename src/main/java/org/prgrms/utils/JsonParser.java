package org.prgrms.utils;

public class JsonParser {

    public static String extractValueFromJson(String json, String key) {
        String targetKey = "\"" + key + "\":";
        int startIndex = json.indexOf(targetKey);
        if (startIndex == -1) {
            return null; // key not found
        }
        startIndex += targetKey.length();

        // Finding the end index of the value
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) { // this means the target key-value is the last one in the JSON string
            endIndex = json.indexOf("}", startIndex);
        }

        // Extracting the value
        String value = json.substring(startIndex, endIndex).trim();
        if (value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }
}