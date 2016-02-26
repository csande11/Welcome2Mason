package gmu.cs477.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DiningInfoPage extends AppCompatActivity {
    ListView buildingListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> buildingNames=new ArrayList<>();
    ArrayList<Building> buildingList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_info_page);
        this.setTitle("Dining Information"); //Setting title

        //Creating the list view
        buildingListView=(ListView)findViewById(R.id.diningView);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, buildingNames);
        buildingListView.setAdapter(adapter);

        //Setting the general description
        TextView diningDescription=(TextView)findViewById(R.id.diningSum);
        diningDescription.setText("Mason offers many dining choices across campus. " +
                "There are many dining halls that offer all you care to eat options, as well" +
                "as chain facilities such as Burger King and Chick-Fil-A.");

        new HttpGetTask().execute("http://mason.gmu.edu/~csande11/dining.json");
        //NOTE: All the dining information is stored at the above link in a json page

        //Listener for the list view
        //Packs the building name, description, lat and long into the intent
        buildingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(DiningInfoPage.this, BuildingInfo.class);
                newIntent.putExtra("buildingname", buildingList.get(position).getName());
                newIntent.putExtra("description", buildingList.get(position).getDescription());
                newIntent.putExtra("latitude", buildingList.get(position).getLatitude());
                newIntent.putExtra("longitude", buildingList.get(position).getLongitude());

                startActivity(newIntent);
            }
        });
    }

    //This function builds the list view using the JSON object that was just pulled
    //From the website
    private void onFinishGetRequest(String result){
        try{
            JSONArray buildings=(new JSONArray(result));
            for (int i=0;i<buildings.length();i++){
                JSONObject building=buildings.getJSONObject(i);
                //Unpacking the JSON
                String name=building.getString("buildingname");
                String description=building.getString("description");
                JSONObject location=building.getJSONObject("location");
                String latitude=location.getString("latitude");
                String longitude=location.getString("longitude");

                buildingNames.add(name); //Creating a list of names
                //Creating a list of buildings
                buildingList.add(new Building(name,description,latitude,longitude));
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //This class is responsible for the retrieval of data from the json page
    private class HttpGetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer data = new StringBuffer();
            BufferedReader br = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) new
                        URL(params[0]).openConnection();
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String rawData;
                while ((rawData = br.readLine()) != null) {
                    data.append(rawData);
                }
            } catch (MalformedURLException e1) {e1.printStackTrace();
            } catch (IOException e1) {e1.printStackTrace();
            } finally {
                if (br != null)
                    try {  br.close();
                    } catch (IOException e) {e.printStackTrace();}
            }
            return data.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            onFinishGetRequest(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dining_info_page, menu);
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
