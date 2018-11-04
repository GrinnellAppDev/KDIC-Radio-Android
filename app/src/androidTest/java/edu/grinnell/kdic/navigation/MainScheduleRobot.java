package edu.grinnell.kdic.navigation;

import android.support.test.espresso.contrib.RecyclerViewActions;

import org.hamcrest.Matchers;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.R;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainScheduleRobot extends ScheduleRobot {
    public MainScheduleRobot() {
    }

    public MainScheduleRobot checkHeaders() {
        onView(withId(R.id.rv_schedule))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withChild(withText("On Air")))))
                .check(matches(hasDescendant(withChild(withText("Later Today")))))
                .perform(RecyclerViewActions.scrollToPosition(10))                  //FIXME: scroll so that week heading will show up
                .check(matches(hasDescendant(withChild(withText("Full Schedule")))));
        return this;
    }

    public MainScheduleRobot checkDays() {
        onView(withId(R.id.rv_schedule))
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

    public DailyScheduleRobot clickDay(String day) {
        onView(withId(R.id.rv_schedule))
                .perform(RecyclerViewActions.scrollToHolder(Matchers.allOf(withType(ScheduleRecyclerViewAdapter.DAY_SCHEDULE), withTitle(day))))
                .perform(RecyclerViewActions.actionOnHolderItem(Matchers.allOf(withType(ScheduleRecyclerViewAdapter.DAY_SCHEDULE), withTitle(day))
                        , click()));
        return new DailyScheduleRobot();
    }
}
