package me.san.viewmodel

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.san.viewmodel.database.NoteProvider.Companion.URI_NOTES
import me.san.viewmodel.database.NotesDatabaseHelper.Companion.TITLE_NOTES

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var noteRecyclerView: RecyclerView
    lateinit var noteAdd: FloatingActionButton

    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd = findViewById(R.id.add_notes)
        noteAdd.setOnClickListener {
            NotesDetailFragment().show(supportFragmentManager, "dialog")
        }

        adapter = NotesAdapter(object: NoteClickedListener{
            override fun noteClickedItem(cursor: Cursor) {
                val id = cursor.getLong(cursor.getColumnIndex(_ID))
                val fragment = NotesDetailFragment.newInstace(id)
                fragment.show(supportFragmentManager, "dialog")
                Toast.makeText(baseContext, "variavel: $id", Toast.LENGTH_SHORT).show()
            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id = cursor?.getLong(cursor.getColumnIndex(_ID))
                contentResolver.delete(Uri.withAppendedPath(URI_NOTES, id.toString()), null, null)
            }
        })
        adapter.setHasStableIds(true)


        noteRecyclerView = findViewById(R.id.notes_recycler)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = adapter

        LoaderManager.getInstance(this).initLoader(0, null, this)

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
            CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null ) { adapter.setCursor(data) }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.setCursor(null)
    }


}