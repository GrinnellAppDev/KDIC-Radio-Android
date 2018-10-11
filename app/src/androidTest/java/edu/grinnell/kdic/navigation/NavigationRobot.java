package edu.grinnell.kdic.navigation;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.view.Gravity;

import edu.grinnell.kdic.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class NavigationRobot {

    public NavigationRobot() {
    }

    public NavigationRobot openSideDrawer() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        return this;
    }

    public NavigationRobot closeSideDrawer() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.close());
        return this;
    }

    public NavigationRobot checkIfSideDrawerOpens() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen()));
        return this;
    }

    public NavigationRobot clickFavoritesPage() {
        onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.favorites));
        //Todo: return FavoritesPage robot
        return this;
    }

    public NavigationRobot clickSchedule() {
        onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.schedule));
        //Todo: return SchedulePage robot
        return this;
    }

    public NavigationRobot clickBlogPage() {
        onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.blog));
        //Todo return BlogPage robot
        return this;
    }

    public NavigationRobot clickNowPlayingPage() {
        onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.visualizer));
        //Todo return NowPlayingPage robot
        return this;
    }

    public NavigationRobot clickAboutPage() {
        onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.about));
        //Todo return AboutPage robot
        return this;
    }
}
