package addresslabel;

import addresslabel.view.View;
import addresslabel.view.LabelPanel;

public class Controller
{
    private Model _model;
    private View _view;

    public Controller( Model model, View view )
    {
        _model = model;
        _view = view;

        for( LabelPanel lpanel: _view.getSheetPanel().getLabelPanels() )
        {
            
        }
    }
}

