package edu.grinnell.kdic;

import android.content.ComponentName;
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
import android.util.Log;
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

    public static final String TAG = MainActivity.class.getSimpleName();

    Toolbar navigationToolbar;
    Toolbar playbackToolbar;
    ImageView playPauseButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Stack<Integer> backStack;

    VisualizeFragment visualizeFragment;
    ScheduleFragment scheduleFragment;
    FavoritesFragment favoritesFragment;

    // for RadioService
    RadioService radioService;
    boolean boundToRadioService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Because we have bound to an explicit
            // service that is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            RadioService.RadioBinder binder = (RadioService.RadioBinder) service;
            radioService = binder.getService();
            boundToRadioService = true;

            // if the stream is playing, then stop the notification
            if (radioService.isPlaying()) {
                radioService.hideNotification();
                playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "onServiceDisconnected");
            boundToRadioService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout to use
        setContentView(R.layout.activity_main);

        setupNavigation(); // setup the nav drawer and navigation functionality
        setupFragments(savedInstanceState);
        setupPlaybackToolbar(); // setup the playback toolbar

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        // bind to the radio service
        Intent intent = new Intent(this, RadioService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        if (backStack.peek() != R.id.visualizer)
            updateShowNamePlaybackToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if the stream is playing, then stop the notification
        if (boundToRadioService && radioService.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if the stream is playing, then start the notification
        if (boundToRadioService && radioService.isPlaying())
            radioService.showNotification();

        // unbind the radio service
        unbindService(mConnection);

        boundToRadioService = false;

    }

    private void setupPlaybackToolbar() {
        playbackToolbar = (Toolbar) findViewById(R.id.playback_toolbar);
        View.OnClickListener onToggleVisualizeFragment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radioService.isLoading()) {
                    if (backStack.peek() != R.id.visualizer) {
                        showVisualizeFragment();
                        backStack.add(R.id.visualizer);
                    } else {
                        hideVisualizeFragment();
                        backStack.pop();
                    }
                    updateNavigationView();
                }
            }
        };
        playbackToolbar.setNavigationOnClickListener(onToggleVisualizeFragment);
        playbackToolbar.setOnClickListener(onToggleVisualizeFragment);

        playPauseButton = (ImageView) findViewById(R.id.ib_play_pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioService.isPlaying()) {
                    // pause
                    radioService.pause();
                    // switch to play icon
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                } else {
                    // play
                    if (NetworkState.isOnline(MainActivity.this)) {
                        if (!radioService.isLoading()) {
                            if (radioService.isLoaded()) {
                                playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                                playPauseButton.clearAnimation();
                            } else {
                                playPauseButton.setImageResource(R.drawable.ic_loading_spinner);

                                // rotation to use for loading icon
                                RotateAnimation rotate;
                                // different center point for rotation if playPauseButton is in the
                                // center of the screen
                                if (backStack.peek() != R.id.visualizer)
                                    rotate = new RotateAnimation(0, 360,
                                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                            0.5f);
                                else {
                                    float shiftX = playbackToolbar.getWidth() / -2 + playPauseButton.getWidth();
                                    float shiftY = playbackToolbar.getHeight() / 2;
                                    rotate = new RotateAnimation(0, 360, shiftX, shiftY);
                                }
                                rotate.setDuration(1000);
                                rotate.setRepeatCount(Animation.INFINITE);
                                rotate.setInterpolator(new LinearInterpolator());

                                playPauseButton.startAnimation(rotate);
                            }
                            radioService.setRunOnStreamPrepared(new Runnable() {
                                @Override
                                public void run() {
                                    playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                                    playPauseButton.clearAnimation();
                                }
                            });
                            radioService.play();
                        }
                    } else {
                        showNoInternetDialog();
                    }
                }
            }
        });
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
            scheduleFragment = new ScheduleFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            scheduleFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, scheduleFragment, ScheduleFragment.TAG)
                    .commit();
            backStack.add(R.id.schedule);


            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, 0);
            if (sharedPreferences.getBoolean(Constants.FIRST_RUN, true)) {
                GetSchedule getSchedule = new GetSchedule(MainActivity.this, scheduleFragment);
                getSchedule.execute();
                sharedPreferences.edit().putBoolean(Constants.FIRST_RUN, false).apply();
            }

            visualizeFragment = new VisualizeFragment();
        }
    }

    private void setupNavigation() {
        // get toolbar and nav drawer
        navigationToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set toolbar as actionbar
        setSupportActionBar(navigationToolbar);

        // initialize navigation drawer
        navigationToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        navigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // set up backstack
        backStack = new Stack<>();

        // set onclick listeners to navigation menu items
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (backStack.peek() != menuItem.getItemId()) {
                    if (backStack.peek() == R.id.visualizer) {
                        hideVisualizeFragment();
                        backStack.pop();
                    }
                    backStack.push(menuItem.getItemId());

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    switch (menuItem.getItemId()) {
                        case R.id.schedule:
                            ft.replace(R.id.fragment, scheduleFragment, ScheduleFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case R.id.visualizer:
                            showVisualizeFragment();
                            break;
                        case R.id.favorites:
                            ft.replace(R.id.fragment,
                                    favoritesFragment == null ? new FavoritesFragment() : favoritesFragment,
                                    FavoritesFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case R.id.blog:
                            ft.replace(R.id.fragment, new BlogWebViewFragment(), BlogWebViewFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        case R.id.about:
                            ft.replace(R.id.fragment, new AboutFragment(), AboutFragment.TAG)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        default:
                            break;
                    }
                }

                // close the drawer after something is clicked
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void updateNavigationView() {
        navigationView.setCheckedItem(backStack.peek());
    }


    public void hideVisualizeFragment() {
        if (!radioService.isLoading()) {
            // hide visualize fragment
            playbackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
            updateShowNamePlaybackToolbar();
            getSupportFragmentManager().popBackStack();

            // move the play button to the right
            final float shiftAmnt = (playbackToolbar.getWidth() - playPauseButton.getWidth()) / 2;
            TranslateAnimation animation = new TranslateAnimation(0, shiftAmnt, 0, 0);
            animation.setDuration(200);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setFillAfter(false);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    playPauseButton.setTranslationX(0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            playPauseButton.startAnimation(animation);

            // move the info onto the screen
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(200);
            alphaAnimation.setStartOffset(100);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setInterpolator(new AccelerateInterpolator());
            findViewById(R.id.ll_show_info).startAnimation(alphaAnimation);
        }
    }

    public void showVisualizeFragment() {
        if (!radioService.isLoading()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_bottom)
                    .replace(R.id.fragment, visualizeFragment, VisualizeFragment.TAG)
                    .addToBackStack(null)
                    .commit();
            ((TextView) findViewById(R.id.tv_playback_show_name)).setText("");
            ((TextView) findViewById(R.id.tv_playback_show_time)).setText("");
            playbackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);

            // move the play button to the middle
            final float shiftAmnt = (playbackToolbar.getWidth() - playPauseButton.getWidth()) / 2;
            TranslateAnimation animation = new TranslateAnimation(shiftAmnt, 0, 0, 0);
            animation.setDuration(200);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setFillAfter(true);
            playPauseButton.setTranslationX(-1 * shiftAmnt);
            playPauseButton.startAnimation(animation);

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
        TextView showName = (TextView) findViewById(R.id.tv_playback_show_name);
        TextView showTime = (TextView) findViewById(R.id.tv_playback_show_time);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update_schedule) {
            GetSchedule getSchedule = new GetSchedule(MainActivity.this, scheduleFragment);
            getSchedule.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backStack.size() > 1) {
            int menuId = backStack.pop();
            updateNavigationView();
            if (menuId == R.id.visualizer) {
                hideVisualizeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity Destroyed.");

        super.onDestroy();
    }
}
