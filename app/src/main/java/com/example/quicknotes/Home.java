package com.example.quicknotes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class Home extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NoteAdapter adapter;
    private RecyclerView.LayoutManager manager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        showAddDialog();
    }
    private void showAddDialog() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog addDialog=new Dialog(Home.this);
                View addNote= LayoutInflater.from(Home.this).inflate(R.layout.add_note_dialog_design,null,false);

                Button btnAdd,btnCancel;
                btnAdd=addNote.findViewById(R.id.btnAdd);
                btnCancel=addNote.findViewById(R.id.btnCancel);

                EditText etTitle,etContent;
                etTitle=addNote.findViewById(R.id.etTitle);
                etContent=addNote.findViewById(R.id.etContent);


                addDialog.setContentView(addNote);
                addDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                addDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                addDialog.show();


                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String title=etTitle.getText().toString().trim();
                        String content=etContent.getText().toString().trim();

                        if(title.isEmpty())
                        {
                            etTitle.setError(getString(R.string.title_is_required));
                        }
                        else if(content.isEmpty())
                        {
                            etContent.setError(getString(R.string.content_is_required));
                        }
                        else
                        {
                            notes.add(new Note(title,content,new Date()));
                            Toast.makeText(Home.this, R.string.note_added_successfully, Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            addDialog.dismiss();
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

    private void init()
    {
        fabAdd=findViewById(R.id.fabAdd);
        recyclerView=findViewById(R.id.rvList);
        notes=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter=new NoteAdapter(this,notes);
        recyclerView.setAdapter(adapter);
    }

}