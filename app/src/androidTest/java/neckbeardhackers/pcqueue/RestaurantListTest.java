package neckbeardhackers.pcqueue;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import neckbeardhackers.pcqueue.controllers.RestaurantListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by brandon on 11/28/15.
 */
public class RestaurantListTest extends ActivityInstrumentationTestCase2<RestaurantListActivity> {
    private RestaurantListActivity listActivity;
    private Dictionary<String, Integer> openRestaurants;
    private Dictionary<String, Integer> closedRestaurants;

    public RestaurantListTest() {
        super(RestaurantListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        openRestaurants = fillOpenRestaurantDict(openRestaurants);
        closedRestaurants = fillClosedRestaurantDict(closedRestaurants);
        listActivity = getActivity();
    }

    private Dictionary<String, Integer> fillOpenRestaurantDict(Dictionary<String, Integer> dict) {
        if (dict == null)
            dict = new Hashtable<String, Integer>();

        dict.put("Burger King", 1);
        dict.put("D'Lush", 2);
        dict.put("Jamba Juice", 3);
        dict.put("Lemongrass", 4);
        dict.put("Panda Express", 5);
        dict.put("Rubios", 7);
        dict.put("Santorini", 8);
        dict.put("Starbucks", 10);
        dict.put("Subway", 11);
        dict.put("Tapioca Express", 12);

        return dict;
    }

    private Dictionary<String, Integer> fillClosedRestaurantDict(Dictionary<String, Integer> dict) {
        if (dict == null)
            dict = new Hashtable<String, Integer>();

        dict.put("Bombay Coast", 0);
        dict.put("Round Table", 6);
        dict.put("Shogun", 9);

        return dict;
    }

    public void testOpenRestaurantInformationPaneOnPress() {
        /* Given I am on the restaurant list, when I click on a restaurant */
        Enumeration<String> openRestaurantNames = openRestaurants.keys();
        while (openRestaurantNames.hasMoreElements()) {
            String name = openRestaurantNames.nextElement();
            testInformationPaneIsAccessible(openRestaurants.get(name), name);
        }


    }

    public void testClosedRestaurantInformationPaneOnPress() {
        Enumeration<String> closedRestaurantNames = closedRestaurants.keys();
        while (closedRestaurantNames.hasMoreElements()) {
            String name = closedRestaurantNames.nextElement();
            testInformationPaneIsAccessible(closedRestaurants.get(name), name);
        }
    }

    public void testOpenRestaurantReporterViewReportButtonOnPress() {
        Enumeration<String> openRestaurantNames = openRestaurants.keys();
        while (openRestaurantNames.hasMoreElements()) {
            String restaurantName = openRestaurantNames.nextElement();

            int restaurantIndex = openRestaurants.get(restaurantName);

            /* Given a restaurant card*/
            onView(withId(R.id.RestaurantListRecycler)).
                    perform(RecyclerViewActions.scrollToPosition(restaurantIndex));

            /* When I click on its report wait time button */
            onView(withId(R.id.RestaurantListRecycler)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(restaurantIndex,
                            ExtendedViewAction.clickChildViewWithId(R.id.updateButton)));

        /* Then the reporter view for this restaurant should display itself */
            onView(withId(R.id.reporter_restaurantName))
                    .check(ViewAssertions.matches((withText(restaurantName))));

            /* Given I am on the reporter view, when I press the report button*/
            onView(withId(R.id.reporter_updateButton)).perform(click());

            /* Then the reporter view should close */
            onView(withId(R.id.ReporterActivity)).check(ViewAssertions.doesNotExist());
        }
    }




    private void testInformationPaneIsAccessible(int restaurantIndex, String restaurantName) {
        onView(withId(R.id.RestaurantListRecycler)).perform(RecyclerViewActions.scrollToPosition(restaurantIndex));
        onView(withId(R.id.RestaurantListRecycler))
            .perform(RecyclerViewActions.actionOnItemAtPosition(restaurantIndex, click()));

        /* The information pane should display itself with information about this restaurant */
        onView(withId(R.id.info_restaurantName))
                .check(ViewAssertions.matches((withText(restaurantName))));

        /* Given I am on the information pane, when I press the back button then I should return to the restaurant list */
        pressBack();
    }

    private void testReportPaneIsAccessible(int restaurantIndex, String restaurantName) {
        onView(withId(R.id.RestaurantListRecycler)).perform(RecyclerViewActions.scrollToPosition(restaurantIndex));
        onView(withId(R.id.RestaurantListRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(restaurantIndex,
                        ExtendedViewAction.clickChildViewWithId(R.id.updateButton)));

        /* Then the report pane should display itself with the selected restaurant's name */
        onView(withId(R.id.reporter_restaurantName))
            .check(ViewAssertions.matches((withText(restaurantName))));

        /* Given I am on the report pane, when I press the back button then I should return to the restaurant list */
        pressBack();
    }

    private void testReportPaneIsNotAccessible(int restaurantIndex, String restaurantName) {
        /* Given this is a closed restaurant, when I click the update button */
        onView(withId(R.id.RestaurantListRecycler)).perform(RecyclerViewActions.scrollToPosition(restaurantIndex));
        onView(withId(R.id.RestaurantListRecycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(restaurantIndex,
                        ExtendedViewAction.clickChildViewWithId(R.id.updateButton)));


        /* Then nothing should happen */
        onView(withId(R.id.ReporterActivity)).check(ViewAssertions.doesNotExist());
    }

    public void testOpenReportPaneOnButtonClick() {

        /* Given I am on the restaurant list, when I click on a restaurant's update button */
        Enumeration<String> openRestaurantNames = openRestaurants.keys();
        while (openRestaurantNames.hasMoreElements()) {
            String name = openRestaurantNames.nextElement();
            testReportPaneIsAccessible(openRestaurants.get(name), name);
        }
    }
}

// Adapted from: http://stackoverflow.com/questions/28476507/
class ExtendedViewAction {
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified ID";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null)
                    v.performClick();
            }
        };
    }
}