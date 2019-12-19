package de.lmu.navigator.database.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

public class RoomSynonym extends RealmObject implements RealmLeaf<Room> {

    @Ignore
    @SerializedName("rCode")
    protected String parentId;

    @Required
    @SerializedName("syn")
    protected String name;

    protected Room parent;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Room getParent() {
        return parent;
    }

    @Override
    public void setParent(Room parent) {
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
