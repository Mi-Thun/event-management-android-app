package edu.ewubd.cse489_23_2_2019360046;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventActivity extends Activity {
    private EditText etName, etPlace, etDate, etCapacity, etBudget, etEmail, etPhone, etDescription;

    private Button btnSave, btnShare, btnCancel;

    private RadioButton rdIndoor, rdOutdoor, rdOnline;

    private RadioGroup radioGroup;

    private String eventID = "";
    private EventDB eventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        etName = findViewById(R.id.etName);
        etPlace = findViewById(R.id.etPlace);
        rdIndoor = findViewById(R.id.rdIndoor);
        rdOutdoor = findViewById(R.id.rdOutdoor);
        rdOnline = findViewById(R.id.rdOnline);
        etDate = findViewById(R.id.etDate);
        etEmail = findViewById(R.id.etEmail);
        radioGroup = findViewById(R.id.radioGroup);
        etCapacity = findViewById(R.id.etCapacity);
        etBudget = findViewById(R.id.etBudget);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnShare = findViewById(R.id.btnShare);
        btnCancel = findViewById(R.id.btnCancel);

        eventDB = new EventDB(this);

        String receivedEventId = getIntent().getStringExtra("event_id");
        String name = getIntent().getStringExtra("name");
        String place = getIntent().getStringExtra("place");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String description = getIntent().getStringExtra("description");

        if (receivedEventId != null) {
            etName.setText(name);
            etPlace.setText(place);
            etDescription.setText(description);
            etPhone.setText(phone);
            etEmail.setText(email);
        } else {
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String place = etPlace.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String budgetStr = etBudget.getText().toString().trim();
                String capacityStr = etCapacity.getText().toString().trim();

                String errMessage = "";

                if (name.isEmpty() || place.isEmpty() || date.isEmpty() || email.isEmpty() || phone.isEmpty() || description.isEmpty() || budgetStr.isEmpty() || capacityStr.isEmpty()) {
                    errMessage = "All fields are mandatory.\n";
                    showErrorDialog(errMessage);
                    return;
                }

                if (name.length() < 4 || name.length() > 12 || !name.matches("[a-zA-Z]+")) {
                    errMessage += "Invalid Name\n";
                }

                if (place.length() < 6 || place.length() > 64 || !place.matches("[a-zA-Z0-9]+")) {
                    errMessage += "Invalid Place\n";
                }

                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    errMessage += "Invalid Event Type\n";
                }

//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:i");
//                try {
//                    Date eventDate = formatter.parse(date);
//                    Date now = new Date();
//                    if (eventDate.before(now)) {
//                        errMessage += "Event date need to be in the future\n";
//                    }
//                } catch (ParseException e) {
//                    errMessage += "Invalid Date & Time format\n";
//                }

                boolean isDateTimeOkay = false;
                String strDateTime = etDate.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm");
                long datetime = 0;
                try{
                    Date eventDate = formatter.parse(strDateTime);
                    Date now = new Date();
                    isDateTimeOkay = eventDate.after(now);
                    if(isDateTimeOkay){
                        datetime = eventDate.getTime();
                    }
                }catch (Exception e){}

                if(!isDateTimeOkay){
                    errMessage += "Invalid Date & Time format\n";
                }

                double budget = 0;
                int capacity = 0;

                try {
                    budget = Double.parseDouble(budgetStr);
                    capacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException e) {
                    errMessage += "Invalid type in budget and capacity\n";
                }

                if (capacity <= 0) {
                    errMessage += "Invalid Capacity\n";
                }

                if (budget <= 1000) {
                    errMessage += "Invalid Budget\n";
                }

                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email);
                if (!matcher.matches()) {
                    errMessage += "Invalid Email\n";
                }

                regex = "^(\\+)?(88)?01[0-9]{9}$";
                pattern = Pattern.compile(regex);
                matcher = pattern.matcher(phone);
                if (!matcher.matches()) {
                    errMessage += "Invalid Phone\n";
                }

                if (description.length() < 10 || description.length() > 1000) {
                    errMessage += "Invalid Description\n";
                }

                if (!errMessage.isEmpty()) {
                    showErrorDialog(errMessage);
                } else {
                    if (receivedEventId != null) {
                        eventID = receivedEventId;
                        eventDB.updateEvent(receivedEventId, name, place, datetime, capacity, budget, email, phone, description);
                        Toast.makeText(EventActivity.this, "Event update successfully!", Toast.LENGTH_SHORT).show();
                    } else{
                        eventID = name + System.currentTimeMillis();
                        eventDB.insertEvent(eventID, name, place, datetime, capacity, budget, email, phone, description);
                        Toast.makeText(EventActivity.this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!eventID.isEmpty()) {
                    Cursor cursor = eventDB.selectEvents("SELECT * FROM events WHERE ID = '" + eventID + "'");
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") String eventName = cursor.getString(cursor.getColumnIndex("title"));
                        @SuppressLint("Range") String eventPlace = cursor.getString(cursor.getColumnIndex("place"));
                        @SuppressLint("Range") long eventDateTime = cursor.getLong(cursor.getColumnIndex("datetime"));
                        @SuppressLint("Range") int eventCapacity = cursor.getInt(cursor.getColumnIndex("capacity"));
                        @SuppressLint("Range") double eventBudget = cursor.getDouble(cursor.getColumnIndex("budget"));
                        @SuppressLint("Range") String eventEmail = cursor.getString(cursor.getColumnIndex("email"));
                        @SuppressLint("Range") String eventPhone = cursor.getString(cursor.getColumnIndex("phone"));
                        @SuppressLint("Range") String eventDescription = cursor.getString(cursor.getColumnIndex("des"));

                        StringBuilder eventInfoBuilder = new StringBuilder();
                        eventInfoBuilder.append("Event Name: ").append(eventName).append("\n");
                        eventInfoBuilder.append("Event Place: ").append(eventPlace).append("\n");
                        eventInfoBuilder.append("Event Date and Time: ").append(new Date(eventDateTime).toString()).append("\n");
                        eventInfoBuilder.append("Event Capacity: ").append(eventCapacity).append("\n");
                        eventInfoBuilder.append("Event Budget: ").append(eventBudget).append("\n");
                        eventInfoBuilder.append("Event Email: ").append(eventEmail).append("\n");
                        eventInfoBuilder.append("Event Phone: ").append(eventPhone).append("\n");
                        eventInfoBuilder.append("Event Description: ").append(eventDescription).append("\n");

                        String eventInfo = eventInfoBuilder.toString();

                        showErrorDialog(eventInfo);

                        System.out.println(eventInfo);

                        Intent intent = new Intent(EventActivity.this, ViewEventActivity.class);
                        intent.putExtra("event_id", eventID);
                        startActivity(intent);
                    }
                    cursor.close();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showErrorDialog(String errMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);

        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
