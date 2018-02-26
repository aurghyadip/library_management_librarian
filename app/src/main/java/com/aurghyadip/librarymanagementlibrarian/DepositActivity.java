package com.aurghyadip.librarymanagementlibrarian;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DepositActivity extends AppCompatActivity {
    TextView studentNameView;
    TextView studentIdView;
    TextView bookFineView;
    TextView bookRentDateView;

    Button payFineBtn;

    EditText studentIdField;
    Button searchStudentBtn;

    String isbn;
    String studentId;

    DatabaseReference studentRef;
    DatabaseReference rentRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        Toolbar toolbar = findViewById(R.id.toolbar_deposit);
        setSupportActionBar(toolbar);

        studentNameView = findViewById(R.id.deposit_student_name);
        studentIdView = findViewById(R.id.deposit_student_id);
        bookFineView = findViewById(R.id.deposit_fine);
        bookRentDateView = findViewById(R.id.deposit_rent_date);
        payFineBtn = findViewById(R.id.pay_fine_deposit_btn);

        studentIdField = findViewById(R.id.deposit_student_id_field);
        searchStudentBtn = findViewById(R.id.deposit_search_student_btn);

        isbn = getIntent().getStringExtra("isbn");

        studentRef = FirebaseDatabase.getInstance().getReference("Students");
        rentRef = FirebaseDatabase.getInstance().getReference("Rent");

    }

    @Override
    protected void onStart() {
        super.onStart();

        searchStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!studentIdField.getText().toString().isEmpty()) {
                    studentId = studentIdField.getText().toString();
                    studentRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(studentId)) {
                                Student student = dataSnapshot.child(studentId).getValue(Student.class);
                                if (student != null) {
                                    studentIdView.setText(studentId);
                                    studentNameView.setText(student.getName());
                                    //TODO: Remove badly nested code
                                    viewRentData(studentId);
                                }
                            } else {
                                Toast.makeText(
                                        DepositActivity.this,
                                        "No student found with this ID",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(DepositActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                            FirebaseCrash.report(databaseError.toException());
                        }
                    });
                }
            }
        });

        
    }

    private void viewRentData(final String stId) {
        rentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(stId)) {
                    if(dataSnapshot.child(stId).hasChild(isbn)) {
                        String rentDate = dataSnapshot.child(stId).child(isbn).getValue(String.class);
                        RentDeposit depositFunctions = new RentDeposit();
                        int fine = depositFunctions.getFine(rentDate);
                        bookFineView.setText(String.valueOf(fine));
                        bookRentDateView.setText(rentDate);
                        findViewById(R.id.deposit_student_search).setVisibility(View.GONE);
                        findViewById(R.id.deposit_detail).setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(DepositActivity.this, "Not rented this book", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DepositActivity.this, "Has not rented any book", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DepositActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                FirebaseCrash.report(databaseError.toException());
            }
        });
    }
}
