package de.lmu.navigator.search;

import java.util.Locale;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * See: https://github.com/xdrop/fuzzywuzzy
 */
public class FuzzyWuzzyStringMetric extends AbstractStringMetric {

    @Override
    public String getLongDescriptionString() {
        return null;
    }

    @Override
    public String getShortDescriptionString() {
        return null;
    }

    @Override
    public float getSimilarity(String compString, String searchString) {
        // Give score independent of position in word and order of search words
        return FuzzySearch.tokenSortPartialRatio(searchString.toLowerCase(Locale.GERMAN), compString.toLowerCase(Locale.GERMAN)) / 100f;
    }

    @Override
    public String getSimilarityExplained(String arg0, String arg1) {
        return null;
    }

    @Override
    public float getSimilarityTimingEstimated(String arg0, String arg1) {
        return 0;
    }

    @Override
    public float getUnNormalisedSimilarity(String compString,
                                           String searchString) {
        return 0;
    }
}
