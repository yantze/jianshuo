package com.vastiny.yantze.jianshuo;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by yantze on 11/4/2015.
 */
public class NotesList extends ListActivity {

    NotesDbAdapter notesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        registerForContextMenu(getListView());

        notesDb = new NotesDbAdapter(this);

        /**
         * 这里的 open() 以后需要在NotesDbAdapter检查一下
         * 如果第一次使用没有执行open()， 可以隐式执行
         */
        notesDb.open();

        fillListData();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, 1);
    }

    protected void fillListData() {
        Cursor notesCursor = notesDb.fetchAllNotes();
        startManagingCursor(notesCursor);


        String[] from = new String[] { NotesDbAdapter.KEY_TITLE ,NotesDbAdapter.KEY_DATE};
        int[] to = new int[] { R.id.title_row ,R.id.date_row};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_list_row, notesCursor, from, to);
        setListAdapter(notes);

    }
}
