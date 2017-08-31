package fulton.shaw.android.x.public_interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import fulton.shaw.android.x.activities.TestActivity;
import fulton.util.android.interfaces.typetransfers.TransferLongToSqliteDateTime;
import fulton.util.android.utils.Util;
import fulton.util.android.notations.CoreMethodOfThisClass;
import fulton.util.android.notations.OverrideToCustomBehaviour;

/**
 * Created by 13774 on 7/30/2017.
 */

public class FirstTimeAppRunning {

    @CoreMethodOfThisClass
    public static boolean runIfTheFirstTime(Activity mainActivity)
    {
        //firstly: getByGetter the db backedup
        boolean needBackup=false;
        if(needBackup) {
            try {
                Util.logi("readable?" + isExternalStorageReadable());
                Util.logi("writeable?" + isExternalStorageWritable());
                backupDatabaseForRecoveryOnRandomError(mainActivity);
                Util.logi("backup successful");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot backup database");
            }
        }
        SharedPreferences pref=mainActivity.getSharedPreferences(AppConfig.CONFIG_FILE_APPICATION, Context.MODE_PRIVATE);
        if(pref.getBoolean(AppConfig.CONFIG_KEY_IS_FIRST_TIME_RUNNING,true))
        {
            Util.logi("Run for the first time.");
            SharedPreferences.Editor editor=pref.edit();
            editor.putBoolean(AppConfig.CONFIG_KEY_IS_FIRST_TIME_RUNNING,false);
            editor.commit();
            runForDatabaseCreation(mainActivity);

            Util.logi("Run always run method");
            runAlwaysAtInit(mainActivity);
            return true;
        }else{
            Util.logi("Not the first time");

            Util.logi("Run always run method");
            runAlwaysAtInit(mainActivity);
            return false;
        }
    }
    public static File getDbStorageDir(String dbDir) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), dbDir);
        if (!file.mkdirs()) {
           Util.logi( "Directory not created");
        }
        return file;
    }
    @OverrideToCustomBehaviour
    public static void runForDatabaseCreation(Activity activity)
    {
        SqliteHelper sqliteHelper=new SqliteHelper(activity);
        SQLiteDatabase db=sqliteHelper.getWritableDatabase();
    }

    public static void backupDatabaseForRecoveryOnRandomError(Context context) throws IOException {
        final String inFileName = context.getDatabasePath(SqliteHelper.DBNAME).getAbsolutePath();
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        TransferLongToSqliteDateTime timeTransfer=new TransferLongToSqliteDateTime();
        String outFileName = "db_backup"+timeTransfer.transferPositive(System.currentTimeMillis())+".db";
        OutputStream output = context.openFileOutput(outFileName,Context.MODE_PRIVATE);

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void runAlwaysAtInit(Activity activity)
    {
        SqliteHelper helper=new SqliteHelper(activity);

        ActivityUtil.startAcitivty(activity,TestActivity.class);
//        ActivityUtil.startAcitivty(activity, ViewAsListUsingPages.class);
    }


}
