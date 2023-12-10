package addresslabel.action;

import javax.swing.*;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import addresslabel.Model;
import addresslabel.view.EditTemplateFormatDialog;
import addresslabel.view.View;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * This action kicks off the PDF generation.  It should only be called by the OpenExportToPDFDialogAction.
 */
public class ExportPDFAction extends AbstractAction {
    private Model model;
    private View view;

    public ExportPDFAction(Model model, View view) {
        super("Export PDF");
        this.model = model;
        this.view = view;
        putValue(Action.SHORT_DESCRIPTION, "Export to PDF File");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    }


    public void actionPerformed(ActionEvent e) {
        if (model.getUsedRecords().size() == 0) {
            System.out.println("No records found, nothing to export!");
            return;
        }

        EditTemplateFormatDialog dialog =
                new EditTemplateFormatDialog(model, view, EditTemplateFormatDialog.EditTemplateSettingsDialogType.EXPORT);
        dialog.setVisible(true);

        if (dialog.isCanceled())
            return;

        model.getTemplate().setFontName(dialog.getFontName());
        model.getTemplate().setFontSize(dialog.getFontSize());
        model.getTemplate().setDrawLabelBorder(dialog.shouldDrawLabelBorder());
        model.getTemplate().setDrawMargins(dialog.shouldDrawMargins());

        view.getSheetPanel().saveLabels();
        PDDocument doc = model.getTemplate().toPDF(model.getUsedRecords());
        String fn = __getTempFilename();
        try {
            doc.save(fn);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        // Open warning box to print correctly
        JOptionPane.showMessageDialog(view.getFrame(), "Be sure to disable Page Scaling when printing PDF", "Print Information", JOptionPane.INFORMATION_MESSAGE);

        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(fn);
                Desktop.getDesktop().open(myFile);
            } catch (IllegalArgumentException iae) // if the specified file doesn't exist
            {
                iae.printStackTrace();
            } catch (UnsupportedOperationException uoe) // if the current platform does not support the Desktop.Action.OPEN action
            {
                uoe.printStackTrace();
            } catch (IOException ex) {
                // no application registered for PDFs
                System.err.println("No application registered to open PDFs, file save at " + fn);
            } catch (SecurityException se) // insufficient permissions
            {
                se.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Runtime.getRuntime().exec("cmd " + fn);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    private String __getTempFilename() {
        //if( sys.platform == "windows":
        //return "C:\\temp\\%s.pdf" % uuid.uuid4()
        return String.format("/tmp/contact-labels-%s.pdf", UUID.randomUUID().toString());
    }
}

