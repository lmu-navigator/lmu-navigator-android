package de.lmu.navigator.database.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

public class BuildingSynonym extends RealmObject implements RealmLeaf<Building> {

    protected Building parent;

    @Ignore
    @SerializedName("bCode")
    protected String parentId;

    @Required
    @SerializedName("syn")
    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Building getParent() {
        return parent;
    }

    @Override
    public void setParent(Building parent) {
        this.parent = parent;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
