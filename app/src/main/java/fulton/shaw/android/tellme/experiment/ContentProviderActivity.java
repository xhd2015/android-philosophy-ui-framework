package fulton.shaw.android.tellme.experiment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import fulton.shaw.android.tellme.R;

public class ContentProviderActivity extends AppCompatActivity {

    private EditText mNameView;
    private EditText mGradeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);

        mNameView=(EditText) findViewById(R.id.name);
        mGradeView=(EditText) findViewById(R.id.grade);
    }

    public void onClickInsertButton(View v)
    {
        Uri uri=Uri.parse("content://fulton.shaw.android.tellme.MyContentProvider/students");
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",mNameView.getText().toString());
        contentValues.put("grade",mGradeView.getText().toString());
        getContentResolver().insert(uri,contentValues);
    }
    public void onClickQueryButton(View v)
    {
        Uri uri=Uri.parse("content://fulton.shaw.android.tellme.MyContentProvider/students");
        Cursor c= managedQuery(uri,null,null,null,"name");//corresponding to those arguments in the query
        String queryName="NO DATA AS REQUIRED";
        String queryGrade="NO DATA AS REQUIRED";
        if(c.moveToFirst())
        {
            queryName=c.getString(c.getColumnIndex("name"))+"("+
                    c.getInt(c.getColumnIndex("_id"))+")";
            queryGrade=c.getString(c.getColumnIndex("grade"));
        }
        mNameView.setText(queryName);
        mGradeView.setText(queryGrade);

    }
}
