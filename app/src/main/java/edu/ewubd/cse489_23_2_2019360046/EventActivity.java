package edu.ewubd.cse489_23_2_2019360046;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

                double budget = Double.parseDouble(budgetStr);
                int capacity = Integer.parseInt(capacityStr);

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

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:m");
                try {
                    Date eventDate = formatter.parse(date);
                    Date now = new Date();
                    if (eventDate.before(now)) {
                        errMessage += "Event date need to be in the future\n";
                    }
                } catch (ParseException e) {
                    errMessage += "Invalid Date & Time format\n";
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
                    //
                    
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
