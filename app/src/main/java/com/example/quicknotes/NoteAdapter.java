package com.example.quicknotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class NoteAdapter extends FirebaseRecyclerAdapter<Note, NoteAdapter.ViewHolder> {

    public NoteAdapter(@NonNull FirebaseRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Note note) {

        String key=getRef(position).getKey();

        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        holder.tvDate.setText(note.getDate());

        holder.itemView.setOnClickListener(v-> updateDeleteDialog(holder,key,note) );

    }

    private void updateDeleteDialog(ViewHolder holder, String position,Note notes)
    {
        holder.itemView.setOnLongClickListener(v -> {

            View editNote=LayoutInflater.from(v.getContext())
                    .inflate(R.layout.update_delete_note_dialog,null,false);

            Button btnDelete,btnUpdate;
            EditText etTitle,etContent;
            btnUpdate=editNote.findViewById(R.id.btnUpdate);
            btnDelete=editNote.findViewById(R.id.btnDelete);
            etTitle=editNote.findViewById(R.id.etTitle);
            etContent=editNote.findViewById(R.id.etContent);

            etTitle.setText(notes.getTitle());
            etContent.setText(notes.getContent());

            Dialog updateDeleteDialog=new Dialog(v.getContext());
            updateDeleteDialog.setContentView(editNote);
            Objects.requireNonNull(updateDeleteDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            updateDeleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            updateDeleteDialog.show();

            AlertDialog.Builder AlertDialog=new AlertDialog.Builder(v.getContext());

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.setTitle(R.string.confirmation);
                    AlertDialog.setMessage(R.string.are_you_sure_you_want_to_update_this_note);

                    AlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String title=etTitle.getText().toString().trim();
                            String content=etContent.getText().toString().trim();

                            if(title.isEmpty())
                            {
                                etTitle.setError(v.getContext().getString(R.string.title_is_required));
                            }
                            else if(content.isEmpty())
                            {
                                etContent.setError(v.getContext().getString(R.string.content_is_required));
                            }
                            else
                            {
                                HashMap<String,Object> data=new HashMap<>();

                                data.put("title",title);
                                data.put("content",content);

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                String date = formatter.format(new Date());
                                data.put("date", date);

                                FirebaseDatabase.getInstance().getReference("Notes")
                                                .child(position)
                                                .setValue(data).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(v.getContext(), R.string.note_updated_successfully, Toast.LENGTH_SHORT).show();
                                                updateDeleteDialog.dismiss();
                                            }
                                        }).
                                        addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                updateDeleteDialog.dismiss();
                                            }
                                        });

                            }
                        }
                    });

                    AlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog.show();

                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.setTitle(R.string.confirmation);
                    AlertDialog.setMessage(R.string.are_you_sure_you_want_to_delete_this_note);

                    AlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateDeleteDialog.dismiss();

                           FirebaseDatabase.getInstance().getReference("Notes").
                                   child(position).removeValue().
                                   addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void unused) {
                                           Toast.makeText(v.getContext(), R.string.note_deleted_successfully, Toast.LENGTH_SHORT).show();
                                       }
                                   }).
                                   addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   });
                        }
                    });

                    AlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog.show();
                }
            });

            return false;
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_design, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
