package gmu.cs477.finalproject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class answerNFC extends AppCompatActivity {
    String question;
    String answer;
    boolean correct;
    NfcAdapter nfcAdapter;
    TextView resultView;
    int score;

    //This function uses NFC to allow the user to answer a question selected
    //In the previous function using an NFC tag placed somewhere on campus.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_nfc);
        this.setTitle("Answer");

        //Setting up the quiz answer
        correct=false;
        Intent incomingIntent=getIntent();
        question=incomingIntent.getStringExtra("question");
        answer=incomingIntent.getStringExtra("answer");
        score=incomingIntent.getIntExtra("score",0);
        TextView questionView=(TextView)findViewById(R.id.questionView);
        questionView.setText(question);
        resultView=(TextView)findViewById(R.id.resultView);

        //Creating the NFC adapter
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause(){
        super.onPause();
        disableForegroundDispatchSystem();
    }

    //The NFC tag comes in as a new Intent, so we use onNewIntent to handle it
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this,"FOUND TAG!", Toast.LENGTH_SHORT).show();
            Parcelable[] parcelables=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables!= null && parcelables.length>0)
                //Calls readText
                readTextFromMessage((NdefMessage)parcelables[0]);
            else
                Toast.makeText(this,"No message found, please try again",Toast.LENGTH_SHORT).show();
        }
    }

    //This function pulls the NDEF record from the NFC tag and checks it against
    //the answer loaded from the previous activity.
    private void readTextFromMessage(NdefMessage ndefMessage){
        //Pulling a list of ndef records from the tag
        NdefRecord[] ndefRecords=ndefMessage.getRecords();
        if (ndefRecords!=null && ndefRecords.length>0){
            //Just gets the first ndef record
            //Since I know whats going to be on the tag I dont
            //Have to worry about looking for it
            NdefRecord ndefRecord=ndefRecords[0];
            //Calling the getTextFromNDEFRecord function to pull data
            String tagContent=getTextFromNdefRecord(ndefRecord);
            //Seeing if the answer is correct
            if (tagContent.contains(answer)){
                correct=true;
                resultView.setText("Correct!!");
            }
            else
                Toast.makeText(this,"Wrong! Sorry!",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"No information found!",Toast.LENGTH_SHORT).show();
        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            //Retrieving NDEF payload
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    //The foreground dispatch system allows the activity to receive the intent
    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, answerNFC.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    //However, you must disable it when the application is paused so default
    //NFC operations can function
    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_nfc, menu);
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

    //Packing up the result
    @Override
    public void onBackPressed() {
        Intent newIntent=new Intent();
        if (correct){
            score++;
            System.out.println(score);
            newIntent.putExtra("score",score);
        }
        else newIntent.putExtra("score",0);
        setResult(Activity.RESULT_OK,newIntent);
        super.onBackPressed();
        finish();
    }
}
