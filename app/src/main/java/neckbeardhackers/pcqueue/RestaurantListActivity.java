package neckbeardhackers.pcqueue;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

<<<<<<< HEAD:app/src/main/java/neckbeardhackers/pcqueue/RestaurantListActivity.java
public class RestaurantListActivity extends AppCompatActivity {
=======

/*
 * Notes:
 *  R = a class containing the definitions for all resources of a particular
 *  application package. It is in the namespace of the application package. As in
 *  it is able to search through our app folder and reference things in any subdirectory
 *  and file
 *  */


/*
 * Class name: RestaurantList
 * Description:
 * Functions:*/
public class RestaurantList extends AppCompatActivity {
>>>>>>> Created card with name and update button. Changed the restaurant function a little bit so that the title bar will not show up by default. This allows our toolbar to be customizable:app/src/main/java/neckbeardhackers/pcqueue/RestaurantList.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Parent class calling onCreate
        super.onCreate(savedInstanceState);

        // This sets the focus on this class on a particular XML file
        setContentView(R.layout.activity_restaurant_list);

        // This will locate the toolbar in activity_restaurant_list and
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //Testing to see if it will close the default action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView txt = (TextView) findViewById(R.id.mainToolbar_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        txt.setTypeface(font);




        /* Load the restaurant list data */
        RestaurantInfoAdapter infoGetter = new RestaurantInfoAdapter(this);
        CardView restaurantListComponent = (CardView) this.findViewById(R.id.restaurantListView);
        restaurantListComponent.setAdapter(infoGetter);
    }

}
