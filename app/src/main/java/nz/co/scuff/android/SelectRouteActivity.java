package nz.co.scuff.android;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.school.Bus;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.util.DialogHelper;
import nz.co.scuff.util.ScuffContextProvider;


public class SelectRouteActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static final String TAG = "SelectRouteActivity";
    private static final boolean D = false;

    static final String CHOSEN_ROUTE = "ROUTE";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        //TabsAdapter ta = new TabsAdapter(this, mViewPager);
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Intent intent = new Intent();
        Family family = ((ScuffContextProvider)getApplicationContext()).getFamily();
        Route selectedRoute = null;
        for (School school : family.getSchools()) {
            for (Route route : school.getRoutes()) {
                if (route.getName().equals(tab.getText()))
                    selectedRoute = route;
            }
        }
        intent.putExtra(CHOSEN_ROUTE, selectedRoute);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     *
     * Loads and returns a route map fragment corresponding to the tab
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Route> routes;

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);

            Family family = ((ScuffContextProvider)getApplicationContext()).getFamily();
            ArrayList<Route> routes = new ArrayList<Route>();
            for (School school : family.getSchools()) {
                routes.addAll(school.getRoutes());
            }
            this.routes = routes;

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            return RouteMapFragment.newInstance(position + 1, this.routes.get(position).getRouteMap());
        }

        @Override
        public int getCount() {
            return this.routes.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return this.routes.get(position).getName();
        }
    }

    /**
     * A fragment containing a map view.
     */
    public static class RouteMapFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMAGE_PATH = "image_path";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static RouteMapFragment newInstance(int sectionNumber, String imagePath) {
            RouteMapFragment fragment = new RouteMapFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMAGE_PATH, imagePath);
            fragment.setArguments(args);
            return fragment;
        }

        public RouteMapFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_select_route, container, false);
            //return rootView;

            ScrollView scroller = new ScrollView(container.getContext());
            //scroller.setBackgroundColor(android.R.color.transparent);
            scroller.setLayoutParams(container.getLayoutParams());
            ImageView imageView = new ImageView(container.getContext());

            String fileLocation = getActivity().getFilesDir() + "/" + this.getArguments().getString(ARG_IMAGE_PATH);
            if (D) Log.d(TAG, "file location = "+ fileLocation);
            Bitmap bitmap = BitmapFactory.decodeFile(fileLocation);
            //imageView.setImageResource(R.drawable.route1);
            imageView.setImageBitmap(bitmap);
            scroller.addView(imageView);

            return scroller;

        }
    }

}
