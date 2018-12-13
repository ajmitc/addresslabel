package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Font;

public class ManualDialog extends BaseDialog
{
    public ManualDialog( JFrame parent )
    {
        super( parent, "Address Label Easy Button Help", false, 600, 800 );

        JLabel title = new JLabel( "Address Label Easy Button" );
        title.setFont( new Font( "Arial", 16, Font.BOLD ) );

        JTextArea txtContent = new JTextArea();
        txtContent.setWrapStyleWord( true );
        txtContent.setFont( new Font( "Arial", 10, Font.PLAIN ) );
        String c = "<html>1) Open a Contact List CSV File (ie. exported from Google Contacts using Outlook or other format)<br/>";
        c += "2) Update any records by right-clicking on the record and selecting 'Edit Record'<br/>";
        c += "3) Update any label templates by right-clicking on the record and selecting 'Edit Template'<br/>";
        c += "4) Manually edit label display by directly changing text in Record box (this will not change the underlying CSV record)<br/>";
        c += "5) Click 'Print' to send the document to the default printer.  If you need to select a different printer or printer options, export to PDF and print from the default PDF viewer application.<br/>";
        txtContent.setText( c );

        JPanel content = new JPanel( new BorderLayout() );
        content.add( title, BorderLayout.NORTH );
        content.add( new JScrollPane( txtContent ), BorderLayout.CENTER );
        setContent( content );
    }
}

