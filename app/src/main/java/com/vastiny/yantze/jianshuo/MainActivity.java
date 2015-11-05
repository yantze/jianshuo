package com.vastiny.yantze.jianshuo;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private NotesDbAdapter notesDb;

    private EditText content_view;
//    private String title;
//    private String content;

    private long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        content_view = (EditText) findViewById(R.id.content_text);

        notesDb = new NotesDbAdapter(this);
        notesDb.open();

        if (savedInstanceState != null) {
            // get mRowId from saveState
            showMessage("title", "id:" + mRowId);
            mRowId = (long)savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        } else {
            // get mRowId from noteList.class
            Bundle extras = getIntent().getExtras();
            mRowId = extras == null ? 0 : extras.getLong(NotesDbAdapter.KEY_ROWID);
        }

        fillData(mRowId);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                saveState();
                openNotesList();

            }
        });


//        toolbar.findViewById(R.id.action_list).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                showMessage("ddd");
//            }
//        });
//        MenuItem item = (MenuItem) findViewById(R.id.action_list);
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                showMessage("title", "body");
//                return false;
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void fillData(long rowId) {
        Cursor cursor;
        if (rowId == 0) {
            cursor = notesDb.fetchAllNotes();
        } else {
            cursor = notesDb.fetchNote(rowId);
        }

//        startManagingCursor(notesCursor);
//        CursorLoader cur = new CursorLoader(notesCursor);
//        EditText


        if (cursor.moveToLast()) {
//            showMessage(notesCursor.getString(1) + "-" + notesCursor.getString(3), notesCursor.getString(2));
//            String title = notesCursor.getString(1);
            String body = cursor.getString(2);

            content_view.setText(body);
        } else {
            showMessage("Failed", "move to first lines faild");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (saveState()) {
            outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
        } else {
            showMessage("Error!", "不能保存当前笔记");
        }
    }

    private boolean saveState() {

        if (mRowId == 0) {
            long id = notesDb.createNote(noteTitle(), noteContent(), noteDate());
            if (id > 0) {
                mRowId = id;
            } else {
                Log.e("create note fail!", "return id:" + id);
                return false;
            }
        } else {
            boolean status = notesDb.updateNote(mRowId, noteTitle(), noteContent(), noteDate());
            if (!status) {
                Log.e("update note fail!", "return id:" + mRowId);
                return false;
            }
        }

        return true;
    }

    private void openNotesList() {
        Intent listIntent = new Intent(this, NotesList.class);
        startActivityForResult(listIntent, 1);
    }


    /**
     * get title from ui
     * @return String title
     */
    private String noteTitle() {
        String _title;
        String[] lines;
        String _content = content_view.getText().toString();
        lines = _content.split("\n", 2);
        if (lines.length > 0) {
            _title = lines[0];
        } else {
            _title = "";
        }

        // String firstLine = text.substring(0,text.indexOf("\n"));
        // String text=editText1.getText().toString().replace("\n", " ").trim();

        return subString(_title, 0, 13);
    }

    /**
     * get safe start and end
     * @param str String
     * @param start int
     * @param len int want sub how long string
     * @return String substr
     */
    private String subString(String str, int start, int len) {
        long maxLong = str.length();
        if (maxLong > start + len + 1) {
            return str.substring(start, start + len);
        } else {
            return str.substring(start);
        }
    }

    /**
     * get summary from content
     * @return string summary
     */
    private String noteSummary() {
        String _summary;
        String[] lines;
        String _content = content_view.getText().toString();
        lines = _content.split("\n", 2);
        if (lines.length > 1) {
            _summary = lines[1];
        } else {
            _summary = "";
        }

        return subString(_summary, 0, 10);
    }

    /**
     * get content from ui
     * @return String content
     */
    private String noteContent() {

        return content_view.getText().toString();
    }

    /**
     * get date by currentTimeMillis
     * @return String date
     */
    private String noteDate() {
        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
//        String curDate = formatter.format(curDateTime);

//        DateFormat date = DateFormat.getDateInstance();
//        date.format(curDateTime);
        return formatter.format(curDateTime);
    }

    private void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}


