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

import edu.grinnell.kdic.R;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public abstract class ScheduleRobot {

    public ScheduleRobot() {
    }

    public abstract ScheduleRobot checkHeaders();


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

    public ScheduleRobot checkFavorited(String title) {
        onView(withId(R.id.rv_schedule))
                .check(matches(hasDescendant(Matchers.allOf(withChild(withText(title)), withChild(withTagValue(Matchers.<Object>equalTo(R.drawable.ic_favorite_white_24dp)))))));
        return this;
    }

    public ScheduleRobot checkUnfavorited(String title) {
        onView(withId(R.id.rv_schedule))
                .check(matches(hasDescendant(Matchers.allOf(withChild(withText(title)), withChild(withTagValue(Matchers.<Object>equalTo(R.drawable.ic_favorite_border_white_24dp)))))));
        return this;
    }

    public static Matcher<RecyclerView.ViewHolder> withType(final int type)
    {
        return new BoundedMatcher<RecyclerView.ViewHolder, ScheduleRecyclerViewAdapter.ViewHolder>(ScheduleRecyclerViewAdapter.ViewHolder.class)
        {
            @Override
            protected boolean matchesSafely(ScheduleRecyclerViewAdapter.ViewHolder item)
            {
                return item.getViewType() == type;
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
                return item.getTitle().getText().toString().equalsIgnoreCase(title);
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
