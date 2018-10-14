package edu.grinnell.kdic.navigation;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.ArrayList;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.R;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.assertion.ViewAssertions.selectedDescendantsMatch;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ScheduleRobot {

    public ScheduleRobot() {
    }
    // check if the recyclerview is there, visible
    public ScheduleRobot checkHeaders() {
        onView(withId(R.id.rv_schedule))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withChild(withText("On Air")))))
                .check(matches(hasDescendant(withChild(withText("Later Today")))))
                .perform(RecyclerViewActions.scrollToPosition(10))                  //FIXME: scroll so that week heading will show up
                .check(matches(hasDescendant(withChild(withText("Full Schedule")))))
                .perform(RecyclerViewActions.scrollToPosition(15))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[0])))))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[1])))))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[2])))))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[3])))))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[4])))))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[5])))))
                .check(matches(hasDescendant(withChild(withText(Constants.DAYS_OF_WEEK[6])))));
        return this;
    }

    public ScheduleRobot favoriteShow(String title) {
        onView(withId(R.id.rv_schedule))
                .perform(RecyclerViewActions.actionOnHolderItem(Matchers.allOf(withType(ScheduleRecyclerViewAdapter.CARD), withTitle(title))
                        , clickFavorite())
                );
        return this;
    }

    public ScheduleRobot unfavoriteShow(String title) {
        onView(withId(R.id.rv_schedule))
                .perform(RecyclerViewActions.actionOnHolderItem(Matchers.allOf(withType(ScheduleRecyclerViewAdapter.CARD), withTitle(title))
                        , clickFavorite())
                );
        return this;
    }

    public ScheduleRobot clickDay(String day) {
        onView(withId(R.id.rv_schedule))
                .perform(RecyclerViewActions.scrollToHolder(Matchers.allOf(withType(ScheduleRecyclerViewAdapter.DAY_SCHEDULE), withTitle(day))))
                .perform(RecyclerViewActions.actionOnHolderItem(Matchers.allOf(withType(ScheduleRecyclerViewAdapter.DAY_SCHEDULE), withTitle(day))
                        , click()));
        return this;
        // TODO: return DailyScheduleRobot
    }

    public static Matcher<RecyclerView.ViewHolder> withType(final int type)
    {
        return new BoundedMatcher<RecyclerView.ViewHolder, ScheduleRecyclerViewAdapter.ViewHolder>(ScheduleRecyclerViewAdapter.ViewHolder.class)
        {
            @Override
            protected boolean matchesSafely(ScheduleRecyclerViewAdapter.ViewHolder item)
            {
                return item.mViewType == type;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("view holder with type " + type);
            }
        };
    }

    public static Matcher<RecyclerView.ViewHolder> withTitle(final String title)
    {
        return new BoundedMatcher<RecyclerView.ViewHolder, ScheduleRecyclerViewAdapter.ViewHolder>(ScheduleRecyclerViewAdapter.ViewHolder.class)
        {
            @Override
            protected boolean matchesSafely(ScheduleRecyclerViewAdapter.ViewHolder item)
            {
                return item.mTitle.getText().toString().equalsIgnoreCase(title);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("view holder with title " + title);
            }
        };
    }

    public static ViewAction clickFavorite() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return Matchers.allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isDisplayed(), withChild(withId(R.id.iv_favorite)));
            }

            @Override
            public String getDescription() {
                return "click favorite button";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.findViewById(R.id.iv_favorite).performClick();
            }
        };
    }
}
