package de.lmu.navigator.search;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import de.lmu.navigator.R;
import de.lmu.navigator.database.RealmDatabaseManager;
import de.lmu.navigator.database.model.Building;
import de.lmu.navigator.database.model.Room;

public class SearchAllActivity extends AbsSearchActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SearchAllActivity.class);
    }

    @Override
    public List<Searchable> getItems() {
        List<Searchable> items = new ArrayList<>();
        RealmDatabaseManager databaseManager = new RealmDatabaseManager();
        // Search for buildings
        for (Building b : databaseManager.getAllBuildings()) {
            items.add(SearchableWrapper.wrap(b));
        }
        // Search for named rooms
        for (Room r : databaseManager.getRoomsWithSynonym()) {
            items.add(SearchableWrapper.wrap(r, null));
        }
        databaseManager.close();
        return items;
    }

    @Override
    public int getSearchHintResId() {
        return R.string.search_hint_buildings;
    }
}
