package fulton.util.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import fulton.util.android.notations.HelperMethods;
import fulton.util.android.thread.PackedObject;

/**
 * Created by 13774 on 8/7/2017.
 */

public class DialogUtils {

    public static void HowToUseSecurityDialog(Context context)
    {

        showSecurityDialog(context, "请输入安全密码", "2486", null, "对不起，您的密码不正确", 2,new Runnable() {
            @Override
            public void run() {
                Util.logi("haha");
            }
        });
    }

    /**
     *
     * @param context
     * @param message
     * @param desiredPassword
     * @param onPassed
     * @param onFail
     * @param repeatTimes   null meant no limit,<=1 means 1
     * @param runOnPassed
     */
    @HelperMethods
    public static void showSecurityDialog(final Context context, String message, final String desiredPassword, @Nullable final String onPassed, @Nullable final String onFail, @Nullable Integer repeatTimes, final Runnable runOnPassed)
    {
        final PackedObject<Boolean> result=new PackedObject<>(false);
        final PackedObject<Integer> repeat=new PackedObject<>( (repeatTimes!=null && repeatTimes<=1)?1:repeatTimes);
        final EditText editText=new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);

        final AlertDialog dialog=new AlertDialog.Builder(context)
                .setView(editText)
                .setMessage(message).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("确认",null).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.e = editText.getText().toString().equals(desiredPassword);
                if (result.e) {
                    if (onPassed != null) {
                        Toast.makeText(context, onPassed, Toast.LENGTH_SHORT).show();
                    }
                    runOnPassed.run();
                    dialog.dismiss();
                } else {
                    if (onFail != null) {
                        Toast.makeText(context, onFail, Toast.LENGTH_SHORT).show();
                    }
                    if(repeat.e!=null && --repeat.e <= 0)
                        dialog.dismiss();
                }
            }
        });
    }

}
