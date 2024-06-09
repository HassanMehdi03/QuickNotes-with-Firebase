package com.example.quicknotes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Home extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private TextView tvEmptyListText;
    private ImageView ivEmptyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        setupRecyclerView();
        updateEmptyState();
        showAddDialog();
    }

    private void init() {
        tvEmptyListText = findViewById(R.id.tvEmptyListText);
        ivEmptyList = findViewById(R.id.ivEmptyList);
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.rvList);
    }

    private void setupRecyclerView() {
        DatabaseReference notesRef = FirebaseDatabase.getInstance().getReference().child("Notes");

        FirebaseRecyclerOptions<Note> options = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(notesRef, Note.class)
                .build();

        adapter = new NoteAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showAddDialog() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog addDialog = new Dialog(Home.this);
                View addNote = LayoutInflater.from(Home.this).inflate(R.layout.add_note_dialog_design, null, false);

                Button btnAdd = addNote.findViewById(R.id.btnAdd);
                Button btnCancel = addNote.findViewById(R.id.btnCancel);
                EditText etTitle = addNote.findViewById(R.id.etTitle);
                EditText etContent = addNote.findViewById(R.id.etContent);

                addDialog.setContentView(addNote);
                addDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                addDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addDialog.show();

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = etTitle.getText().toString().trim();
                        String content = etContent.getText().toString().trim();

                        if (title.isEmpty()) {
                            etTitle.setError(getString(R.string.title_is_required));
                        } else if (content.isEmpty()) {
                            etContent.setError(getString(R.string.content_is_required));
                        } else {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("title", title);
                            data.put("content", content);

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String date = formatter.format(new Date());
                            data.put("date", date);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Notes")
                                    .push()
                                    .setValue(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Home.this, R.string.note_added_successfully, Toast.LENGTH_SHORT).show();
                                            updateEmptyState();
                                            addDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            addDialog.dismiss();
                                        }
                                    });
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDialog.dismiss();
                    }
                });
            }
        });
    }

    private void updateEmptyState() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notes");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    tvEmptyListText.setVisibility(View.VISIBLE);
                    ivEmptyList.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyListText.setVisibility(View.GONE);
                    ivEmptyList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
