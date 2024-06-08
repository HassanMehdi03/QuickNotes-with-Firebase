package com.example.quicknotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    ArrayList<Note> notes;
    Context context;

    public NoteAdapter(Context context, ArrayList<Note> notes)
    {
        this.notes=notes;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_note_desing,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvTitle.setText(notes.get(position).getTitle());
        holder.tvContent.setText(notes.get(position).getContent());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(notes.get(position).getDate());
        holder.tvDate.setText(formattedDate);

        holder.note.setOnLongClickListener(v-> updateDeleteDialog(holder, position));


    }

    private boolean updateDeleteDialog(ViewHolder holder, int position)
    {
        holder.note.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                View editNote=LayoutInflater.from(v.getContext())
                        .inflate(R.layout.update_delete_note_dialog,null,false);

                Button btnDelete,btnUpdate;
                EditText etTitle,etContent;
                btnUpdate=editNote.findViewById(R.id.btnUpdate);
                btnDelete=editNote.findViewById(R.id.btnDelete);
                etTitle=editNote.findViewById(R.id.etTitle);
                etContent=editNote.findViewById(R.id.etContent);

                etTitle.setText(notes.get(position).getTitle());
                etContent.setText(notes.get(position).getContent());

                Dialog updateDeleteDialog=new Dialog(v.getContext());
                updateDeleteDialog.setContentView(editNote);
                updateDeleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
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
                                    notes.get(position).setTitle(etTitle.getText().toString().trim());
                                    notes.get(position).setContent(etContent.getText().toString().trim());

                                    notifyDataSetChanged();
                                    Toast.makeText(v.getContext(), R.string.note_updated_successfully, Toast.LENGTH_SHORT).show();
                                    updateDeleteDialog.dismiss();

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
                                notes.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(v.getContext(), R.string.note_deleted_successfully, Toast.LENGTH_SHORT).show();
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
            }
        });
        return true;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle,tvContent,tvDate;
        CardView note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle=itemView.findViewById(R.id.tvTitle);
            tvContent=itemView.findViewById(R.id.tvContent);
            tvDate=itemView.findViewById(R.id.tvDate);
            note=itemView.findViewById(R.id.Note);

        }
    }

}
