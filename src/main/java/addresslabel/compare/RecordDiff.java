package addresslabel.compare;

import addresslabel.Record;

import java.util.HashMap;
import java.util.Map;

public class RecordDiff {
    // Record 1 is the currently loaded Record (the one to be updated)
    private Record _record1;

    // Record 2 is the newer record that may contain updates for Record 1
    private Record _record2;

    // How closely do the records match?
    private double _comparisonScore;

    // Determines how record1 compares to record2
    private DiffType _diffType;

    // List of fields that are different between record 1 and 2.  Only valid if _diffType is Updated.
    private Map<String, DiffType> _differentFields;

    public RecordDiff( Record r1, Record r2, DiffType type, double score ) {
        _record1 = r1;
        _record2 = r2;
        _comparisonScore = 0.0;
        _diffType = type;
        _differentFields = new HashMap<>();
        if( _record1 != null && _record2 != null )
            _populateDifferentFields();
    }

    private void _populateDifferentFields() {
        for( String key: _record1.getData().keySet() ) {
            String value1 = _record1.getData().get( key );
            String value2 = null;
            if( _record2.getData().containsKey( key ) ) {
                value2 = _record2.getData().get( key );
            }

            if( value2 == null ) {
                _differentFields.put( key, DiffType.Removed );
            }
            else {
                _differentFields.put( key, DiffType.Updated );
            }
        }

        // Check for new fields (exist in record2, but not in record1)
        for( String key: _record2.getData().keySet() ) {
            String value2 = _record2.getData().get( key );
            if( !_record1.getData().containsKey( key ) ) {
                _differentFields.put( key, DiffType.Added );
            }
        }
    }

    public Record getRecord1(){ return _record1; }
    public Record getRecord2(){ return _record2; }
    public double getComparisonScore(){ return _comparisonScore; }
    public DiffType getDiffType(){ return _diffType; }
    public Map<String, DiffType> getDifferentFields(){ return _differentFields; }
}
