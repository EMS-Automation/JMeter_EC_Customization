package org.apache.jmeter.assertions.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.jmeter.assertions.RuleAssertion;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.testelement.TestElement;

public class RuleAssertionGui extends AbstractAssertionGui {
    private JTextArea expectedJson;
    private JTextField ignoreNodesField;
    private JTextField regexReplacementsField;
    private JTextField ignoreArrayOrderField;
    private JTextField ignoreArrayLengthField;

    public RuleAssertionGui() {
        this.init();
    }

    public String getLabelResource() {
        return "rule_assertion";
    }

    public String getStaticLabel() {
        return "Rule Assertion";
    }

    public TestElement createTestElement() {
        RuleAssertion assertion = new RuleAssertion();
        this.modifyTestElement(assertion);
        return assertion;
    }

    public void modifyTestElement(TestElement te) {
        super.configureTestElement(te);
        if (te instanceof RuleAssertion) {
            RuleAssertion assertion = (RuleAssertion)te;
            assertion.setJsonExpected(this.expectedJson.getText());
            assertion.setIgnoreNodes(this.ignoreNodesField.getText());
            assertion.setRegexReplacements(this.regexReplacementsField.getText());
            assertion.setIgnoreArrayOrder(this.ignoreArrayOrderField.getText());
            assertion.setIgnoreArrayLength(this.ignoreArrayLengthField.getText());
        }

    }

    public void configure(TestElement element) {
        super.configure(element);
        RuleAssertion assertion = (RuleAssertion)element;
        this.expectedJson.setText(assertion.getJsonExpected());
        this.ignoreNodesField.setText(assertion.getIgnoreNodes());
        this.regexReplacementsField.setText(assertion.getRegexReplacements());
        this.ignoreArrayOrderField.setText(assertion.getIgnoreArrayOrder());
        this.ignoreArrayLengthField.setText(assertion.getIgnoreArrayLength());
    }

    public void clearGui() {
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.setBorder(this.makeBorder());
        Box mainPanel = Box.createVerticalBox();
        mainPanel.add(this.makeTitlePanel());
        JPanel jsonPanel = new JPanel(new BorderLayout());
        jsonPanel.setBorder(BorderFactory.createTitledBorder("Expected JSON"));
        this.expectedJson = new JTextArea(10, 50);
        jsonPanel.add(new JScrollPane(this.expectedJson), "Center");
        mainPanel.add(jsonPanel);
        JPanel settingsPanel = new JPanel(new GridLayout(4, 2));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Comparison Settings"));
        settingsPanel.add(new JLabel("Ignore Nodes (comma-separated):"));
        this.ignoreNodesField = new JTextField();
        settingsPanel.add(this.ignoreNodesField);
        settingsPanel.add(new JLabel("Regex Replacements (regex:replacement):"));
        this.regexReplacementsField = new JTextField();
        settingsPanel.add(this.regexReplacementsField);
        settingsPanel.add(new JLabel("Ignore Array Order (comma-separated):"));
        this.ignoreArrayOrderField = new JTextField();
        settingsPanel.add(this.ignoreArrayOrderField);
        settingsPanel.add(new JLabel("Ignore Array Length (comma-separated):"));
        this.ignoreArrayLengthField = new JTextField();
        settingsPanel.add(this.ignoreArrayLengthField);
        mainPanel.add(settingsPanel);
        this.add(mainPanel, "Center");
    }
}

