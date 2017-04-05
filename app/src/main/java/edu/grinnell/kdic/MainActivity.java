package edu.grinnell.kdic;

import  android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Stack;

import edu.grinnell.kdic.schedule.GetSchedule;
import edu.grinnell.kdic.schedule.Schedule;
import edu.grinnell.kdic.schedule.ScheduleFragment;
import edu.grinnell.kdic.visualizer.VisualizeFragment;

public class MainActivity extends AppCompatActivity {
    //Set the tag for Main Activity class
    public static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mNavigationToolbar;
    private Toolbar mPlaybackToolbar;
    private ImageView mPlayPauseButton;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Stack<Integer> mBackStack;
    private float shiftAmnt;
    private FavoritesFragment mFavoritesFragment;
    private VisualizeFragment mVisualizeFragment;
    private ScheduleFragment mScheduleFragment;


    // for RadioService
    private RadioService radioService;
    private boolean boundToRadioService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Because we have bound to an explicit
            // service that is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            RadioService.RadioBinder binder = (RadioService.RadioBinder) service;
            radioService = binder.getService();
            boundToRadioService = true;

            // if the stream is playing, then stop the notification and change the button from
            // pause to play
            if (radioService.isPlaying()) {
                radioService.hideNotification();
                mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
        }
        // Called when the connection with the service disconnects unexpectedly
        // Changed the boolean boundToRadioService to false.
        public void onServiceDisconnected(ComponentName className) {
            boundToRadioService = false;
        }
    };

    /**
     *  Generate main layout, navigation drawer for the main activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout to use
        setContentView(R.layout.activity_main);
        // setup the nav drawer and navigation functionality
        setupNavigation();
        setupFragments(savedInstanceState);
        setupPlaybackToolbar(); // setup the playback toolbar

        // update the schedule if connected to internet
        if (NetworkState.isOnline(this))
            updateSchedule();
    }

    /**
     *Bind the radio services after the activity is being displayed to the user
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Start an intent to bind to the radio service
        Intent intent = new Intent(this, RadioService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        if (mBackStack.peek() != R.id.visualizer)
            updateShowNamePlaybackToolbar();
    }

    /**
     *Set up icon for the pause and play button
     */
    @Override
    protected void onResume() {
        super.onResume();
        // if the stream is playing, then stop the notification
        if (boundToRadioService && radioService.isPlaying()) {
            mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    /**
     * Stop the activity
     */
    @Override
    protected void onStop() {
        super.onStop();
        // if the stream is playing, then start the notification
        if (boundToRadioService && radioService.isPlaying()) {
            radioService.showNotification();
        }
        // unbind the radio service
        unbindService(mConnection);
        boundToRadioService = false;

    }

    /**
     * Set up the playback bar on the bottom of the activity
     */
    private void setupPlaybackToolbar() {
        //Find the layout to populate into the screen
        mPlaybackToolbar = (Toolbar) findViewById(R.id.playback_toolbar);

        //Handling response after clicking on the bar
        //Generate/Hide the visualizer action according to the current element in backstack

        View.OnClickListener onToggleVisualizeFragment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radioService.isLoading()) {
                    if (mBackStack.peek() != R.id.visualizer) {
                        showVisualizeFragment();
                        mBackStack.add(R.id.visualizer);
                    } else {
                        hideVisualizeFragment();
                        mBackStack.pop();
                    }
                    updateNavigationView();
                }
            }
        };


        mPlaybackToolbar.setNavigationOnClickListener(onToggleVisualizeFragment);
        mPlaybackToolbar.setOnClickListener(onToggleVisualizeFragment);

        //Handling pause button and set on click method
        mPlayPauseButton = (ImageView) findViewById(R.id.ib_play_pause);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPauseButton(v);
            }
        });
    }

    /**
     * Set response after clicking the pause button
     * @param v
     */
    public void onClickPauseButton(View v) {
        if (radioService.isPlaying()) {
            // pause
            radioService.pause();
            // switch to play icon
            mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            // play
            if (NetworkState.isOnline(MainActivity.this)) {
                  if (!radioService.isLoading()) {
                    if (radioService.isLoaded()) {
                        mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                        mPlayPauseButton.clearAnimation();

                    } else {
                        mPlayPauseButton.setImageResource(R.drawable.ic_loading_spinner);

                        // rotation to use for loading icon
                        RotateAnimation rotate;
                        // different center point for rotation if playPauseButton is in the
                        // center of the screen
                        if (mBackStack.peek() != R.id.visualizer)
                            rotate = new RotateAnimation(0, 360,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                    0.5f);
                        else {
                            float shiftX = mPlaybackToolbar.getWidth() / -2 + mPlayPauseButton.getWidth();
                            float shiftY = mPlaybackToolbar.getHeight() / 2;
                            rotate = new RotateAnimation(0, 360, shiftX, shiftY);
                        }
                        //Set duration of 1000 milliseconds
                        rotate.setDuration(1000);
                        rotate.setRepeatCount(Animation.INFINITE);
                        rotate.setInterpolator(new LinearInterpolator());

                        mPlayPauseButton.startAnimation(rotate);
                    }
                    radioService.setRunOnStreamPrepared(new Runnable() {
                        @Override
                        public void run() {
                            mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                            mPlayPauseButton.clearAnimation();
                        }
                    });
                    radioService.play();
                }
            } else {
                showNoInternetDialog();
            }
        }
    }

    private void setupFragments(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            mScheduleFragment = new ScheduleFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mScheduleFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, mScheduleFragment, ScheduleFragment.TAG)
                    .commit();
            mBackStack.add(R.id.schedule);


            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, 0);
            if (sharedPreferences.getBoolean(Constants.FIRST_RUN, true)) {
                GetSchedule getSchedule = new GetSchedule(MainActivity.this, mScheduleFragment);
                getSchedule.execute();
                sharedPreferences.edit().putBoolean(Constants.FIRST_RUN, false).apply();
            }

            mVisualizeFragment = new VisualizeFragment();
        }
    }

    /**
     * Set up navigation drawer bar
     */
    private void setupNavigation() {
        // get toolbar and nav drawer
        mNavigationToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set toolbar as actionbar
        setSupportActionBar(mNavigationToolbar);

        // initialize navigation drawer
        mNavigationToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        mNavigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // set up backstack
        mBackStack = new Stack<>();

        // set onclick listeners to navigation menu items
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (mBackStack.peek() != menuItem.getItemId()) {
                    if (mBackStack.peek() == R.id.visualizer) {
                        hideVisualizeFragment();
                        mBackStack.pop();
                    }
                    mBackStack.push(menuItem.getItemId());

                    //Create fragment transaction to handle response to call from the navigation drawer
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    switch (menuItem.getItemId()) {
                        case R.id.schedule:
                            //generate schedule fragment after clicking on Schedule
                            ft.replace(R.id.fragment, mScheduleFragment, ScheduleFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case R.id.visualizer:
                            //Generate now playing response
                            showVisualizeFragment();
                            break;
                        case R.id.favorites:
                            //Generate favorite response
                            ft.replace(R.id.fragment,
                                    mFavoritesFragment == null ? new FavoritesFragment() : mFavoritesFragment,
                                    FavoritesFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case R.id.blog:
                            //Generate blog fragment in response to the blog button
                            ft.replace(R.id.fragment, new BlogWebViewFragment(), BlogWebViewFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case R.id.about:
                            //Generate about fragment in response to the about button
                            ft.replace(R.id.fragment, new AboutFragment(), AboutFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        default:
                            break;
                    }
                }
                // close the drawer after something is clicked
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    /**
     * Highlight selected item
     */
    public void updateNavigationView() {
        mNavigationView.setCheckedItem(mBackStack.peek());
    }

    /**
     * Hide the station visualizer
     */
    public void hideVisualizeFragment() {
        if (!radioService.isLoading()) {
            // hide visualize fragment
            mPlaybackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
            updateShowNamePlaybackToolbar();
            getSupportFragmentManager().popBackStack();

            // move the play button to the right
            shiftAmnt = (mPlaybackToolbar.getWidth() - mPlayPauseButton.getWidth()) / 2;

            TranslateAnimation animation = new TranslateAnimation(0, shiftAmnt, 0, 0);
            animation.setDuration(200); //milliseconds
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setFillAfter(false);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mPlayPauseButton.setTranslationX(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mPlayPauseButton.startAnimation(animation);

            // move the info onto the screen
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(200);
            alphaAnimation.setStartOffset(100);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setInterpolator(new AccelerateInterpolator());
            findViewById(R.id.ll_show_info).startAnimation(alphaAnimation);
        }
    }

    /**
     * Create visualizer fragment when the show is playing
     */
    public void showVisualizeFragment() {
        if (!radioService.isLoading()) {
            //Commiting the visualize fragment
            getSupportFragmentManager().beginTransaction()
                    //Set transition
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_bottom)
                    .replace(R.id.fragment, mVisualizeFragment, VisualizeFragment.TAG)
                    .addToBackStack(null)
                    .commit();
            ((TextView) findViewById(R.id.tv_playback_show_name)).setText("");
            ((TextView) findViewById(R.id.tv_playback_show_time)).setText("");
            mPlaybackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);

            // move the play button to the middle

            shiftAmnt = (mPlaybackToolbar.getWidth() - mPlayPauseButton.getWidth()) / 2;
            TranslateAnimation animation = new TranslateAnimation(shiftAmnt, 0, 0, 0);
            animation.setDuration(200);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setFillAfter(true);
            mPlayPauseButton.setTranslationX(-1 * shiftAmnt);
            mPlayPauseButton.startAnimation(animation);

            // move the info off the screen
            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
            alphaAnimation.setDuration(300);
            alphaAnimation.setStartOffset(100);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setInterpolator(new AccelerateInterpolator());
            findViewById(R.id.ll_show_info).startAnimation(alphaAnimation);
        }
    }

    /**
     * Update the show name in the bottom playback toolbar to the current show
     */
    public void updateShowNamePlaybackToolbar() {
        //Finding the text fields for show names and show time
        TextView showName = (TextView) findViewById(R.id.tv_playback_show_name);
        TextView showTime = (TextView) findViewById(R.id.tv_playback_show_time);
        //Get the current show, if it's null, the radio is on autoplay, else get
        //the name of the show and its schedule
        Show curShow = Schedule.getCurrentShow(this);
        if (curShow == null) {
            showName.setText("Auto Play");
            showTime.setVisibility(View.GONE);
        } else {
            showName.setText(curShow.getTitle());
            showTime.setText("Started at " + curShow.getTime());
            showTime.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Create and show an alert dialog warning about no internet connection
     */
    public void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Connect to the internet to play the live stream.")
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();
    }

    /**
     * Generate the Update Schedule button
     * @param menu
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Generate Update Schedule fragment after clicking on update schedule
     * @param item
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.update_schedule) {
            GetSchedule getSchedule = new GetSchedule(MainActivity.this, mScheduleFragment);
            getSchedule.execute();
            updateSchedule();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Reset and update the schedule fragment
     */
    private void updateSchedule() {
        GetSchedule getSchedule = new GetSchedule(MainActivity.this, mScheduleFragment);
        getSchedule.execute();
    }

    /**
     * Return to previous activity on the stack,
     */
    @Override
    public void onBackPressed() {
        if (mBackStack.size() > 1) {
            int menuId = mBackStack.pop();
            updateNavigationView();
            //If the most recent activity in the back stack is the visualizer,
            //make a call to hide the visualizer
            if (menuId == R.id.visualizer) {
                hideVisualizeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
