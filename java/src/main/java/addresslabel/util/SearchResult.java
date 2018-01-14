package addresslabel.util;

import addresslabel.Record;

public class SearchResult
{
    public int index;
    public Record record;

    public SearchResult( int index, Record record )
    {
        this.index  = index;
        this.record = record;
    }
}

