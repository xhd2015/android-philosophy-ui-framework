package fulton.shaw.android.tellme.experiment;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import fulton.shaw.android.tellme.R;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

public class HasActionBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_has_action_bar);
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar bar = this.getSupportActionBar();

        Log.i("==ActionBarMethodTest==",bar.toString());
        Log.i("==ActionBarMethodTest==",toolbar.toString());


       // toolbar.setTitle("Great");
        bar.setTitle("Greate/Start A Service");

        Intent intent=new Intent(this,EchoIntentService.class);
        startService(intent);

        ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
