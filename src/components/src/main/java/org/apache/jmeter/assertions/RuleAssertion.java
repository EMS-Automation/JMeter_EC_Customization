package org.apache.jmeter.assertions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;

public class RuleAssertion extends AbstractTestElement implements Assertion  {
    public RuleAssertion() {
    }

    public AssertionResult getResult(SampleResult response) {
        AssertionResult result = new AssertionResult(this.getName());
        StringBuilder failureMessage = new StringBuilder("JSON Comparison Failed due to:\n");

        try {
            String jsonResponse = response.getResponseDataAsString();
            String jsonExpected = this.getJsonExpected();
            Set<String> ignoreNodes = new HashSet(Arrays.asList(this.getIgnoreNodes().split(",")));
            Map<String, String> regexReplacements = this.parseKeyValuePairs(this.getRegexReplacements());
            Map<String, Boolean> ignoreArrayOrder = this.parseBooleanMap(this.getIgnoreArrayOrder());
            Map<String, Boolean> ignoreArrayLength = this.parseBooleanMap(this.getIgnoreArrayLength());
            JsonComparator comparator = new JsonComparator();
            boolean areEqual = comparator.compareJson(jsonExpected, jsonResponse, ignoreNodes, regexReplacements, ignoreArrayOrder, ignoreArrayLength, failureMessage);
            if (!areEqual) {
                result.setFailure(true);
                result.setFailureMessage(failureMessage.toString());
            }
        } catch (Exception var12) {
            result.setError(true);
            result.setFailureMessage("Error occurred during JSON comparison: " + var12.getMessage());
        }

        return result;
    }

    private Map<String, String> parseKeyValuePairs(String input) {
        Map<String, String> map = new HashMap();
        if (input != null && !input.trim().isEmpty()) {
            String[] pairs = input.split(",");
            String[] var4 = pairs;
            int var5 = pairs.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String pair = var4[var6];
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
        }

        return map;
    }

    private Map<String, Boolean> parseBooleanMap(String input) {
        Map<String, Boolean> map = new HashMap();
        if (input != null && !input.trim().isEmpty()) {
            String[] items = input.split(",");
            String[] var4 = items;
            int var5 = items.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String item = var4[var6];
                map.put(item, true);
            }
        }

        return map;
    }

    public String getJsonExpected() {
        return this.getPropertyAsString("jsonExpected");
    }

    public void setJsonExpected(String jsonExpected) {
        this.setProperty("jsonExpected", jsonExpected);
    }

    public String getIgnoreNodes() {
        return this.getPropertyAsString("ignoreNodes");
    }

    public void setIgnoreNodes(String ignoreNodes) {
        this.setProperty("ignoreNodes", ignoreNodes);
    }

    public String getRegexReplacements() {
        return this.getPropertyAsString("regexReplacements");
    }

    public void setRegexReplacements(String regexReplacements) {
        this.setProperty("regexReplacements", regexReplacements);
    }

    public String getIgnoreArrayOrder() {
        return this.getPropertyAsString("ignoreArrayOrder");
    }

    public void setIgnoreArrayOrder(String ignoreArrayOrder) {
        this.setProperty("ignoreArrayOrder", ignoreArrayOrder);
    }

    public String getIgnoreArrayLength() {
        return this.getPropertyAsString("ignoreArrayLength");
    }

    public void setIgnoreArrayLength(String ignoreArrayLength) {
        this.setProperty("ignoreArrayLength", ignoreArrayLength);
    }

}
