package fulton.shaw.android.x.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.shaw.android.x.public_interfaces.AppConfig;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/3/2017.
 */

/**
 *  when adding, first find if there has such name.If not,add it with description.
 *  If there already has such name,if description is not "",then ask the user if to override it.
 *
 *  multi_column_custom_content
 *
 *  process_type_multi_column_with_group. find its father,if it has already been processed,then ignore.
 */
public class AddThingsPriceActivity extends AddForDatabaseActivity {
    protected boolean mReplaceIfNotEmpty;
    @Override
    protected Object[][] getDatabaseViewAdapterUnderlyingData() {
        //each condition, you selecct a thing from tableThing,you can also create new thing.
        //that is a spinner from things table.
        //but currently we just
        //actually, the same name meaning different things is hard to understand,so that things name is also unique
        return new Object[][]{
                {SqliteHelper.TABLE_THING,SqlUtil.COL_NAME,null,"物品名称",null,null,null,null},
                {SqliteHelper.TABLE_THING,SqlUtil.COL_DESCRIPTION,null,"物品描述(可选)",null,null,null,null},
                {SqliteHelper.TABLE_CONDITIONAL_PRICE,SqlUtil.COL_CONDITION,null,"情况",null,null,null,null},
                {SqliteHelper.TABLE_CONDITIONAL_PRICE,SqlUtil.COL_PRICE,null,"价格",null,null,null,null},
         };
    }

    @Override
    public void onClickPositive(View v) {
        final HashMap<String, ArrayList<ContentValues>> data = mAdapter.collectAsContentValues();
        final ContentValues thingContentValue=data.get(SqliteHelper.TABLE_THING).get(0);
        int thingId=-1;
        final Cursor thingCursor=mDb.query(SqliteHelper.TABLE_THING,null,SqlUtil.COL_NAME+"=?",new String[]{thingContentValue.getAsString(SqlUtil.COL_NAME)},null,null,null);
        if(thingCursor.getCount()==0)
        {
            long id=mDb.insert(SqliteHelper.TABLE_THING,null,thingContentValue);
            if(id!=-1)
            {
                thingId= (int) id;
            }
        }else {
            thingCursor.moveToFirst();
            thingId = thingCursor.getInt(thingCursor.getColumnIndex(SqlUtil.COL_ID));
            final int tempId = thingId;

            if (mReplaceIfNotEmpty) {
                if(thingContentValue.getAsString(SqlUtil.COL_DESCRIPTION).length()>0) {
                    ContentValues update = new ContentValues();
                    update.put(SqlUtil.COL_DESCRIPTION, thingContentValue.getAsString(SqlUtil.COL_DESCRIPTION));
                    mDb.update(SqliteHelper.TABLE_THING, update, SqlUtil.COL_ID + "=?", new String[]{String.valueOf(tempId)});
                }

            } else {
                new AlertDialog.Builder(this).setMessage("已经存在名为" + thingContentValue.getAsString(SqlUtil.COL_NAME) + "的物品，是否替换描述（否则保留原来的描述）？")
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues update = new ContentValues();
                        update.put(SqlUtil.COL_DESCRIPTION, thingContentValue.getAsString(SqlUtil.COL_DESCRIPTION));
                        mDb.update(SqliteHelper.TABLE_THING, update, SqlUtil.COL_ID + "=?", new String[]{String.valueOf(tempId)});

                    }
                }).show();
            }
            mLastTable=SqliteHelper.TABLE_THING;
            setResult(RESULT_OK);
        }
        for(ContentValues priceValue:data.get(SqliteHelper.TABLE_CONDITIONAL_PRICE))
        {
            priceValue.put(SqlUtil.COL_INNER_ID,thingId);
            mDb.insert(SqliteHelper.TABLE_CONDITIONAL_PRICE,null,priceValue);
        }
        if(mExitAfterAdd)
        {
            if(mViewDetailAfterExit && mLastTable!=null)
            {
                Intent intent=new Intent(this,SeePriceActivity.class);
                startActivity(intent);
            }
            this.finish();
        }
    }

    @Override
    protected void doAdditionalSettingsAfterInit() {
        mReplaceIfNotEmpty = mActivityPref.getBoolean(AppConfig.CONFIG_KEY_REPLACE_IF_NOT_EMPTY,false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item=menu.add(Menu.NONE,1,Menu.NONE,"不为空时更新描述");
        item.setTitleCondensed("当已经存在物品名时，如果描述不为空则替换");
        item.setCheckable(true);
        item.setChecked(mReplaceIfNotEmpty);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1)
        {
            mReplaceIfNotEmpty=!mReplaceIfNotEmpty;
            item.setChecked(mReplaceIfNotEmpty);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mActivityPrefEditor.putBoolean(AppConfig.CONFIG_KEY_REPLACE_IF_NOT_EMPTY,mReplaceIfNotEmpty);
        super.onDestroy();
    }
}
