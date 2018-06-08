package programfirebase.comdasd.example.javad.progland;

/**
 * Created by javad on 07.06.2018.
 */

    import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotesMine extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference fNotesDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        mNotesList = (RecyclerView) findViewById(R.id.notes_list);

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(gridLayoutManager);
      //  gridLayoutManager.setReverseLayout(true);
      //  gridLayoutManager.setStackFromEnd(true);
        mNotesList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }


    private Query query = fNotesDatabase;
    FirebaseRecyclerOptions<NoteModel> options =
            new FirebaseRecyclerOptions.Builder<NoteModel>()
                    .setQuery(query, NoteModel.class)
                    //.setLifecycleOwner(NotesMine.this)
                    .build();

    FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(options) {
        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_notes, parent, false);

            return new NoteViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(final NoteViewHolder holder, int position, NoteModel model) {
            final String noteId = getRef(position).getKey();

            fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("timestamp")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                        holder.setNoteTitle(title);
                        holder.setNoteTime(timestamp);

                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        holder.setNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getApplicationContext()));

                        holder.noteCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(NotesMine.this, NewNoteActivity.class);
                                intent.putExtra("noteId", noteId);
                                startActivity(intent);
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


   /* private void updateUI() {
        if (fAuth.getCurrentUser() != null){
            Log.i("MainActivity", "fAuth != null");
        } else {
            Intent startIntent = new Intent(NotesMine.this, MainActivity.class);
            startActivity(startIntent);
            finish();
            Log.i("MainActivity", "fAuth == null");
        }

    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_new_note_btn:
                Intent newIntent = new Intent(NotesMine.this, NewNoteActivity.class);
                startActivity(newIntent);
                break;
        }

        return true;
    }



    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
