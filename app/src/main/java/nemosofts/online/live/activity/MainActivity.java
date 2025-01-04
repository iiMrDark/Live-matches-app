package nemosofts.online.live.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.nemosofts.view.ToggleView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.onesignal.Continue;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nemosofts.online.live.R;
import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.dialog.DialogUtil;
import nemosofts.online.live.executor.LoadAbout;
import nemosofts.online.live.fragment.online.FragmentCategories;
import nemosofts.online.live.fragment.online.FragmentEvent;
import nemosofts.online.live.interfaces.AboutListener;
import nemosofts.online.live.utils.IfSupported;
import nemosofts.online.live.utils.advertising.AdManagerInterAdmob;
import nemosofts.online.live.utils.advertising.AdManagerInterApplovin;
import nemosofts.online.live.utils.advertising.AdManagerInterStartApp;
import nemosofts.online.live.utils.advertising.AdManagerInterUnity;
import nemosofts.online.live.utils.advertising.AdManagerInterWortise;
import nemosofts.online.live.utils.advertising.AdManagerInterYandex;
import nemosofts.online.live.utils.advertising.GDPRChecker;
import nemosofts.online.live.utils.advertising.RewardAdAdmob;
import nemosofts.online.live.utils.advertising.RewardAdApplovin;
import nemosofts.online.live.utils.advertising.RewardAdStartApp;
import nemosofts.online.live.utils.advertising.RewardAdUnity;
import nemosofts.online.live.utils.advertising.RewardAdWortise;
import nemosofts.online.live.utils.helper.DBHelper;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.helper.SPHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fm;
    MenuItem menuLogin;
    MenuItem menuProfile;
    MenuItem menuSubscription;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    Helper helper;
    DBHelper dbHelper;
    SPHelper spHelper;
    NavigationView navigationView;

    //ToggleView navHome;
    ToggleView navCategories;
    ToggleView navEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IfSupported.isRTL(this);
        IfSupported.isScreenshot(this);

        fm = getSupportFragmentManager();

        helper = new Helper(this);
        dbHelper = new DBHelper(this);
        spHelper = new SPHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        toggle.setToolbarNavigationClickListener(view -> drawer.openDrawer(GravityCompat.START));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon = DrawableCompat.wrap(navigationIcon);
            DrawableCompat.setTint(navigationIcon, ContextCompat.getColor(MainActivity.this,
                    Boolean.TRUE.equals(new ThemeEngine(this).getIsThemeMode()) ? R.color.ns_white : R.color.ns_black));
            toolbar.setNavigationIcon(navigationIcon);
        }

        navigationView.setNavigationItemSelectedListener(this);

        new GDPRChecker(MainActivity.this).check();
        changeLoginName();
        loadAboutData();

        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            }
        });

        navEvent = findViewById(R.id.tv_nav_event);
        navEvent.setBadgeText("");
        navCategories = findViewById(R.id.tv_nav_category);

        navClickListener();
        loadDashboardFrag();

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        OneSignal.getNotifications().requestPermission(false, Continue.none());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleOnBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void navClickListener() {
        navEvent.setOnClickListener(view -> {
            if (!navEvent.isActive()) {
                pageChange(0);
            }
            bottomNavigationView(0);
        });

        navCategories.setOnClickListener(view -> {
            if (!navCategories.isActive()) {
                pageChange(1);
            }
            bottomNavigationView(1);
        });

    }

    private void loadDashboardFrag() {
        FragmentEvent f1 = new FragmentEvent();
        loadFrag(f1, getResources().getString(R.string.live_event), fm);
        navigationView.setCheckedItem(R.id.nav_event);
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStackImmediate();
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!name.equals(getString(R.string.live_event))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.fragment, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.fragment, f1, name);
        }
        ft.commit();

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(name);
        }
    }


    private void changeLoginName() {
        if (menuLogin != null) {
            menuSubscription.setVisible(true);
            if (spHelper.isLogged()) {
                menuProfile.setVisible(true);
                menuLogin.setTitle(getResources().getString(R.string.logout));
                menuLogin.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout));
                if (spHelper.getIsSubscribed()) {
                    menuSubscription.setVisible(false);
                }
            } else {
                menuProfile.setVisible(false);
                menuLogin.setTitle(getResources().getString(R.string.login));
                menuLogin.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_login));
            }
        }
    }

    public void loadAboutData() {
        if (helper.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(MainActivity.this, new AboutListener() {
                @Override
                public void onStart() {
                    // this method is empty
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (isFinishing() && !success.equals("1")) {
                        return;
                    }
                    dbHelper.addToAbout();
                    helper.initializeAds();
                    initAds();
                }
            });
            loadAbout.execute();
        } else {
            try {
                dbHelper.getAbout();
            } catch (Exception e) {
                Log.e("MainActivity", "Error getAbout", e);
            }
        }
    }

    private void initAds() {
        if (Boolean.TRUE.equals(Callback.getIsInterAd()) && (!spHelper.getIsSubscribed() || spHelper.getIsAdOn())) {
            switch (Callback.getAdNetwork()) {
                case Callback.AD_TYPE_ADMOB:
                    AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(getApplicationContext());
                    adManagerInterAdmob.createAd();
                    break;
                case Callback.AD_TYPE_STARTAPP:
                    AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(getApplicationContext());
                    adManagerInterStartApp.createAd();
                    break;
                case Callback.AD_TYPE_APPLOVIN:
                    AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(MainActivity.this);
                    adManagerInterApplovin.createAd();
                    break;
                case Callback.AD_TYPE_YANDEX:
                    AdManagerInterYandex adManagerInterYandex = new AdManagerInterYandex(MainActivity.this);
                    adManagerInterYandex.createAd();
                    break;
                case Callback.AD_TYPE_WORTISE:
                    AdManagerInterWortise adManagerInterWortise = new AdManagerInterWortise(MainActivity.this);
                    adManagerInterWortise.createAd();
                    break;
                case Callback.AD_TYPE_UNITY:
                    AdManagerInterUnity adManagerInterUnity = new AdManagerInterUnity();
                    adManagerInterUnity.createAd();
                    break;
                default:
                    break;
            }
        }
        if (Boolean.TRUE.equals(Callback.getIsRewardAd()) && (!spHelper.getIsSubscribed() || spHelper.getIsAdOn())) {
            switch (Callback.getAdNetwork()) {
                case Callback.AD_TYPE_ADMOB:
                    RewardAdAdmob rewardAdAdmob = new RewardAdAdmob(getApplicationContext());
                    rewardAdAdmob.createAd();
                    break;
                case Callback.AD_TYPE_STARTAPP:
                    RewardAdStartApp rewardAdStartApp = new RewardAdStartApp(getApplicationContext());
                    rewardAdStartApp.createAd();
                    break;
                case Callback.AD_TYPE_APPLOVIN:
                    RewardAdApplovin rewardAdApplovin = new RewardAdApplovin(MainActivity.this);
                    rewardAdApplovin.createAd();
                    break;
                case Callback.AD_TYPE_WORTISE:
                    RewardAdWortise rewardAdWortise = new RewardAdWortise(getApplicationContext());
                    rewardAdWortise.createAd();
                    break;
                case Callback.AD_TYPE_UNITY:
                    RewardAdUnity rewardAdUnity = new RewardAdUnity();
                    rewardAdUnity.createAd();
                    break;
                default:
                    break;
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_event) {
            if (!navEvent.isActive()){
                pageChange(0);
            }
            bottomNavigationView(0);

        }
        else if (id == R.id.nav_category) {
            FragmentCategories category = new FragmentCategories();
            loadFrag(category, getString(R.string.categories), fm);
            bottomNavigationView(1);
        } else if (id == R.id.nav_fav) {
            Intent intent = new Intent(MainActivity.this, PostIDActivity.class);
            intent.putExtra("page_type", getString(R.string.favourite));
            intent.putExtra("id", "");
            intent.putExtra("name", getString(R.string.favourite));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void bottomNavigationView(int pos) {
        if (navEvent == null || navCategories == null) {
            return;
        }

        // List of navigation items
        ToggleView[] navItems = {navEvent,navCategories};

        if (pos == 2) {
            deactivateAll(navItems);
            return;
        }

        for (int i = 0; i < navItems.length; i++) {
            if (i == pos) {
                if (!navItems[i].isActive()) {
                    navItems[i].activate();
                    navItems[i].setBadgeText("");
                }
            } else {
                if (navItems[i].isActive()) {
                    navItems[i].deactivate();
                    navItems[i].setBadgeText(null);
                }
            }
        }
    }

    private void deactivateAll(ToggleView[] navItems) {
        if (navItems == null) {
            return;
        }
        for (ToggleView navItem : navItems) {
            if (navItem.isActive()) {
                navItem.deactivate();
                navItem.setBadgeText(null);
            }
        }
    }

    @Override
    public void onResume() {
        changeLoginName();
        if (Boolean.TRUE.equals(Callback.isRecreate())) {
            Callback.setRecreate(false);
            recreate();
        }
        super.onResume();
    }

    @Override
    public int setContentViewID() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        try {
            dbHelper.close();
        } catch (Exception e) {
            Log.e("MainActivity", "Error in closing", e);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleOnBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleOnBack() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() != 0) {
            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
            if (title != null) {

                // Custom class to hold both navigation ID and bottom navigation index
                class NavInfo {
                    final int navId;
                    final int bottomNavIndex;

                    NavInfo(int navId, int bottomNavIndex) {
                        this.navId = navId;
                        this.bottomNavIndex = bottomNavIndex;
                    }
                }

                // Map to hold titles and corresponding NavInfo
                Map<String, NavInfo> titleToNavInfoMap = new HashMap<>();

                titleToNavInfoMap.put(getString(R.string.live_event), new NavInfo(R.id.nav_event, 0));
                titleToNavInfoMap.put(getString(R.string.categories), new NavInfo(R.id.nav_category, 1));

                // Update the navigation view and bottom navigation view if the title is in the map
                NavInfo navInfo = titleToNavInfoMap.get(title);
                if (navInfo != null) {
                    navigationView.setCheckedItem(navInfo.navId);
                    bottomNavigationView(navInfo.bottomNavIndex);
                    pageChange(navInfo.bottomNavIndex);
                }
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Objects.equals(title, getString(R.string.live_event)) ? getString(R.string.live_event) : title);
            }
        } else if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
            flow.addOnCompleteListener(task1 -> DialogUtil.exitDialog(MainActivity.this));
        } else {
            DialogUtil.exitDialog(MainActivity.this);
        }
    }

    private void pageChange(int bottomNavIndex) {
        if (bottomNavIndex == 0) {
            FragmentEvent event = new FragmentEvent();
            loadFrag(event, getString(R.string.live_event), fm);
        } else if (bottomNavIndex == 1) {
            FragmentCategories categories = new FragmentCategories();
            loadFrag(categories, getString(R.string.categories), fm);
        }
    }
}