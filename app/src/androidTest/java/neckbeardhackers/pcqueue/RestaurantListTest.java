package neckbeardhackers.pcqueue;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v7.widget.CardView;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

import neckbeardhackers.pcqueue.view.RestaurantListActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
        /* NOTE: This test assumes that the first restaurant will always be 'Bombay Coast' */
        /* Given I am on the restaurant list, when I click on the first restaurant */
        onView(withId(R.id.RestaurantListRecycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        /* The informtaion pane should display itself with information about this restaurant */
        onView(withId(R.id.info_restaurantName))
                .check(ViewAssertions.matches((withText("Bombay Coast"))));

    }
}
