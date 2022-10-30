package com.example.firebasetutorial.Note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.firebasetutorial.R;

public class AddEditNoteActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_ID =
            "com.elecomco.architectureexample.EXTRA_ID";

    public static final String EXTRA_TITLE =
            "com.elecomco.architectureexample.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.elecomco.architectureexample.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.elecomco.architectureexample.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    Button btnSave;

    public static final String TAG="aaa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        btnSave = findViewById(R.id.btnSave);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        btnSave.setOnClickListener(this);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            Log.d(TAG, "intent.hasExtra(EXTRA_ID) "+getIntent().getStringExtra(EXTRA_ID));
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Note");
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        String id = getIntent().getStringExtra(EXTRA_ID);
        Log.d(TAG, "saveNote: with this id "+id+" "+title+" "+description);

        if(id != null)
        {
            Log.d(TAG, "saveNote: not empty");
            data.putExtra(EXTRA_ID, id);
        }
        Log.d(TAG, "setResult = ok");
        setResult(RESULT_OK, data);
        finish();
        Log.d(TAG, "finish() ");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.add_note_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.save_note:
//                saveNote();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onClick(View view) {
        saveNote();
    }
}