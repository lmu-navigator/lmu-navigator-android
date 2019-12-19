package de.lmu.navigator.database.model;

/**
 * This interface specifies the last element in a Realm tree, as it only has a parent, but no child elements.
 * TODO make to abstract class if polymorphism is supported: https://github.com/realm/realm-java/issues/761
 *
 * @param <T> the type of the parent element.
 */
public interface RealmLeaf<T> {

    String getName();

    void setName(String name);

    T getParent();

    void setParent(T parent);

    String getParentId();

    void setParentId(String parentId);
}
