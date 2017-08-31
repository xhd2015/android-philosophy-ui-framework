package fulton.shaw.android.tellme.experiment;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import fulton.shaw.android.tellme.R;
import fulton.util.android.utils.Util;

public class MessageHandlerActivity extends AppCompatActivity {

    public Handler mMessageHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_handler);

        mMessageHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                MessageHandlerActivity.this.handleMessage(mMessageHandler,msg);
            }
        };
    }
    public void onClickSendMessage(View v)
    {
        Bundle data=new Bundle();
        data.putString("data_key","put data");
        Message msg=new Message();
        msg.setData(data);
        mMessageHandler.sendMessage(msg);

    }
    protected void handleMessage(Handler handler,Message msg)
    {
        Util.logi(msg.getData().getString("data_key","No data"));
    }
}
