package addresslabel.action;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.*;

import addresslabel.Model;
import addresslabel.view.EditTemplateFormatDialog;
import addresslabel.view.View;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class PrintLabelsAction extends AbstractAction {
    private Model model;
    private View view;

    public PrintLabelsAction(Model model, View view) {
        super("Print Labels");
        this.model = model;
        this.view = view;
        putValue(Action.SHORT_DESCRIPTION, "Print Labels");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
    }


    public void actionPerformed(ActionEvent e) {
        EditTemplateFormatDialog dialog =
                new EditTemplateFormatDialog(model, view, EditTemplateFormatDialog.EditTemplateSettingsDialogType.EXPORT);
        dialog.setVisible(true);

        if (dialog.isCanceled())
            return;

        model.getTemplate().setFontName(dialog.getFontName());
        model.getTemplate().setFontSize(dialog.getFontSize());
        model.getTemplate().setDrawLabelBorder(dialog.shouldDrawLabelBorder());
        model.getTemplate().setDrawMargins(dialog.shouldDrawMargins());

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Address Labels");
        boolean ok = job.printDialog();
        if (ok) {
            doPrint(job);
        }
    }


    private void doPrint(PrinterJob job) {
        PDDocument document = model.getTemplate().toPDF(model.getUsedRecords());
        job.setPageable(new PDFPageable(document));

        try {
            job.print();
        } catch (PrinterException ex) {
            // The job did not successfully complete
            ex.printStackTrace();
        }
    }
}
