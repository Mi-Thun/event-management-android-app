package edu.ewubd.cse489_23_2_2019360046;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class ViewEventActivity extends AppCompatActivity {

    private TextView Name, Email, Place, Description;
    private Button btnUpdate, btnDelete;
    private EventDB eventDB;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Name = findViewById(R.id.tvName);
        Email = findViewById(R.id.tvEmail);
        Place = findViewById(R.id.tvPlace);
        Description = findViewById(R.id.tvDescription);

        String eventID = getIntent().getStringExtra("event_id");

        System.out.println("---------------View called to received id");
        System.out.println(eventID);

        // String eventID = "Mohsenul1691853534995";

        eventDB = new EventDB(this);
        Cursor cursor = eventDB.selectEvents("SELECT * FROM events WHERE ID = '" + eventID + "'");

        String eventName = "", eventPlace = "", eventEmail = "", eventDescription = "", eventPhone = "";

        if (cursor != null && cursor.moveToFirst()) {
            eventName = cursor.getString(cursor.getColumnIndex("title"));
            eventPlace = cursor.getString(cursor.getColumnIndex("place"));
            @SuppressLint("Range") long eventDateTime = cursor.getLong(cursor.getColumnIndex("datetime"));
            @SuppressLint("Range") int eventCapacity = cursor.getInt(cursor.getColumnIndex("capacity"));
            @SuppressLint("Range") double eventBudget = cursor.getDouble(cursor.getColumnIndex("budget"));
            eventEmail = cursor.getString(cursor.getColumnIndex("email"));
            eventPhone = cursor.getString(cursor.getColumnIndex("phone"));
            eventDescription = cursor.getString(cursor.getColumnIndex("des"));

            Name.setText("Name: " + eventName);
            Email.setText("Email: " + eventEmail);
            Place.setText("Place: " + eventPlace);
            Description.setText("Description: " + eventDescription);
        }

        if (cursor != null) {
            cursor.close();
        }

        String finalEventName = eventName;
        String finalEventPlace = eventPlace;
        String finalEventPhone = eventPhone;
        String finalEventDescription = eventDescription;
        String finalEventEmail = eventEmail;
        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEventActivity.this, EventActivity.class);
                intent.putExtra("event_id", eventID);
                intent.putExtra("name", finalEventName);
                intent.putExtra("place", finalEventPlace);
                intent.putExtra("email", finalEventEmail);
                intent.putExtra("phone", finalEventPhone);
                intent.putExtra("description", finalEventDescription);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDB.deleteEvent(eventID);
                Intent intent = new Intent(ViewEventActivity.this, ViewEventActivity.class);
                startActivity(intent);
            }
        });
    }
}
