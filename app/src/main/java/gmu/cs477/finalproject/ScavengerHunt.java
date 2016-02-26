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

public class ScavengerHunt extends AppCompatActivity {
    int score;
    int numQuestions;
    TextView scoreView;
    ListView questionListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> questions=new ArrayList<>();
    ArrayList<question> questionList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_hunt);
        //Creates the list view for the questions
        questionListView=(ListView)findViewById(R.id.questionListView);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,questions);
        questionListView.setAdapter(adapter);

        this.setTitle("NFC Scavenger Hunt");
        new HttpGetTask().execute("http://mason.gmu.edu/~csande11/questions.json");
        scoreView=(TextView)findViewById(R.id.scoreView);

        //Creates the activity for the NFC answering activity
        questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (questionList.get(position).isAnswered()){
                    Toast.makeText(getApplicationContext(),
                            "You already answered that one!",Toast.LENGTH_SHORT).show();
                    return;
                }
                questionList.get(position).setAnswered();
                Intent newIntent=new Intent(ScavengerHunt.this,answerNFC.class);
                newIntent.putExtra("score",score);
                newIntent.putExtra("question",questionList.get(position).getQuestiontext());
                newIntent.putExtra("answer",questionList.get(position).getNfcanswer());
                startActivityForResult(newIntent,0);
            }
        });
    }


    //Loads the questions into the question list
    private void onFinishGetRequest(String result){
        try{
            JSONArray buildings=(new JSONArray(result));
            for (int i=0;i<buildings.length();i++){
                JSONObject building=buildings.getJSONObject(i);
                //Unpacking the JSON
                String question=building.getString("question");
                String nfcanswer=building.getString("answer");

                questions.add(question); //Creating a list of names
                //Creating a list of buildings
                questionList.add(new question(question, nfcanswer));
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
        numQuestions=questionList.size();
        scoreView.setText("Your score is "+score+" out of "+numQuestions+".");
    }

    //Getting the questions from the JSON object
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
        getMenuInflater().inflate(R.menu.menu_scavenger_hunt, menu);
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

    //Retrieving the data from the answered question
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        score=data.getIntExtra("score",0);
        System.out.println(score);
        scoreView.setText("Your score is "+score+" out of "+numQuestions+".");
    }
    }
