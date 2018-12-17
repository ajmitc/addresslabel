package addresslabel.compare;

import addresslabel.Record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.*;

public class RecordComparator {
    private static Logger logger = Logger.getLogger( RecordComparator.class.getName() );

    public static List<RecordDiff> getDiffs( List<Record> records1, List<Record> records2 ) {
        List<RecordDiff> diffs = new ArrayList<>();

        // For each Record in records1, see if we can match with a Record in records2
        for( Record r1: records1 ) {
            List<RecordDiff> matches = new ArrayList<>();

            boolean foundExactMatch = false;
            for( Record r2: records2 ) {
                double score = getComparisonScore( r1, r2 );
                if( score >= 1.0 ) {
                    // exact match, skip it
                    foundExactMatch = true;
                    logger.info( "Found exact match:\n" + r1.getDisplay() + "\n\n" + r2.getDisplay() );
                    break;
                }
                if( score > .5 ) {
                    matches.add( new RecordDiff( r1, r2, DiffType.Updated, score ) );
                    logger.info( "Found match (" + score + "):\n" + r1.getDisplay() + "\n\n" + r2.getDisplay() );
                }
            }

            if( foundExactMatch ) {
                continue;
            }

            if( matches.size() > 0 ) {
                // Sort by score (higher score moves to front of list)
                matches.sort( new Comparator<RecordDiff>() {
                    public int compare( RecordDiff rm1, RecordDiff rm2 ) {
                        if( rm1.getComparisonScore() > rm2.getComparisonScore() )
                            return -1;
                        if( rm1.getComparisonScore() < rm2.getComparisonScore() )
                            return 1;
                        return 0;
                    }

                    public boolean equals( Object o ) {
                        return false;
                    }
                });

                // Filter out all matches that are not within 10% of the best match
                double bestScore = matches.get( 0 ).getComparisonScore();
                logger.info( "Filtering out matches more than 0.1 less than " + bestScore );
                matches = matches.stream().filter( rd -> bestScore - rd.getComparisonScore() > 0.1 ).collect( Collectors.toList() );
                logger.info( "   " + matches.size() + " matches left" );

                for( RecordDiff rm: matches ) {
                    diffs.add( rm );
                }
            }
            else {
                // Record removed
                diffs.add( new RecordDiff( r1, null, DiffType.Removed, 0.0 ) );
            }
        }

        // Search for new Records added in records2
        for( Record r2: records2 ) {
            boolean foundMatch = false;
            for( RecordDiff rd: diffs ) {
                if( rd.getRecord2() == r2 ) {
                    // match, skip it
                    foundMatch = true;
                    break;
                }
            }
            if( foundMatch ) {
                continue;
            }

            // No RecordDiff for r2, it must be new
            diffs.add( new RecordDiff( null, r2, DiffType.Added, 0.0 ) );
        }

        int numUpdated = 0;
        int numRemoved = 0;
        int numAdded = 0;
        for( DiffType diffType: Arrays.asList( DiffType.Updated, DiffType.Removed, DiffType.Added ) ) {
            for( RecordDiff recordDiff : diffs ) {
                if( recordDiff.getDiffType() == diffType ) {
                    if( diffType == DiffType.Updated ) {
                        numUpdated += 1;
                        logger.info( "UPDATED" );
                        logger.info( "   Record 1: " + recordDiff.getRecord1().getDisplay() );
                        logger.info( "   Record 2: " + recordDiff.getRecord2().getDisplay() );
                        logger.info( "   Score: " + recordDiff.getComparisonScore() );
                    }
                    else if( diffType == DiffType.Added ) {
                        numAdded += 1;
                        logger.info( "ADDED" );
                        logger.info( "   Record 1: None" );
                        logger.info( "   Record 2: " + recordDiff.getRecord2().getDisplay() );
                    }
                    else if( diffType == DiffType.Removed ) {
                        numRemoved += 1;
                        logger.info( "REMOVED" );
                        logger.info( "   Record 1: " + recordDiff.getRecord1().getDisplay() );
                        logger.info( "   Record 2: None" );
                    }
                }
            }
        }
        logger.info( "Num Updated: " + numUpdated );
        logger.info( "Num Removed: " + numRemoved );
        logger.info( "Num Added:   " + numAdded   );

        return diffs;
    }

    /**
     * Compare the two Records.
     * score = (Number of equal Fields) / (Number of fields)
     * @param record1
     * @param record2
     * @return
     */
    private static double getComparisonScore( Record record1, Record record2 ) {
        double numEqualFields = 0.0;
        double numAddedFields = 0.0;
        double numRemovedFields = 0.0;

        for( String key: record1.getData().keySet() ) {
            String value1 = record1.getData().get( key );
            String value2 = null;
            if( record2.getData().containsKey( key ) ) {
                value2 = record2.getData().get( key );
            }

            if( value2 == null ) {
                numAddedFields += 1.0;
            }
            else if( value1.equalsIgnoreCase( value2 ) ){
                numEqualFields += 1.0;
            }
        }

        // Check for new fields (exist in record2, but not in record1)
        for( String key: record2.getData().keySet() ) {
            String value2 = record2.getData().get( key );
            if( !record1.getData().containsKey( key ) ) {
                numRemovedFields += 1.0;
            }
        }

        double totalNumFields = record1.getData().size();
        double score = numEqualFields / totalNumFields;

        return score;
    }
}

