package edu.ewubd.cse489_23_2_2019360046;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ListView listEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    private EventDB eventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listEvents = findViewById(R.id.listEvents);
        events = new ArrayList<>();

        eventDB = new EventDB(this);

        findViewById(R.id.btnCreateNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
            }
        });
    }
    public void onStart(){
        super.onStart();
        loadData();
    }

    private void loadData(){
        events.clear();
        EventDB db = new EventDB(this);

        Cursor rows = db.selectEvents("SELECT * FROM events");
        if (rows.getCount() > 0) {

            while (rows.moveToNext()) {
                String id = rows.getString(0);
                String name = rows.getString(1);
                String place = rows.getString(2);
                long dateTime = rows.getLong(3);
                int capacity = rows.getInt(4);
                double budget = rows.getDouble(5);
                String email = rows.getString(6);
                String phone = rows.getString(7);
                String des = rows.getString(8);
                String eventType = rows.getString(9);
                Event e = new Event(id, name, place, dateTime, capacity, budget, email, phone, des, eventType);
                events.add(e);
            }
        }
        db.close();
        adapter = new CustomEventAdapter(this, events);
        listEvents.setAdapter(adapter);

        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, CreateEventActivity.class);
                i.putExtra("EventID", events.get(position).id);
                i.putExtra("name", events.get(position).name);
                i.putExtra("place", events.get(position).place);
                i.putExtra("datetime", events.get(position).datetime);
                i.getIntExtra("capacity", events.get(position).capacity);
                i.putExtra("budget", events.get(position).budget);
                i.putExtra("email", events.get(position).email);
                i.putExtra("phone", events.get(position).phone);
                i.putExtra("des", events.get(position).description);
                i.putExtra("type", events.get(position).eventType);
                startActivity(i);
            }
        });

        listEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Do you want to delete event - "+events.get(position).name +" ?";
                System.out.println(message);
                showDialog(message, "Delete Event", events.get(position).id);
                return true;
            }
        });
    }

    private void showDialog(String errMsg, String type, String eventId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(type);
        builder.setMessage(errMsg);
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventDB.deleteEvent(eventId);
                loadData();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}