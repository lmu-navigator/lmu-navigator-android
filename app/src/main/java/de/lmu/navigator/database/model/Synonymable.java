package de.lmu.navigator.database.model;

import java.util.List;

/**
 * Interface to avoid duplicate code fragments for "synonymable" elements.
 *
 * @param <T> The class of the database elements, which can have synonyms.
 */
public interface Synonymable<T extends Synonymable<T>> {

    boolean hasSynonyms();

    /**
     * @param <U> the class of the synonym (of type T)
     * @return
     */
    <U extends RealmLeaf<T>> List<U> getSynonyms();

    String getName();
}
