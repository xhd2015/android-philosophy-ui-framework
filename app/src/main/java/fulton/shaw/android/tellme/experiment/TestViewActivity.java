package fulton.shaw.android.tellme.experiment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.experiment.viewhelpers.NumberSelectViewHelper;

public class TestViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);

        test1();
    }

    public void test1()
    {
        final View testView = findViewById(R.id.test_view);
        NumberSelectViewHelper.HowToUse(this,testView);
    }

}
