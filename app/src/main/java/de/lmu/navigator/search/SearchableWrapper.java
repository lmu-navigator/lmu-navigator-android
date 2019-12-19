package de.lmu.navigator.search;

import de.lmu.navigator.database.ModelHelper;
import de.lmu.navigator.database.model.Building;
import de.lmu.navigator.database.model.Room;

public class SearchableWrapper implements Searchable {

    private String mPrimaryText;

    private String mSecondaryText;

    private String mCode;

    private String mClassName;

    public static Searchable wrap(Room room, String buildingPartHint) {
        return new SearchableWrapper(room, buildingPartHint);
    }

    public static Searchable wrap(Building building) {
        return new SearchableWrapper(building);
    }

    private SearchableWrapper(Room room, String buildingPartHint) {
        mPrimaryText = ModelHelper.getName(room);
        mSecondaryText = ModelHelper.getDescription(room);
        mCode = room.getCode();
        mClassName = Room.class.getSimpleName();

        if (buildingPartHint != null) {
            mSecondaryText = mSecondaryText + " (" + buildingPartHint + ")";
        }
    }

    private SearchableWrapper(Building building) {
        mPrimaryText = ModelHelper.getName(building);
        mSecondaryText = ModelHelper.getDescription(building);
        mCode = building.getCode();
        mClassName = Building.class.getSimpleName();
    }


    @Override
    public String getPrimaryText() {
        return mPrimaryText;
    }

    @Override
    public String getSecondaryText() {
        return mSecondaryText;
    }

    @Override
    public String getCode() {
        return mCode;
    }

    @Override
    public String getClassName() {
        return mClassName;
    }
}
