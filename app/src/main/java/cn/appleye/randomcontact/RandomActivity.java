package cn.appleye.randomcontact;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
/*import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;*/
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.appleye.randomcontact.common.list.TabState;
import cn.appleye.randomcontact.common.list.ViewPagerTabs;
import cn.appleye.randomcontact.widget.MenuPopupWindow;

public class RandomActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPagerTabs mViewPagerTabs;
    private String[] mTabTitles;
    private ViewPager mTabPager;
    private TabPagerAdapter mTabPagerAdapter;
    private TabPagerListener mTabPagerListener = new TabPagerListener();

    private ContactsFragment mContactsFragment;
    private GenerateFragment mGenerateFragement;

    private MenuPopupWindow mMenuPopup;

    /* 两次返回键之间的间隔 */
    private long exitTime = 0;

    private static final int CONTACTS_PERMISSION_GRANTED = 1;

    private boolean mAllPermissionGranted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS}, CONTACTS_PERMISSION_GRANTED);
            }else {
                initContentView();
            }
        } else {
            initContentView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACTS_PERMISSION_GRANTED) {
            int length = grantResults.length;
            if (length > 0) {
                mAllPermissionGranted = true;
                for (int i=0; i<length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        mAllPermissionGranted = false;
                        break;
                    }
                }
                if (mAllPermissionGranted) {
                    initContentView();
                } else {
                    Toast.makeText(this, getString(R.string.contacts_permission_request), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void initContentView() {
        setContentView(R.layout.activity_random);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mViewPagerTabs = (ViewPagerTabs)findViewById(R.id.lists_pager_header);

        mTabTitles = new String[TabState.COUNT];
        mTabTitles[TabState.CONTACTS] = getString(R.string.contacts);
        mTabTitles[TabState.GENERATE] = getString(R.string.contacts_generate);

        mTabPagerAdapter = new TabPagerAdapter();
        mTabPager = (ViewPager)findViewById(R.id.tab_pager);
        mTabPager.setAdapter(mTabPagerAdapter);
        mTabPager.setOnPageChangeListener(mTabPagerListener);

        mViewPagerTabs.setViewPager(mTabPager);

        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        mContactsFragment = new ContactsFragment();
        mGenerateFragement = new GenerateFragment();

        final String CONTACTS_TAG = "tab-pager-contacts";
        final String GENERATE_TAG = "tab-pager-generate";

        transaction.add(R.id.tab_pager, mGenerateFragement, GENERATE_TAG);
        transaction.add(R.id.tab_pager, mContactsFragment, CONTACTS_TAG);

        transaction.hide(mContactsFragment);
        transaction.hide(mGenerateFragement);

        transaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_random, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu){
        mMenuPopup = new MenuPopupWindow(this, this);
        mMenuPopup.showAtLocation(findViewById(R.id.content),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_basic_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_advanced_settings) {
            Intent intent = new Intent(this, AdvancedSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_basic_settings:{
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.menu_advanced_settings:{
                Intent intent = new Intent(this, AdvancedSettingsActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 连续两个返回键退出
     * */
    public void onBackPressed() {

        if (mAllPermissionGranted) {
            if (mMenuPopup!= null && mMenuPopup.isShowing()) {
                mMenuPopup.dismiss();
                return;
            }

            exit();
        } else {
            super.onBackPressed();
        }
    }

    public void onPause() {
        super.onPause();
        if (mMenuPopup!= null && mMenuPopup.isShowing()) {
            mMenuPopup.dismiss();
        }
    }

    public void exit() {
        int postion = mViewPagerTabs.getTabPositionSelected();
        if (postion != TabState.GENERATE) {
            if (postion == TabState.CONTACTS) {
                if (mGenerateFragement.onBackPressed()) {
                    mTabPager.setCurrentItem(TabState.GENERATE);
                }
            }
        } else {
            if ((System.currentTimeMillis() - exitTime) > 1000) {
                Toast.makeText(this,
                        getString(R.string.keyback_hint), Toast.LENGTH_SHORT)
                        .show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
        }
    }

    private class TabPagerAdapter extends PagerAdapter {
        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;

        private boolean mAreTabsHiddenInTabPager;

        private Fragment mCurrentPrimaryItem;

        public TabPagerAdapter() {
            mFragmentManager = getFragmentManager();
        }

        public boolean areTabsHidden() {
            return mAreTabsHiddenInTabPager;
        }

        public void setTabsHidden(boolean hideTabs) {
            if (hideTabs == mAreTabsHiddenInTabPager) {
                return;
            }
            mAreTabsHiddenInTabPager = hideTabs;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAreTabsHiddenInTabPager ? 1 : TabState.COUNT;
        }

        /** Gets called when the number of items changes. */
        @Override
        public int getItemPosition(Object object) {
            if (object == mContactsFragment) {
                return TabState.CONTACTS;
            } else if (object == mGenerateFragement) {
                 return TabState.GENERATE;
            }

            return TabState.CONTACTS;
        }

        @Override
        public void startUpdate(ViewGroup container) {
        }

        private Fragment getFragment(int position) {
            if (position == TabState.CONTACTS) {
                return mContactsFragment;
            } else if (position == TabState.GENERATE) {
                return mGenerateFragement;
            }

            throw new IllegalArgumentException("position: " + position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            Fragment f = getFragment(position);
            mCurTransaction.show(f);

            // Non primary pages are not visible.
            f.setUserVisibleHint(f == mCurrentPrimaryItem);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.hide((Fragment) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (mCurrentPrimaryItem != fragment) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    private class TabPagerListener implements ViewPager.OnPageChangeListener {
        TabPagerListener() {}

        @Override
        public void onPageScrollStateChanged(int state) {
            if (!mTabPagerAdapter.areTabsHidden()) {
                mViewPagerTabs.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (!mTabPagerAdapter.areTabsHidden()) {
                mViewPagerTabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            // Make sure not in the search mode, in which case position != TabState.ordinal().
            if (!mTabPagerAdapter.areTabsHidden()) {
                mViewPagerTabs.onPageSelected(position);
            }
        }
    }
}
