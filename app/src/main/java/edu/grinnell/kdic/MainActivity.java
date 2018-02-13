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
import static android.support.v4.view.GravityCompat.START;
import static edu.grinnell.kdic.Constants.FINAL_ALPHA_LEVEL_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.FINAL_ALPHA_LEVEL_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.FINAL_ANGLE_DEGREES;
import static edu.grinnell.kdic.Constants.FINAL_CHANGE_IN_X_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.FINAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.FINAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.INITIAL_ALPHA_LEVEL_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.INITIAL_ALPHA_LEVEL_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.INITIAL_ANGLE_DEGREES;
import static edu.grinnell.kdic.Constants.INITIAL_CHANGE_IN_X_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.INITIAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.INITIAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.MS_ANIMATION_DURATION_LOADING_SPIN;
import static edu.grinnell.kdic.Constants.MS_ANIMATION_DURATION_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.MS_ANIMATION_DURATION_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.MS_DURATION_FADE_IN_DELAY_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.MS_DURATION_FADE_IN_DELAY_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.MS_DURATION_FADE_IN_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.MS_DURATION_FADE_IN_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT;
import static edu.grinnell.kdic.Constants.PIVOT_X_COORDINATE_MIDDLE;
import static edu.grinnell.kdic.Constants.PIVOT_X_TYPE;
import static edu.grinnell.kdic.Constants.PIVOT_Y_COORDINATE_MIDDLE;
import static edu.grinnell.kdic.Constants.PIVOT_Y_TYPE;
import static edu.grinnell.kdic.NetworkState.isOnline;
public class MainActivity extends AppCompatActivity {
  public static final String TAG = MainActivity.class.getSimpleName();
  private Toolbar mNavigationToolbar;
  private Toolbar mPlaybackToolbar;
  private ImageView mPlayPauseButton;
  private DrawerLayout mDrawerLayout;
  private NavigationView mNavigationView;
  private Stack<Integer> mBackStack;
  private VisualizeFragment mVisualizeFragment;
  private ScheduleFragment mScheduleFragment;
  private FavoritesFragment mFavoritesFragment;
  // for RadioService
  private RadioService mRadioService;
  private boolean mBoundToRadioService;
  private ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      // Because we have bound to an explicit
      // service that is running in our own process, we can
      // cast its IBinder to a concrete class and directly access it.
      RadioService.RadioBinder binder = (RadioService.RadioBinder) service;
      mRadioService = binder.getService();
      mBoundToRadioService = true;
      if (mRadioService.isPlaying()) {
        mRadioService.hideNotification();
        mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
      } else {
        mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
      }
    }
    // Called when the connection with the service disconnects unexpectedly
    public void onServiceDisconnected(ComponentName className) {
      mBoundToRadioService = false;
    }
  };
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupNavigation();
    setupFragments(savedInstanceState);
    setupPlaybackToolbar();
  }
  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, RadioService.class);
    startService(intent);
    bindService(intent, mConnection, BIND_AUTO_CREATE);
    if (mBackStack.peek() != R.id.visualizer)
      updateShowNamePlaybackToolbar();
  }
  @Override
  protected void onResume() {
    super.onResume();
    // if the stream is playing, then stop the notification
    if (mBoundToRadioService && mRadioService.isPlaying()) {
      mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
    } else {
      mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
    }
  }
  @Override
  protected void onStop() {
    super.onStop();
    // if the stream is playing, then start the notification
    if (mBoundToRadioService && mRadioService.isPlaying())
      mRadioService.showNotification();
    // unbind the radio service
    unbindService(mConnection);
    mBoundToRadioService = false;
  }
  private void setupPlaybackToolbar() {
    mPlaybackToolbar = (Toolbar) findViewById(R.id.playback_toolbar);
    View.OnClickListener onToggleVisualizeFragment = toggleVisualizeFragmentClickListener();
    mPlaybackToolbar.setNavigationOnClickListener(onToggleVisualizeFragment);
    mPlaybackToolbar.setOnClickListener(onToggleVisualizeFragment);
    mPlayPauseButton = (ImageView) findViewById(R.id.ib_play_pause);
    mPlayPauseButton.setOnClickListener(togglePlayPauseClickListener());
  }

  private View.OnClickListener togglePlayPauseClickListener() {
    mPlayPauseButton = (ImageView) findViewById(R.id.ib_play_pause);
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mRadioService.isPlaying()) {
          mRadioService.pause();
          mPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
          if (isOnline(MainActivity.this)) {
            if (!mRadioService.isLoading()) {
              if (mRadioService.isLoaded()) {
                mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                mPlayPauseButton.clearAnimation();
              } else {
                loadingSpinAnimation();
              }
              mRadioService.setRunOnStreamPrepared(new Runnable() {
                @Override
                public void run() {
                  mPlayPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
                  mPlayPauseButton.clearAnimation();
                }
              });
              mRadioService.play();
            }
          } else {
            showNoInternetDialog();
          }
        }
      }
    };
  }

  private void loadingSpinAnimation() {
    mPlayPauseButton.setImageResource(R.drawable.ic_loading_spinner);
    RotateAnimation rotate;
    if (mBackStack.peek() != R.id.visualizer) {
      rotate = new RotateAnimation(INITIAL_ANGLE_DEGREES, FINAL_ANGLE_DEGREES, PIVOT_X_TYPE, PIVOT_X_COORDINATE_MIDDLE, PIVOT_Y_TYPE, PIVOT_Y_COORDINATE_MIDDLE);
    }
    else {
        final float pivotXCoordinate = mPlaybackToolbar.getWidth() / -2 + mPlayPauseButton.getWidth();
        final float pivotYCoordinate = mPlaybackToolbar.getHeight() / 2;
        rotate = new RotateAnimation(INITIAL_ANGLE_DEGREES, FINAL_ANGLE_DEGREES, pivotXCoordinate, pivotYCoordinate);
    }
    rotate.setDuration(MS_ANIMATION_DURATION_LOADING_SPIN);
    rotate.setRepeatCount(Animation.INFINITE);
    rotate.setInterpolator(new LinearInterpolator());
    mPlayPauseButton.startAnimation(rotate);
  }

  private View.OnClickListener toggleVisualizeFragmentClickListener() {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!mRadioService.isLoading()) {
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
        mDrawerLayout.openDrawer(START);
      }
    });
    mBackStack = new Stack<>();
    mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
    mNavigationView.setNavigationItemSelectedListener(setOnClickListenerToNavMenuItems());
  }

  private NavigationView.OnNavigationItemSelectedListener setOnClickListenerToNavMenuItems() {
    return new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (mBackStack.peek() != menuItem.getItemId()) {
          if (mBackStack.peek() == R.id.visualizer) {
            hideVisualizeFragment();
            mBackStack.pop();
          }
          mBackStack.push(menuItem.getItemId());
          FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          switch (menuItem.getItemId()) {
            case R.id.schedule:
              ft.replace(R.id.fragment, mScheduleFragment, ScheduleFragment.TAG)
                      .addToBackStack(null)
                      .commit();
              break;
            case R.id.visualizer:
              showVisualizeFragment();
              break;
            case R.id.favorites:
              ft.replace(R.id.fragment,
                      mFavoritesFragment == null ? new FavoritesFragment() : mFavoritesFragment, FavoritesFragment.TAG)
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
        mDrawerLayout.closeDrawer(START);
        return true;
      }
    };
  }
  public void updateNavigationView() {
    mNavigationView.setCheckedItem(mBackStack.peek());
  }
  public void hideVisualizeFragment() {
    if (!mRadioService.isLoading()) {
      mPlaybackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
      updateShowNamePlaybackToolbar();
      getSupportFragmentManager().popBackStack();
      animationPlayPauseButtonHideVisualizeFragment();
      animationShowInfoHideVisualizeFragment();
    }
  }
  private void animationShowInfoHideVisualizeFragment() {
    // move the info onto the screen
    AlphaAnimation alphaAnimation = new AlphaAnimation(INITIAL_ALPHA_LEVEL_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT,
            FINAL_ALPHA_LEVEL_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT);
    alphaAnimation.setDuration(MS_DURATION_FADE_IN_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT);
    alphaAnimation.setStartOffset(MS_DURATION_FADE_IN_DELAY_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT);
    alphaAnimation.setFillAfter(true);
    alphaAnimation.setInterpolator(new AccelerateInterpolator());
    findViewById(R.id.ll_show_info).startAnimation(alphaAnimation);
  }
  private void animationPlayPauseButtonHideVisualizeFragment() {
    mPlaybackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
    final float finalChangeInXCoordinatePlayPauseButton = (mPlaybackToolbar.getWidth() - mPlayPauseButton.getWidth()) / 2;
    TranslateAnimation animation = new TranslateAnimation(INITIAL_CHANGE_IN_X_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT,
            finalChangeInXCoordinatePlayPauseButton,
            INITIAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT,
            FINAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT);
    animation.setDuration(MS_ANIMATION_DURATION_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT);
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
  }
  public void showVisualizeFragment() {
    if (!mRadioService.isLoading()) {
      getSupportFragmentManager().beginTransaction()
              .setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_bottom)
              .replace(R.id.fragment, mVisualizeFragment, VisualizeFragment.TAG)
              .addToBackStack(null)
              .commit();
      ((TextView) findViewById(R.id.tv_playback_show_name)).setText("");
      ((TextView) findViewById(R.id.tv_playback_show_time)).setText("");
      animationPlayPauseButtonShowVisualizeFragment();
      animationShowInfoShowVisualizeFragment();
    }
  }
  private void animationShowInfoShowVisualizeFragment() {
    // move the info off the screen
    AlphaAnimation alphaAnimation = new AlphaAnimation(INITIAL_ALPHA_LEVEL_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT,
            FINAL_ALPHA_LEVEL_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT);
    alphaAnimation.setDuration(MS_DURATION_FADE_IN_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT);
    alphaAnimation.setStartOffset(MS_DURATION_FADE_IN_DELAY_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT);
    alphaAnimation.setFillAfter(true);
    alphaAnimation.setInterpolator(new AccelerateInterpolator());
    findViewById(R.id.ll_show_info).startAnimation(alphaAnimation);
  }
  private void animationPlayPauseButtonShowVisualizeFragment() {
    mPlaybackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
    final float initialChangeInXCoordinatePlayPauseButton= (mPlaybackToolbar.getWidth() - mPlayPauseButton.getWidth()) / 2;
    TranslateAnimation animation = new TranslateAnimation(initialChangeInXCoordinatePlayPauseButton,
            FINAL_CHANGE_IN_X_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT,
            INITIAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT,
            FINAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT);
    animation.setDuration(MS_ANIMATION_DURATION_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT);
    animation.setInterpolator(new AccelerateDecelerateInterpolator());
    animation.setFillAfter(true);
    mPlayPauseButton.setTranslationX(-1 * initialChangeInXCoordinatePlayPauseButton);
    mPlayPauseButton.startAnimation(animation);
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
      GetSchedule getSchedule = new GetSchedule(MainActivity.this, mScheduleFragment);
      getSchedule.execute();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  @Override
  public void onBackPressed() {
    if (mBackStack.size() > 1) {
      int menuId = mBackStack.pop();
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
    mNavigationToolbar = null;
    mPlaybackToolbar = null;
    mPlayPauseButton = null;
    mDrawerLayout = null;
    mNavigationView = null;
    mBackStack = null;
    mVisualizeFragment = null;
    mScheduleFragment = null;
    mFavoritesFragment = null;
    mRadioService = null;
    mBoundToRadioService = false;
    super.onDestroy();
  }

}
