package fulton.shaw.android.x.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.views.TextSwitchableTextView;
import fulton.util.android.utils.ViewUtil;

public class AddGeneralRecordBaseActivity extends AppCompatActivity{

    private FrameLayout mCustomContentContainer;
    private FrameLayout mCustomAdditionalContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_general_record_base);

        mCustomContentContainer= (FrameLayout) findViewById(R.id.customContentContainer);
        mCustomAdditionalContainer=(FrameLayout)findViewById(R.id.customAdditionalContainer);

        TextSwitchableTextView switcher= (TextSwitchableTextView) findViewById(R.id.switchTextView);
        switcher.setOnSwitchListener(new TextSwitchableTextView.OnSwitchTitleListener() {
            @Override
            public void onSwitchTitle(TextSwitchableTextView view) {
                ViewUtil.switchShown(mCustomAdditionalContainer);
            }
        });
        ViewUtil.setShown(mCustomAdditionalContainer,false);
    }

}