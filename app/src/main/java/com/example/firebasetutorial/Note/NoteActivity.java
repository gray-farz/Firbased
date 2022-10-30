package com.example.firebasetutorial.Note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebasetutorial.R;
import com.example.firebasetutorial.factory.Datas;
import com.example.firebasetutorial.factory.RecievedFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    NoteAdapter adapter;
    DatabaseReference databaseReference;
    String Uid;
    private List<Note> notes = new ArrayList<>();
    public static final String TAG="aaa";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.d(TAG, "onCreate NoteActivity: ");
        databaseReference = FirebaseDatabase.getInstance().getReference("Note");
        creatProgressDialog();
        RecyclerView recyclerView = initRecyclerANDAdapter();
        itemListClicked();
        whenSwipeItem(recyclerView);
        floatingBtnClicked();
        
        showFirebaseDatabase();
    }

    private void itemListClicked() {
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(NoteActivity.this, AddEditNoteActivity.class);
                Log.d(TAG, "onItemClick: "+note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    private void whenSwipeItem(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                removeItemFirebseDatabse(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                showFirebaseDatabase();

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void floatingBtnClicked() {
        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("aaa", "onClick: ");
                Intent intent = new Intent(NoteActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
    }

    @NonNull
    private RecyclerView initRecyclerANDAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private void removeItemFirebseDatabse(Note noteAt)
    {
        databaseReference.child(noteAt.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(NoteActivity.this, "داده با موفقیت حذف شد", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(NoteActivity.this, "خطایی در خذف داده اتفاق افتاد", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void creatProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            Log.d(TAG, "ADD_NOTE_REQUEST: ");
            handInANDshowDatabase(data,ADD_NOTE_REQUEST);
        }
        else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            Log.d(TAG, "EDIT_NOTE_REQUEST: ");
            String id = data.getStringExtra(AddEditNoteActivity.EXTRA_ID);

            if ( id.equals("-1") ) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            handInANDshowDatabase(data,EDIT_NOTE_REQUEST);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();

        }
        else if(resultCode != RESULT_OK)
        {
            Log.d(TAG, "resultCode != RESULT_OK ");
        }
        else
        {
            Log.d(TAG, "Note not saved ");
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void handInANDshowDatabase(@Nullable Intent data, int action) {
        Log.d(TAG, "handInANDshowDatabase: ");
        String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
        String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
        int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
        String id= data.getStringExtra(AddEditNoteActivity.EXTRA_ID);
        Log.d(TAG, "handInANDshowDatabase: "+id);
        if( action == ADD_NOTE_REQUEST)
            addToFirebaseDatabase(title,description,priority);
        else
            addToFirebaseDatabase(id,title,description,priority);
        showFirebaseDatabase();
    }

    public void showFirebaseDatabase()
    {
        progressDialog.show();
        notes.clear();
        Log.d(TAG, "showFirebaseDatabase: ");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String title=dataSnapshot.child("title").getValue().toString();
                    String desc=dataSnapshot.child("description").getValue().toString();
                    int priority=Integer.valueOf(dataSnapshot.child("priority").getValue().toString()) ;
                    String id=dataSnapshot.child("id").getValue().toString();
                    //Note note=new Note(id,title,desc,priority);
                    Datas note=RecievedFactory.createNote(id,title,desc,priority);
                    notes.add((Note)note);

                }
                Log.d(TAG, "onDataChange: ");
                adapter.setNotes(notes);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.d(TAG, "onCancelled: "+error.getMessage());
                Toast.makeText(NoteActivity.this, "خطایی در نمایش دیتابیس اتفاق افتاد", Toast.LENGTH_SHORT).show();
            }

        });




    }

    private void addToFirebaseDatabase(String title, String description, int priority)
    {
        Log.d(TAG, "addToFirebaseDatabase: ");
        String id= databaseReference.push().getKey();
        //Note note=new Note(id,title,description,priority);
        Datas note=RecievedFactory.createNote(id,title,description,priority);
        databaseReference.child(id).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(NoteActivity.this, "به دیتابیس اضافه شد", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(NoteActivity.this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToFirebaseDatabase(String id,String title, String description, int priority)
    {
        //Note note=new Note(id,title,description,priority);
        Datas note=RecievedFactory.createNote(id,title,description,priority);
        databaseReference.child(id).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(NoteActivity.this, "به دیتابیس اضافه شد", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(NoteActivity.this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}