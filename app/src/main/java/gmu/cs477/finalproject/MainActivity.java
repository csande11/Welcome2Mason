package gmu.cs477.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ArrayList<String> optionsList;
    Spinner actionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating all the main page objects
        actionSpinner=(Spinner)findViewById(R.id.ActionChooser);
        optionsList=new ArrayList<>();
        optionsList.add("Menu");
        optionsList.add("Building Info");
        optionsList.add("Dining Info");
        optionsList.add("Housing Info");

        //Creating the spinner
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,optionsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(adapter);
        //Resetting the spinner choice to 0
        actionSpinner.setSelection(0);

        //Creating a listener for the spinner selection
        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = null;
                switch (position) {
                    case 1:
                        newIntent = new Intent(MainActivity.this, BuildingListView.class);
                        break;
                    case 2:
                        newIntent = new Intent(MainActivity.this, DiningInfoPage.class);
                        break;
                    case 3:
                        newIntent = new Intent(MainActivity.this, HousingInfo.class);
                        break;
                }
                if (position != 0) startActivity(newIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        //Button for the scavenger hunt
        Button scavengerHuntStart=(Button)findViewById(R.id.scavButton);
        scavengerHuntStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent=new Intent(MainActivity.this,ScavengerHunt.class);
                startActivity(newIntent);
            }
        });

        //Button for the sharing
        Button shareButton=(Button)findViewById(R.id.ShareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting up a generic send intent
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //Creating text that will be included in the message
                String shareBody="Visiting Mason! @GMU_Ambassadors #GoMason";
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                //Creating the chooser for the activity where to share
                startActivity(Intent.createChooser(sharingIntent,"Share via"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume(){
        actionSpinner.setSelection(0);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
