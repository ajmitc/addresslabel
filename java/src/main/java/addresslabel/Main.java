package addresslabel;

import javax.swing.JFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import addresslabel.view.View;
import addresslabel.util.Util;

public class Main
{
    public static void main( String ... args )
    {
        JFrame frame = new JFrame();
        frame.setTitle( "Address Label Easy Button" );
        frame.setSize( 800, 1000 );
        Util.center( frame );

        frame.addWindowListener( new WindowAdapter(){
            public void windowClosing( WindowEvent e )
            {
                System.exit( 0 );
            }
        });

        Model model = new Model();
        View view   = new View( model, frame );
        new Controller( model, view );

        frame.setVisible( true );
    }
}

