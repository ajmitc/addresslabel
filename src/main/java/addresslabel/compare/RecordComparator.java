package addresslabel.compare;

import addresslabel.Record;
import addresslabel.view.RecordComparatorProgressBarWorker;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.*;

public class RecordComparator {
    private static Logger logger = Logger.getLogger(RecordComparator.class.getName());

    private static final double MIN_MATCH_SCORE = 0.5;

    public static List<RecordDiff> getDiffs(List<Record> records1, List<Record> records2, RecordComparatorProgressBarWorker worker) {
        List<RecordDiff> diffs = new ArrayList<>();

        // For each Record in records1, see if we can match with a Record in records2
        worker.publish(1, "Finding exact matches and removed records...");
        List<Record> exactMatches = new ArrayList<>();
        for (Record r1 : records1) {
            List<RecordDiff> matches = new ArrayList<>();

            boolean foundExactMatch = false;
            for (Record r2 : records2) {
                // Ignore any Records that we've already matched exactly to another Record
                if (exactMatches.contains(r2)) {
                    continue;
                }

                // Check if this Record exactly matches r1
                if (r1.equals(r2)) {
                    foundExactMatch = true;
                    exactMatches.add(r1);
                    exactMatches.add(r2);
                    break;
                }

                // OK, they don't exactly match, see how close they are to each other.
                double score = getComparisonScore(r1, r2);
                if (score > MIN_MATCH_SCORE) {
                    matches.add(new RecordDiff(r1, r2, DiffType.Updated, score));
                    logger.info("Found match (" + score + "):\n" + r1.getDisplay() + "\n\n" + r2.getDisplay());
                }
                worker.publish(1, null);
            }

            if (foundExactMatch) {
                continue;
            }

            if (matches.size() > 0) {
                // Sort by score (higher score moves to front of list)
                matches.sort(new Comparator<RecordDiff>() {
                    public int compare(RecordDiff rm1, RecordDiff rm2) {
                        if (rm1.getComparisonScore() > rm2.getComparisonScore())
                            return -1;
                        if (rm1.getComparisonScore() < rm2.getComparisonScore())
                            return 1;
                        return 0;
                    }

                    public boolean equals(Object o) {
                        return false;
                    }
                });

                // Filter out all matches that are not within 10% of the best match
                double bestScore = matches.stream().max(Comparator.comparing(RecordDiff::getComparisonScore)).get().getComparisonScore();
                double minScore = bestScore - (bestScore / 10.0);
                logger.info("Filtering out matches less than " + minScore);
                matches = matches.stream().filter(rd -> rd.getComparisonScore() >= minScore).collect(Collectors.toList());
                logger.info("   " + matches.size() + " matches left");

                for (RecordDiff rm : matches) {
                    diffs.add(rm);
                }
            } else {
                // Record removed
                diffs.add(new RecordDiff(r1, null, DiffType.Removed, 0.0));
            }
        }

        // Search for new Records added in records2
        // These will be Records that are not exact matches and are not a close enough match to an existing Record
        worker.publish(1, "Finding new records...");
        for (Record r2 : records2) {
            boolean foundMatch = exactMatches.contains(r2);
            if (!foundMatch) {
                for (RecordDiff rd : diffs) {
                    if (rd.getRecord2() == r2) {
                        // match, skip it
                        foundMatch = true;
                        break;
                    }
                }
            }
            if (foundMatch) {
                continue;
            }

            // No RecordDiff for r2, it must be new
            diffs.add(new RecordDiff(null, r2, DiffType.Added, 0.0));
        }

        int numUpdated = 0;
        int numRemoved = 0;
        int numAdded = 0;
        for (DiffType diffType : Arrays.asList(DiffType.Updated, DiffType.Removed, DiffType.Added)) {
            for (RecordDiff recordDiff : diffs) {
                if (recordDiff.getDiffType() == diffType) {
                    if (diffType == DiffType.Updated) {
                        numUpdated += 1;
                        logger.info("UPDATED");
                        logger.info("   Record 1: " + recordDiff.getRecord1().getDisplay());
                        logger.info("   Record 2: " + recordDiff.getRecord2().getDisplay());
                        logger.info("   Score: " + recordDiff.getComparisonScore());
                    } else if (diffType == DiffType.Added) {
                        numAdded += 1;
                        logger.info("ADDED");
                        logger.info("   Record 1: None");
                        logger.info("   Record 2: " + recordDiff.getRecord2().getDisplay());
                    } else if (diffType == DiffType.Removed) {
                        numRemoved += 1;
                        logger.info("REMOVED");
                        logger.info("   Record 1: " + recordDiff.getRecord1().getDisplay());
                        logger.info("   Record 2: None");
                    }
                }
            }
        }
        logger.info("Num Updated: " + numUpdated);
        logger.info("Num Removed: " + numRemoved);
        logger.info("Num Added:   " + numAdded);

        return diffs;
    }

    /**
     * Compare the two Records.
     * score = Sum(comparison of each field)
     *
     * @param record1
     * @param record2
     * @return
     */
    private static double getComparisonScore(Record record1, Record record2) {
        double totalScore = 0.0;

        logger.info("Checking for updated/removed fields");
        for (String key1 : record1.getData().keySet()) {
            String value1 = record1.getData().get(key1);

            double highestScore = 0.0;
            for (String key2 : record2.getData().keySet()) {
                String value2 = record2.getData().get(key2);

                if (value1 != null && value2 != null && (!value1.isEmpty() || !value2.isEmpty())) {
                    double score = StringSimilarity.similarity(value1, value2);
                    logger.info("Comparing '" + value1 + "' to '" + value2 + "': " + score);
                    highestScore = Math.max(highestScore, score);
                }
            }
            totalScore += highestScore;
        }

        // Check for new fields (exist in record2, but not in record1)
        logger.info("Checking for new fields");
        for (String key : record2.getData().keySet()) {
            String value2 = record2.getData().get(key);
            if (!record1.getData().containsKey(key) && !value2.isEmpty()) {
                double score = StringSimilarity.similarity("", value2);
                logger.info("Comparing '' to '" + value2 + "': " + score);
                totalScore += score;
            }
        }

        logger.info("Total Score: " + totalScore);
        return totalScore;
    }
}

