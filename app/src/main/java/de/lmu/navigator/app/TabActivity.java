package de.lmu.navigator.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.lmu.navigator.R;
import de.lmu.navigator.database.model.Building;
import de.lmu.navigator.database.model.Version;
import de.lmu.navigator.map.ClusterMapFragment;
import de.lmu.navigator.search.AbsSearchActivity;
import de.lmu.navigator.search.SearchBuildingActivity;
import de.lmu.navigator.update.RestService;
import de.lmu.navigator.update.RetrofitRestClient;
import de.lmu.navigator.update.UpdateService;
import io.realm.RealmResults;
import me.alexrs.prefs.lib.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TabActivity extends BaseActivity {

    private static final String LOG_TAG = TabActivity.class.getSimpleName();

    public static final int REQUEST_CODE_SEARCH_BUILDING = 1;
    public static final int REQUEST_CODE_ADD_FAVORITE = 2;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;

    @InjectView(R.id.pager)
    ViewPager mPager;

    private RealmResults<Building> mBuildings;
    private MaterialDialog mUpdateProgressDialog;
    private MenuItem mUpdateMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        setTitle(R.string.lmu_navigator);

        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        mPager.setAdapter(new MyPagerAdapter());
        mPager.setOffscreenPageLimit(2);
        mTabs.setViewPager(mPager);

        mBuildings = mDatabaseManager.getAllBuildings(true);

        checkVersion();
    }

    public RealmResults<Building> getBuildings() {
        return mBuildings;
    }

    private void checkVersion() {
        // for testing
//        Prefs.with(this).remove(Preferences.DATA_VERSION);
//        Prefs.with(this).remove(Preferences.UPDATE_PENDING);

        if (Prefs.with(this).getBoolean(Preferences.UPDATE_PENDING, false)) {
            return;
        }

        RestService restService = RetrofitRestClient.create();
        restService.getVersionAsync(new Callback<Version>() {
            @Override
            public void success(Version version, Response response) {
                if (version.version > Prefs.with(TabActivity.this).getInt(Preferences.DATA_VERSION,
                        Preferences.SHIPPED_DATA_VERSION)) {
                    showUpdateDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(LOG_TAG, "Could not check data version! Cause:\n" + error.getMessage());
            }
        });
    }

    private void showUpdateDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.update_dialog_title)
                .content(R.string.update_dialog_message)
                .cancelable(false)
                .positiveText(R.string.update_dialog_accept)
                .negativeText(R.string.update_dialog_postpone)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        startService(new Intent(TabActivity.this, UpdateService.class));
                        showUpdateProgressDialog();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Prefs.with(TabActivity.this).save(Preferences.UPDATE_PENDING, true);
                        invalidateOptionsMenu();
                        // TODO: show hint!
                    }
                })
                .show();
    }

    private void showUpdateProgressDialog() {
        mUpdateProgressDialog = new MaterialDialog.Builder(this)
                .content(R.string.update_progress_message)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    private void onUpdateFinished(boolean success) {
        if (success) {
            mUpdateProgressDialog.dismiss();
            new MaterialDialog.Builder(this)
                    .title(R.string.update_success_title)
                    .content(R.string.update_success_message)
                    .positiveText(R.string.update_button_ok)
                    .show();
        } else {
            mUpdateProgressDialog.dismiss();
            new MaterialDialog.Builder(this)
                    .title(R.string.update_failure_title)
                    .content(R.string.update_failure_message)
                    .positiveText(R.string.update_button_ok)
                    .show();
        }
        invalidateOptionsMenu();
    }

    public void onEventMainThread(UpdateService.UpdateSuccessEvent e) {
        onUpdateFinished(true);
    }

    public void onEventMainThread(UpdateService.UpdateFailureEvent e) {
        onUpdateFinished(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab, menu);
        mUpdateMenuItem = menu.findItem(R.id.download);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean updatePending = Prefs.with(this).getBoolean(Preferences.UPDATE_PENDING, false);
        mUpdateMenuItem.setVisible(updatePending);
        mUpdateMenuItem.setEnabled(updatePending);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivityForResult(SearchBuildingActivity.newIntent(this),
                        REQUEST_CODE_SEARCH_BUILDING);
                return true;

            case R.id.settings:
                Toast.makeText(this, "TODO Einstellungen", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.impressum:
                Toast.makeText(this, "TODO Impressum", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.download:
                showUpdateDialog();
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private enum Tabs {

        FAVORITES(FavoritesFragment.class, R.string.tab_favorites),
        ALL(AllFragment.class, R.string.tab_all),
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
            return Fragment.instantiate(TabActivity.this, className);
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