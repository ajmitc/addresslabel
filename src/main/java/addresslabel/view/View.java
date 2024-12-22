package addresslabel.view;

import javax.swing.*;

import java.awt.FileDialog;
import java.awt.BorderLayout;

import java.io.File;
import java.io.FilenameFilter;

import addresslabel.Model;
import addresslabel.action.*;
import addresslabel.template.LabelSheetTemplate;
import addresslabel.util.Logger;

public class View {
    private static final Logger _logger = Logger.getLogger(View.class);
    private Model model;
    private JFrame frame;

    private JTextField tfSearch;
    private JButton btnSearchNext;
    private JButton btnNextPage;
    private JButton btnPrevPage;
    private JLabel lblPage;

    private JLabel lblStatus;
    private JLabel lblGoogleUser;

    private SheetPanel sheetpanel;

    private FileDialog fileDialog;
    private FileDialog projectFileDialog;

    public View(Model model, JFrame frame) {
        this.model = model;
        this.frame = frame;

        fileDialog = new FileDialog(this.frame, "Choose a file", FileDialog.LOAD);
        fileDialog.setDirectory(System.getProperty("os.name").toLowerCase().startsWith("win") ? "C:\\" : "~");
        fileDialog.setFile("*.csv");

        projectFileDialog = new FileDialog(this.frame, "Choose a file", FileDialog.LOAD);
        projectFileDialog.setDirectory(System.getProperty("os.name").toLowerCase().startsWith("win") ? "C:\\" : "~");
        projectFileDialog.setFile("*.sav");

        JMenu filemenu = new JMenu("File");
        filemenu.add(new NewAction(this.model, this));
        filemenu.addSeparator();
        filemenu.add(new OpenProjectAction(this.model, this));
        filemenu.add(new SaveProjectAction(this.model, this, false));
        filemenu.add(new SaveProjectAction(this.model, this, true));
        filemenu.addSeparator();
        filemenu.add(new ExportPDFAction(this.model, this));
        filemenu.add(new PrintLabelsAction(this.model, this));
        filemenu.addSeparator();
        filemenu.add(new ExitAction(this.model, this));

        JMenu projectmenu = new JMenu("Project");
        projectmenu.add(new AddPageAction(this.model, this));
        projectmenu.addSeparator();
        projectmenu.add(new SortByLastNameAction(this.model, this));
        projectmenu.add(new SortByCountryAction(this.model, this));

        JMenu csvmenu = new JMenu("CSV");
        csvmenu.add(new OpenCsvAction(this.model, this));
        csvmenu.addSeparator();
        csvmenu.add(new SaveCsvAction(this.model, this, false));
        csvmenu.add(new SaveCsvAction(this.model, this, true));
        csvmenu.addSeparator();
        csvmenu.add(new UpdateRecordsAction(this.model, this));

        JMenu templmenu = new JMenu("Template");
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < Model.LABEL_SHEET_TEMPLATES.length; ++i) {
            LabelSheetTemplate templ = Model.LABEL_SHEET_TEMPLATES[i];
            JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem(new SelectTemplateAction(this.model, this, templ));
            templmenu.add(rbmi);
            group.add(rbmi);
            if (i == 0)
                rbmi.setSelected(true);
        }

        JMenu googlemenu = new JMenu("Google");
        googlemenu.add(new ImportGoogleContactsAction(this.model, this));
        googlemenu.add(new LogoutGoogleContactsAction(this.model, this));

        JMenu helpmenu = new JMenu("Help");
        helpmenu.add(new DisplayManualAction(this.model, this));
        helpmenu.add(new DisplayAboutAction(this.model, this));

        JMenuBar menubar = new JMenuBar();
        menubar.add(filemenu);
        menubar.add(projectmenu);
        menubar.add(csvmenu);
        menubar.add(templmenu);
        menubar.add(googlemenu);
        menubar.add(helpmenu);

