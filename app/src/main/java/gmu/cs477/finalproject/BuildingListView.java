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

public class BuildingListView extends AppCompatActivity {
    ListView buildingListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> buildingNames=new ArrayList<>();
    ArrayList<Building> buildingList=new ArrayList<>();

    //This activity displays all the buildings it retrieves from the JSON page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list_view);
        //Creating the list view and adapter
        buildingListView=(ListView)findViewById(R.id.listView3);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,buildingNames);
        buildingListView.setAdapter(adapter);
        this.setTitle("List of Buildings");

        //Getting the JSON info, stored on my Mason account
        new HttpGetTask().execute("http://mason.gmu.edu/~csande11/buildings.json");

        buildingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent=new Intent(BuildingListView.this,BuildingInfo.class);
                //Handing off the data
                newIntent.putExtra("buildingname",buildingList.get(position).getName());
                newIntent.putExtra("description",buildingList.get(position).getDescription());
                newIntent.putExtra("latitude",buildingList.get(position).getLatitude());
                newIntent.putExtra("longitude",buildingList.get(position).getLongitude());

                startActivity(newIntent);
            }
        });

    }

    //Loads data into the building list from the JSON object
    private void onFinishGetRequest(String result){
        try{
            JSONArray buildings=(new JSONArray(result));
            for (int i=0;i<buildings.length();i++){
                System.out.println("ONE");
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


    //Retrieves the JSON object from the specified URL
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
        getMenuInflater().inflate(R.menu.menu_building_list_view, menu);
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
