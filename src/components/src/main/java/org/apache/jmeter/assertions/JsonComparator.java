package org.apache.jmeter.assertions;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JsonComparator {

    public JsonComparator() {
    }

    public boolean compareJson(String expectedJson, String actualJson, Set<String> ignoreNodes,
                               Map<String, String> regexReplacements, Map<String, Boolean> ignoreArrayOrder,
                               Map<String, Boolean> ignoreArrayLength, StringBuilder failureMessage) {
        try {
            // Parse JSON strings into JSONObject instances using JSONParser.
            JSONParser parser = new JSONParser();
            JSONObject expected = (JSONObject) parser.parse(expectedJson);
            JSONObject actual = (JSONObject) parser.parse(actualJson);

            // Apply regex replacements to JSON objects directly.
            JSONObject expectedReplaced = applyRegexReplacements(expected, regexReplacements);
            JSONObject actualReplaced = applyRegexReplacements(actual, regexReplacements);

            // Compare the JSON objects.
            return compareJsonObjects(expectedReplaced, actualReplaced, ignoreNodes, ignoreArrayOrder,
                    ignoreArrayLength, failureMessage, "");
        } catch (ParseException e) {
            failureMessage.append("Failed to parse JSON: ").append(e.getMessage()).append("\n");
            return false;
        }
    }

    private JSONObject applyRegexReplacements(JSONObject jsonObject, Map<String, String> regexReplacements) {
        // Convert JSONObject to String, apply regex replacements, and parse back to JSONObject.
        String jsonString = jsonObject.toString();
        String replacedJsonString = applyRegexReplacements(jsonString, regexReplacements);
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(replacedJsonString);
        } catch (ParseException e) {
            // Handle parse exception.
            return null;
        }
    }

    private boolean compareJsonObjects(JSONObject expected, JSONObject actual, Set<String> ignoreNodes,
                                       Map<String, Boolean> ignoreArrayOrder, Map<String, Boolean> ignoreArrayLength,
                                       StringBuilder failureMessage, String path) {
        boolean isMatch = true;

        Iterator<String> keys = expected.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String currentPath = path.isEmpty() ? key : path + "." + key;

            if (ignoreNodes.contains(currentPath)) {
                continue;
            }

            if (!actual.containsKey(key)) {
                isMatch = false;
                failureMessage.append("Missing key in actual JSON: ").append(currentPath).append("\n");
            } else {
                Object expectedValue = expected.get(key);
                Object actualValue = actual.get(key);

                if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
                    isMatch &= compareJsonObjects((JSONObject) expectedValue, (JSONObject) actualValue,
                            ignoreNodes, ignoreArrayOrder, ignoreArrayLength,
                            failureMessage, currentPath);
                } else if (expectedValue instanceof JSONArray && actualValue instanceof JSONArray) {
                    isMatch &= compareJsonArrays((JSONArray) expectedValue, (JSONArray) actualValue,
                            ignoreNodes, ignoreArrayOrder, ignoreArrayLength,
                            failureMessage, currentPath);
                } else if (!expectedValue.equals(actualValue)) {
                    isMatch = false;
                    failureMessage.append("Value mismatch for key ").append(currentPath).append(": Expected ")
                            .append(expectedValue).append(", Found ").append(actualValue).append("\n");
                }
            }
        }

        // Check for extra keys in the actual JSON.
        for (String key : actual.keySet()) {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            if (!expected.containsKey(key)) {
                isMatch = false;
                failureMessage.append("Extra key in actual JSON: ").append(currentPath).append("\n");
            }
        }

        return isMatch;
    }

    private boolean compareJsonArrays(JSONArray expected, JSONArray actual, Set<String> ignoreNodes,
                                      Map<String, Boolean> ignoreArrayOrder, Map<String, Boolean> ignoreArrayLength,
                                      StringBuilder failureMessage, String path) {
        boolean isMatch = true;

        if (ignoreArrayOrder.getOrDefault(path, false)) {
            Set<Object> expectedSet = new HashSet<>(expected);
            Set<Object> actualSet = new HashSet<>(actual);

            if (!expectedSet.equals(actualSet)) {
                isMatch = false;
                failureMessage.append("Array mismatch for path ").append(path).append(": Expected ").append(expectedSet)
                        .append(", Found ").append(actualSet).append("\n");
            }
        } else {
            if (!ignoreArrayLength.getOrDefault(path, false) && expected.size() != actual.size()) {
                isMatch = false;
                failureMessage.append("Array length mismatch for path ").append(path).append(": Expected ")
                        .append(expected.size()).append(", Found ").append(actual.size()).append("\n");
            }

            for (int i = 0; i < Math.min(expected.size(), actual.size()); i++) {
                Object expectedItem = expected.get(i);
                Object actualItem = actual.get(i);
                String currentPath = path + "[" + i + "]";

                if (expectedItem instanceof JSONObject && actualItem instanceof JSONObject) {
                    isMatch &= compareJsonObjects((JSONObject) expectedItem, (JSONObject) actualItem,
                            ignoreNodes, ignoreArrayOrder, ignoreArrayLength,
                            failureMessage, currentPath);
                } else if (expectedItem instanceof JSONArray && actualItem instanceof JSONArray) {
                    isMatch &= compareJsonArrays((JSONArray) expectedItem, (JSONArray) actualItem,
                            ignoreNodes, ignoreArrayOrder, ignoreArrayLength,
                            failureMessage, currentPath);
                } else if (!expectedItem.equals(actualItem)) {
                    isMatch = false;
                    failureMessage.append("Value mismatch for array index ").append(i).append(" at path ")
                            .append(path).append(": Expected ").append(expectedItem).append(", Found ")
                            .append(actualItem).append("\n");
                }
            }
        }

        return isMatch;
    }


    private String applyRegexReplacements(String json, Map<String, String> regexReplacements) {
        String result = json;

        for (Map.Entry<String, String> entry : regexReplacements.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