        this.frame.setJMenuBar(menubar);


        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.addSeparator();
        toolbar.add(new NewAction(this.model, this));
        toolbar.add(new OpenCsvAction(this.model, this));
        toolbar.add(new SaveCsvAction(this.model, this, false));
        toolbar.addSeparator();
        toolbar.add(new ExportPDFAction(this.model, this));
        toolbar.add(new PrintLabelsAction(this.model, this));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Search: "));

        tfSearch = new JTextField(10);
        btnSearchNext = new JButton("Next");
        toolbar.add(tfSearch);
        toolbar.add(btnSearchNext);
        toolbar.addSeparator();

        btnNextPage = new JButton(">");
        btnPrevPage = new JButton("<");
        lblPage = new JLabel("Page 0 of 0");
        toolbar.add(btnPrevPage);
        toolbar.add(btnNextPage);
        toolbar.add(lblPage);
        toolbar.addSeparator();

        sheetpanel = new SheetPanel(this.model, this);

        // Status Panel
        JPanel pnlStatus = new JPanel(new BorderLayout());
        //pnlStatus.setBorder(BorderFactory.createBevelBorder(1));
        lblStatus = new JLabel();
        lblStatus.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(1),
                        BorderFactory.createEmptyBorder(2, 5, 2, 1)));
        lblStatus.setText("Load a CSV File or Import your Google Contacts");
        lblGoogleUser = new JLabel();
        lblGoogleUser.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(1),
                        BorderFactory.createEmptyBorder(2, 5, 2, 1)));
        lblGoogleUser.setText("Not logged in");
        pnlStatus.add(lblStatus, BorderLayout.CENTER);
        pnlStatus.add(lblGoogleUser, BorderLayout.EAST);

        this.frame.getContentPane().setLayout(new BorderLayout());
        this.frame.getContentPane().add(toolbar, BorderLayout.PAGE_START);
        this.frame.getContentPane().add(sheetpanel, BorderLayout.CENTER);
        this.frame.getContentPane().add(pnlStatus, BorderLayout.PAGE_END);
    }

    public void refresh() {
        displayPage();
    }

    public void displayPage() {
        int recordsPerPage = model.getRecordsPerPage();
        sheetpanel.display(model.getRecords().subList(model.getPage() * recordsPerPage, model.getRecords().size()));
        lblPage.setText("Page " + (model.getPage() + 1) + " of " + model.getNumPagesToFitRecords());
    }

    public JFrame getFrame() {
        return frame;
    }

    public SheetPanel getSheetPanel() {
        return sheetpanel;
    }

    public JTextField getTfSearch() {
        return tfSearch;
    }

    public JButton getBtnSearchNext() {
        return btnSearchNext;
    }

    public JButton getBtnNextPage() {
        return btnNextPage;
    }

    public JButton getBtnPrevPage() {
        return btnPrevPage;
    }

    public FileDialog getFileDialog() {
        return fileDialog;
    }

    public FileDialog getLoadCsvFileDialog() {
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setTitle("Load Contact List");
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        });
        return fileDialog;
    }

    public FileDialog getSaveCsvFileDialog() {
        fileDialog.setMode(FileDialog.SAVE);
        fileDialog.setTitle("Save Contact List");
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        });
        return fileDialog;
    }

    public FileDialog getLoadProjectFileDialog() {
        projectFileDialog.setMode(FileDialog.LOAD);
        fileDialog.setTitle("Load Project");
        projectFileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sav");
            }
        });
        return projectFileDialog;
    }

    public FileDialog getSaveProjectFileDialog() {
        projectFileDialog.setMode(FileDialog.SAVE);
        projectFileDialog.setTitle("Save Project");
        projectFileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sav");
            }
        });
        return projectFileDialog;
    }

    public void setStatus(String status) {
        lblStatus.setText(status);
    }

    public void setGoogleUser(String username) {
        lblGoogleUser.setText(username);
    }
}

