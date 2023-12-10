package addresslabel.view;

import addresslabel.Model;

import javax.swing.*;
import java.awt.*;

public class EditTemplateFormatDialog extends BaseDialog {
    private Model model;
    private View view;

    private JTextField fontName;
    private JSpinner fontSize;
    private JCheckBox drawLabelBorder;
    private JCheckBox drawMargins;

    public static enum EditTemplateSettingsDialogType {
        EXPORT,
        PRINT
    }

    public EditTemplateFormatDialog(Model model, View view, EditTemplateSettingsDialogType type) {
        super(view.getFrame(), type == EditTemplateSettingsDialogType.EXPORT ? "Export to PDF" : "Template Format", true, 400, 200);
        this.model = model;
        this.view = view;

        fontName = new JTextField(model.getTemplate().getFontName(), 20);
        fontSize = new JSpinner();
        ((SpinnerNumberModel) fontSize.getModel()).setMinimum(1);
        ((SpinnerNumberModel) fontSize.getModel()).setMaximum(100);
        fontSize.setValue(model.getTemplate().getFontSize());

        drawLabelBorder = new JCheckBox("Draw Label Border");
        drawLabelBorder.setSelected(model.getTemplate().shouldDrawLabelBorder());

        drawMargins = new JCheckBox("Draw Page Margins");
        drawMargins.setSelected(model.getTemplate().shouldDrawMargins());

        JPanel fontNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontNamePanel.add(new JLabel("Font Name"));
        fontNamePanel.add(fontName);

        JPanel fontSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontSizePanel.add(new JLabel("Font Size"));
        fontSizePanel.add(fontSize);

        JPanel contentPanel = new JPanel(new GridLayout(4, 1));
        contentPanel.add(fontNamePanel);
        contentPanel.add(fontSizePanel);
        contentPanel.add(drawLabelBorder);
        contentPanel.add(drawMargins);

        setContent(contentPanel);
        setCloseText(type == EditTemplateSettingsDialogType.EXPORT ? "Export" : "Print");
    }

    public String getFontName() {
        return fontName.getText();
    }

    public int getFontSize() {
        return (Integer) fontSize.getValue();
    }

    public boolean shouldDrawLabelBorder() {
        return drawLabelBorder.isSelected();
    }

    public boolean shouldDrawMargins() {
        return drawMargins.isSelected();
    }
}
