package de.lmu.navigator.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.lmu.navigator.R;
import de.lmu.navigator.database.ModelHelper;
import de.lmu.navigator.database.model.Building;
import de.lmu.navigator.database.model.Room;
import de.lmu.navigator.indoor.FloorViewActivity;
import de.lmu.navigator.map.ClusterMapFragment;
import de.lmu.navigator.preferences.Preferences;
import de.lmu.navigator.preferences.SettingsActivity;
import de.lmu.navigator.search.AbsSearchActivity;
import de.lmu.navigator.search.SearchAllActivity;
import de.lmu.navigator.search.SearchBuildingActivity;
import io.realm.RealmResults;
import me.alexrs.prefs.lib.Prefs;

public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CODE_SEARCH_BUILDING = 1;
    public static final int REQUEST_CODE_ADD_FAVORITE = 2;
    public static final int REQUEST_CODE_SEARCH_ALL = 3;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mTabs;

    @BindView(R.id.pager)
    ViewPager mPager;

    private RealmResults<Building> mBuildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        setTitle(R.string.lmu_navigator);

        ButterKnife.bind(this);

        mPager.setAdapter(new MyPagerAdapter());
        mPager.setOffscreenPageLimit(2);
        mTabs.setViewPager(mPager);

        mBuildings = mDatabaseManager.getAllBuildings();
    }

    public RealmResults<Building> getBuildings() {
        return mBuildings;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                if (Prefs.with(this).getBoolean(Preferences.KEY_SEARCH_ADVANCED, true)) {
                    startActivityForResult(SearchAllActivity.newIntent(this),
                            REQUEST_CODE_SEARCH_ALL);
                } else {
                    startActivityForResult(SearchBuildingActivity.newIntent(this),
                            REQUEST_CODE_SEARCH_BUILDING);
                }
                return true;

            case R.id.settings:
                startActivity(SettingsActivity.newIntent(this));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SEARCH_BUILDING:
                if (resultCode == RESULT_OK) {
                    String buildingCode = data.getStringExtra(AbsSearchActivity.KEY_SEARCH_RESULT);
                    startActivity(BuildingDetailActivity.newIntent(this, buildingCode));
                }
                break;
            case REQUEST_CODE_SEARCH_ALL:
                if (resultCode == RESULT_OK) {
                    String code = data.getStringExtra(AbsSearchActivity.KEY_SEARCH_RESULT);
                    String className = data.getStringExtra(AbsSearchActivity.CLASS_NAME);
                    if(Building.class.getSimpleName().equals(className)) {
                        startActivity(BuildingDetailActivity.newIntent(this, code));
                    } else if (Room.class.getSimpleName().equals(className)) {
                        Building building = mDatabaseManager.getRoom(code).getFloor().getBuildingPart().getBuilding();
                        startActivity(BuildingDetailActivity.newIntent(this, building.getCode()));
                        startActivity(FloorViewActivity.newIntent(this, building, code));
                    }
                }
                break;

            case REQUEST_CODE_ADD_FAVORITE:
                if (resultCode == RESULT_OK) {
                    onAddFavoriteResult(data);
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onAddFavoriteResult(Intent data) {
        String buildingCode = data.getStringExtra(AbsSearchActivity.KEY_SEARCH_RESULT);
        Building building = mDatabaseManager.getBuilding(buildingCode);
        mDatabaseManager.setBuildingStarred(building, true);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Display synonym name instead of address
        if (Prefs.with(this).getBoolean(Preferences.KEY_DISPLAY_SYNONYM, true)) {
            ModelHelper.displaySynonyms = true;
        } else {
            ModelHelper.displaySynonyms = false;
        }

    }

    private enum Tabs {

        FAVORITES(FavoritesFragment.class, R.string.tab_favorites),
        ALL(AllBuildingsFragment.class, R.string.tab_all),
        MAP(ClusterMapFragment.class, R.string.tab_map);

        Class<? extends Fragment> fragmentClass;
        int titleRes;

        Tabs(Class<? extends Fragment> fragmentClass, @StringRes int titleRes) {
            this.fragmentClass = fragmentClass;
            this.titleRes = titleRes;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            String className = Tabs.values()[position].fragmentClass.getName();
            return Fragment.instantiate(MainActivity.this, className);
        }

        @Override
        public int getCount() {
            return Tabs.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(Tabs.values()[position].titleRes);
        }
    }
}
