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

import neckbeardhackers.pcqueue.view.RestaurantListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by brandon on 11/28/15.
 */
public class RestaurantListTest extends ActivityInstrumentationTestCase2<RestaurantListActivity> {
    private RestaurantListActivity listActivity;

    public RestaurantListTest() {
        super(RestaurantListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        listActivity = getActivity();
    }

    public void testRestaurantInformationPaneOnPress() {
        String[] restaurantNames = {"Bombay Coast", "Burger King", "D'Lush", "Jamba Juice",
                "Lemongrass", "Panda Express", "Round Table", "Rubios", "Santorini", "Shogun",
                "Starbucks", "Subway", "Tapioca Express"};
        /* Given I am on the restaurant list, when I click on a restaurant */
        for (int i = 0; i < restaurantNames.length; ++i) {
            onView(withId(R.id.RestaurantListRecycler)).perform(RecyclerViewActions.scrollToPosition(i));
            onView(withId(R.id.RestaurantListRecycler))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));

            /* The information pane should display itself with information about this restaurant */
            onView(withId(R.id.info_restaurantName))
                    .check(ViewAssertions.matches((withText(restaurantNames[i]))));

            /* Given I am on the information pane, when I press the back button then I should return to the restaurant list */
            pressBack();
        }

    }

    public void testReportPaneOnButtonClick() {
        String[] restaurantNames = {"Bombay Coast", "Burger King", "D'Lush", "Jamba Juice",
        "Lemongrass", "Panda Express", "Round Table", "Rubios", "Santorini", "Shogun",
        "Starbucks", "Subway", "Tapioca Express"};

        /* Given I am on the restaurant list, when I click on a restaurant's update button */
        for (int i = 0; i < restaurantNames.length; i++) {

            onView(withId(R.id.RestaurantListRecycler)).perform(RecyclerViewActions.scrollToPosition(i));
            onView(withId(R.id.RestaurantListRecycler)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(i,
                            ExtendedViewAction.clickChildViewWithId(R.id.updateButton)));

            /* Then the report pane should display itself with the selected restaurant's name */
            onView(withId(R.id.reporter_restaurantName))
                .check(ViewAssertions.matches((withText(restaurantNames[i]))));

            /* Given I am on the report pane, when I press the back button then I should return to the restaurant list */
            pressBack();
        }
    }
}

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
