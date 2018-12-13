package addresslabel;

import addresslabel.view.View;
import addresslabel.util.Logger;
import addresslabel.util.SearchResult;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller
{
    private Logger _logger;
    private Model _model;
    private View _view;

    public Controller( Model model, View view )
    {
        _logger = Logger.getLogger( Controller.class );
        _model = model;
        _view = view;

        _view.getTfSearch().addKeyListener( new KeyAdapter(){
            public void keyReleased( KeyEvent e )
            {
                if( _model.getRecords().size() == 0 )
                    return;
                _model.getSearchResults().clear();
                _model.setSearchResultsIndex( 0 );
                String searchtext = _view.getTfSearch().getText();
                _logger.debug( "Searching for '" + searchtext + "'" );
                for( int idx = 0; idx < _model.getRecords().size(); ++idx )
                {
                    Record record = _model.getRecords().get( idx );
                    if( record.search( searchtext ) )
                    {
                        _model.getSearchResults().add( new SearchResult( idx, record ) );
                        _model.setSearchResultsIndex( -1 );
                    }
                }
                findSearchNext();
            }
        });

        _view.getBtnSearchNext().addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                findSearchNext();
            }
        });

        _view.getBtnNextPage().addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                _model.setPage( _model.getPage() + 1 );
                _view.displayPage();
            }
        });

        _view.getBtnPrevPage().addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                _model.setPage( _model.getPage() - 1 );
                _view.displayPage();
            }
        });

        _view.getSheetPanel().reset();
        _view.displayPage();
    }


    public void findSearchNext()
    {
        if( _model.getSearchResults().size() == 0 )
            return;
        _model.setSearchResultsIndex( (_model.getSearchResultsIndex() + 1) % _model.getSearchResults().size() );
        SearchResult sr = _model.getSearchResults().get( _model.getSearchResultsIndex() );
        // Get page
        int recordsPerPage = _model.getRecordsPerPage();
        int page = (int) (sr.getIndex() / recordsPerPage);
        _model.setPage( page );
        _view.displayPage();
        _view.getSheetPanel().highlightLabelWithRecord( sr.getRecord() );
    }
}

