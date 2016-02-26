package gmu.cs477.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BuildingInfo extends AppCompatActivity {
    String buildingName;
    String description;
    String latitude;
    String longitude;

    //This activity just displays the information from the building object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);

        //Retrieving the data from previous activity
        final Intent incomingIntent=getIntent();
        buildingName=incomingIntent.getStringExtra("buildingname");
        description=incomingIntent.getStringExtra("description");
        latitude=incomingIntent.getStringExtra("latitude");
        longitude=incomingIntent.getStringExtra("longitude");

        //Setting title, and making the text view
        this.setTitle(buildingName);
        TextView descView=(TextView)findViewById(R.id.DescriptionView);
        descView.setText(description);

        //Setting up the button and the listener for the map it button
        Button mapButton=(Button)findViewById(R.id.MapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starts google maps with the latitude and longitude
                Uri location=Uri.parse("geo:0,0?q="+latitude+","+longitude+"&z=3");
                Intent mapIntent=new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(location);
                if (incomingIntent.resolveActivity(getPackageManager())!=null)
                    startActivity(mapIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_building_info, menu);
        return true;
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
