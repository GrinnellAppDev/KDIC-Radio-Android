package edu.grinnell.kdic.navigation;

import android.support.test.espresso.contrib.RecyclerViewActions;

import org.hamcrest.Matchers;

import edu.grinnell.kdic.R;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class DailyScheduleRobot extends ScheduleRobot {

    public DailyScheduleRobot() {
    }

    public DailyScheduleRobot checkHeaders() {
        onView(withId(R.id.rv_schedule))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withChild(withText("All Shows for the Day")))));
        onView(withId(R.id.toolbar_main))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withText("DailySchedule"))));
        return this;
    }

    public DailyScheduleRobot checkIsDay(String day) {
        onView(withId(R.id.rv_schedule))
                .check(matches(hasDescendant(withText(day))));
        return this;
    }

    public ScheduleRobot clickBack() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        return new MainScheduleRobot();
    }
}
