package addresslabel.util;

import addresslabel.Record;

public class SearchResult
{
    private int _index;
    private Record _record;

    public SearchResult( int index, Record record )
    {
        _index = index;
        _record = record;
    }

    public int getIndex(){ return _index; }
    public Record getRecord(){ return _record; }
}

