package programfirebase.comdasd.example.javad.progland;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by javad on 06.06.2018.
 */


public class Notes extends AppCompatActivity {

    private Button btnCreate;
    private EditText etTitle, etContent;

    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;

    private String noteID;

    private boolean isExist;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_note_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        try {
            noteID = getIntent().getStringExtra("noteId");

          //  Toast.makeText(this, noteID, Toast.LENGTH_SHORT).show();

            if (!noteID.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        btnCreate = (Button) findViewById(R.id.new_note_btn);
        etTitle = (EditText) findViewById(R.id.new_note_title);
        etContent = (EditText) findViewById(R.id.new_note_content);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(title, content);
                } else {
                    Snackbar.make(view, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        putData();
    }

    private void putData() {

        if (isExist) {
            fNotesDatabase.child(noteID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void createNote(String title, String content) {

        if (fAuth.getCurrentUser() != null) {

            if (isExist) {
                // UPDATE A NOTE
                Map updateMap = new HashMap();
                updateMap.put("title", etTitle.getText().toString().trim());
                updateMap.put("content", etContent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);

                fNotesDatabase.child(noteID).updateChildren(updateMap);

                Toast.makeText(this, "Note updated0", Toast.LENGTH_SHORT).show();
            } else {
                // CREATE A NEW NOTE
                final DatabaseReference newNoteRef = fNotesDatabase.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Notes.this, "Note added to database", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Notes.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }



        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.new_note_delete_btn:
                if (isExist) {
                    deleteNote();
                } else {
                    Toast.makeText(this, "Nothing to delete", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    private void deleteNote() {

        fNotesDatabase.child(noteID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Notes.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    noteID = "no";
                    finish();
                } else {
                    Log.e("NewNoteActivity", task.getException().toString());
                    Toast.makeText(Notes.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}


    /*private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();

    FirebaseListAdapter mAdapter;
    Firebase mRef;

    private EditText ET_new_task;
    private Button Btn_new_task;

    ListView ListUserTasks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);




                ListUserTasks = (ListView) findViewById(R.id.nav_notes);

        myRef = FirebaseDatabase.getInstance().getReference();

        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                .setQuery(myRef, String.class)
                .build();

        FirebaseListAdapter<String> adapter = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(View v, String s, int position) {
                TextView text = (TextView) v.findViewById(android.R.id.text1);
                text.setText(s);
            }
        };
        ListUserTasks.setAdapter(mAdapter);

        Btn_new_task = (Button)findViewById(R.id.btn_add);
        ET_new_task = (EditText)findViewById(R.id.et_new_tasks);

        Btn_new_task.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                myRef.child(user.getUid()).child("Tasks").push().setValue(ET_new_task.getText().toString());
            }
        });

    }
}

        mRef = new Firebase("https://progland-e4eb3.firebaseio.com/");

        ListUserTasks = (ListView) findViewById(R.id.rv_list_tasks);
        myRef = FirebaseDatabase.getInstance().getReference();

        FirebaseListAdapter<String> adapter = new FirebaseListAdapter<String>(this, String.class,android.R.layout.simple_list_item_1, myRef.child(user.getUid()).child("Tost")) {
            @Override
            protected void populateView(View v, String s, int position) {
                TextView text = (TextView) v.findViewById(android.R.id.text1);
                text.setText(s);
            }

        };
        ListUserTasks.setAdapter(mAdapter);

        ListUserTasks.setOnItemClickListener(new  AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                myRef.child(user.getUid()).child("Tasks").child("" + position).removeValue();
                DatabaseReference itemRef = mAdapter.getRef(position);
                itemRef.removeValue();
            }
        });

        Btn_new_task = (Button) findViewById(R.id.btn_add);
        ET_new_task = (EditText) findViewById(R.id.et_new_tasks);

        Btn_new_task.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                myRef.child(
                        user.getUid()).child("Tasks").
                        push().
                        setValue(ET_new_task.getText().toString());
            }
        });
*/
