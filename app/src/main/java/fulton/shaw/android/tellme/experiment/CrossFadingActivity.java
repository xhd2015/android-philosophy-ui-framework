package fulton.shaw.android.tellme.experiment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import fulton.shaw.android.tellme.R;

public class CrossFadingActivity extends AppCompatActivity {
    private ScrollView mView1;
    private  TextView mView2;
    private  int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_fading);

        mView1=(ScrollView)findViewById(R.id.view1);
        mView2=(TextView)findViewById(R.id.view2);

        mView1.setVisibility(View.GONE);
        mShortAnimationDuration=getResources().getInteger(android.R.integer.config_shortAnimTime);


//        final Button startBtn=(Button)findViewById(R.id.startButton);
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                crossfade();
//            }
//        });

    }

    protected  void crossfade(View v)
    {
        mView1.setAlpha(0f);
        mView1.setVisibility(View.VISIBLE);

        mView1.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mView2.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mView2.setVisibility(View.GONE);
                    }
                });
    }
}
