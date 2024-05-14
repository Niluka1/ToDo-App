package com.example.todoapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button add;
    AlertDialog dialog;
    LinearLayout layout;
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        layout = findViewById(R.id.container);
        dbHelper = new DatabaseHelper(this);

        buildDialog();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        loadTasksFromDatabase();
    }

    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);
        final EditText description = view.findViewById(R.id.descriptionEdit);

        builder.setView(view);
        builder.setTitle("Enter your Task")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = name.getText().toString();
                        String taskDescription = description.getText().toString();
                        addTaskToDatabase(taskName, taskDescription);
                        loadTasksFromDatabase();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog = builder.create();
    }

    private void addTaskToDatabase(String name, String description) {
        dbHelper.addTask(name, description);
    }

    private void loadTasksFromDatabase() {
        Cursor cursor = dbHelper.getAllTasks();
        layout.removeAllViews();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);

                // Check if the column indices are valid (-1 indicates column not found)
                if (titleIndex != -1 && descriptionIndex != -1 && idIndex != -1) {
                    String taskName = cursor.getString(titleIndex);
                    String taskDescription = cursor.getString(descriptionIndex);
                    int taskId = cursor.getInt(idIndex);
                    addCard(taskId, taskName, taskDescription);
                } else {
                    // Handle the case where one or more columns are not found in the cursor
                    // For example, log an error message or skip adding the task
                }
            }
            cursor.close();
        }
    }


    private void addCard(final int taskId, String name, String description) {
        final View view = getLayoutInflater().inflate(R.layout.card, null);

        TextView nameView = view.findViewById(R.id.name);
        TextView descriptionView = view.findViewById(R.id.description);
        Button delete = view.findViewById(R.id.delete);
        Button update = view.findViewById(R.id.update);

        nameView.setText(name);
        descriptionView.setText(description);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteTask(taskId);
                loadTasksFromDatabase();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTaskDialog(taskId);
            }
        });

        layout.addView(view);
    }

    private void updateTaskDialog(final int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText name = view.findViewById(R.id.nameEdit);
        final EditText description = view.findViewById(R.id.descriptionEdit);

        builder.setView(view);
        builder.setTitle("Update Task")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = name.getText().toString();
                        String taskDescription = description.getText().toString();
                        dbHelper.updateTask(taskId, taskName, taskDescription);
                        loadTasksFromDatabase();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }

}
