package edu.ewubd.cse489_23_2_2019360046;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEventActivity extends AppCompatActivity {
    EditText etName, etPlace, etDate, etCapacity, etBudget, etEmail, etPhone, etDsc;
    TextView errorTv;
    RadioButton rIndoor, rOutdoor, rOnline;
    private String eventID = "";
    private EventDB eventDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventDB = new EventDB(this);

        etName = findViewById(R.id.etName);
        etPlace= findViewById(R.id.etPlace);
        etDate = findViewById(R.id.etDateTime);
        etCapacity = findViewById(R.id.etCapacity);
        etBudget = findViewById(R.id.etBudget);
        etEmail= findViewById(R.id.etEmail);
        etPhone= findViewById(R.id.etPhone);
        etDsc = findViewById(R.id.etDes);
        rIndoor = findViewById(R.id.rdIndoor);
        rOutdoor = findViewById(R.id.rdOutdoor);
        rOnline = findViewById(R.id.rdOnline);

        Intent i = getIntent();
        if(i.hasExtra("EventID")){
            eventID = i.getStringExtra("EventID");
            etName.setText(i.getStringExtra("name"));
            etPlace.setText(i.getStringExtra("place"));

            long longValue = 123456789L;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date(i.getLongExtra("datetime", longValue));
            etDate.setText(sdf.format(date));

            etCapacity.setText(String.valueOf(i.getIntExtra("capacity", 1)));
            etBudget.setText(String.valueOf(i.getDoubleExtra("budget", 1)));
            etEmail.setText(i.getStringExtra("email"));
            etPhone.setText(i.getStringExtra("phone"));
            etDsc.setText(i.getStringExtra("des"));
            String type = i.getStringExtra("type");

            rIndoor.setChecked("IN".equals(type));
            rOnline.setChecked("ON".equals(type));
            rOutdoor.setChecked("OUT".equals(type));
        }

        errorTv = findViewById(R.id.tvErrorMsg);
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rIndoor = findViewById(R.id.rdIndoor);
                rOutdoor = findViewById(R.id.rdOutdoor);
                rOnline = findViewById(R.id.rdOnline);

                String name = etName.getText().toString();
                String capacity = etCapacity.getText().toString();
                String place = etPlace.getText().toString();
                String date = etDate.getText().toString();
                String budget = etBudget.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String desc = etDsc.getText().toString();
                String eventType = "";
                String err = "";

                int _capacity = Integer.parseInt(capacity);
                double _budget = Double.parseDouble(budget);
                long _date = 0;

                if(!name.isEmpty() && !place.isEmpty() && !date.isEmpty() && !capacity.isEmpty() && !budget.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !desc.isEmpty()){
                    if(name.length() < 4 || name.length() > 12 || !name.matches("^[a-zA-Z ]+$")){
                        err += "Invalid Name (4-12 long and only alphabets)\n";
                    }

                    if(place.length() >= 6 && place.length() <= 64 && !place.matches("^[a-zA-Z0-9, ]+$")){
                        err += "Invalid Place (only alpha-numeric and , and 6-64 characters)\n";
                    }

                    boolean isIndoor = rIndoor.isChecked();
                    boolean isOutdoor = rOutdoor.isChecked();
                    boolean isOnline = rOnline.isChecked();

                    if(!isIndoor && !isOutdoor && !isOnline){
                        err += "Please select event type\n";
                    } else {
                        eventType = isIndoor ? "IN" : (isOutdoor ? "OUT" : "ON");
                    }

                    int cap;
                    double event_budget;

                    cap = _capacity;
                    event_budget = _budget;

                    if(cap <= 0){
                        err += "Invalid capacity (number greater than zero)\n";
                    }

                    if(event_budget < 1000){
                        err += "Invalid budget (number greater than 1000.00)\n";
                    }

                    String format = "yyyy-MM-dd HH:mm";
                    try {
                        DateTimeFormatter formatter = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            formatter = DateTimeFormatter.ofPattern(format);

                            LocalDateTime inputDate = LocalDateTime.parse(date, formatter);
                            LocalDateTime curDate = LocalDateTime.now();

                            if (inputDate.isBefore(curDate)) {
                                err += "Input date and time is before the current date and time\n" +
                                        "or Invalid date format (yyyy-MM-dd HH:mm)\n";
                            }
                            else {
                                _date = inputDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                                Log.d("mydate1", String.valueOf(_date));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.d("mydate", String.valueOf(ex));
                        err += "Invalid date format (yyyy-MM-dd HH:mm)";
                    }

                    String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                    if(!email.matches(EMAIL_REGEX)){
                        err += "Invalid email format\n";
                    }

                    Pattern pattern = Pattern.compile("^\\+\\d{13}$");
                    Matcher matcher = pattern.matcher(phone);
                    if(!matcher.matches()){
                        err += "Invalid phone number (format +8801234556789)\n";
                    }

                    if(desc.length() < 10 || desc.length() > 1000){
                        err += "Invalid description format (10-1000 characters)\n";
                    }
                }
                else{
                    err += "Fill all the fields\n";
                }

                if(err.length() > 0){
                    showErrorDialog(err);
                    errorTv.setText(err);
                }

                if(eventID.isEmpty()){
                    eventID = name + System.currentTimeMillis();
                    eventDB.insertEvent(eventID, name, place, _date, _capacity, _budget, email, phone, desc, eventType);
                    Toast.makeText(CreateEventActivity.this, "Event Insert successfully!", Toast.LENGTH_SHORT).show();
                }
                else {
                    eventDB.updateEvent(eventID, name, place, _date, _capacity, _budget, email, phone, desc, eventType);
                    Toast.makeText(CreateEventActivity.this, "Event Update successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cancel btn");
            }
        });

        findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, "event details");
                i.setType("text/plain");
                startActivity(i);
            }
        });
    }

    private void showErrorDialog(String errMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errMsg);
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