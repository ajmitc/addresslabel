package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Font;

import addresslabel.Model;

public class AboutDialog extends BaseDialog
{
    public static final String COPYRIGHT_SYMBOL = "\u00A9";

    public AboutDialog( JFrame frame )
    {
        super( frame, "About", true, 300, 300 );

        JPanel content = new JPanel( new BorderLayout() );

        //Font font = new Font( "Arial", 16, Font.BOLD );
        JLabel title = new JLabel( "Address Label Easy Button" );
        //title.setFont( title );
        content.add( title, BorderLayout.NORTH );

        //font = new Font( "Arial", 10, Font.PLAIN );
        JLabel lbl = new JLabel(
            "<html>Version " + Model.VERSION + "<br/>" +
            "Developed by Aaron Mitchell<br/>" + 
            "Copyright \u00A9 2017 Aaron Mitchell</html>" );
        content.add( lbl, BorderLayout.CENTER );

        setContent( content );
        hideCancel();
    }

}

