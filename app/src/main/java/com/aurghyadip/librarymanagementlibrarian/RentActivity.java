package com.aurghyadip.librarymanagementlibrarian;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RentActivity extends AppCompatActivity {
    private DatabaseReference rentRef;
    private DatabaseReference studentRef;

    private int copies;
    private String studentId;
    private String isbn;

    private EditText studentIdField;
    private Button rentBookBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        studentIdField = findViewById(R.id.rent_student_id_field);
        rentBookBtn = findViewById(R.id.rent_book_to_student_btn);

        rentRef = FirebaseDatabase.getInstance().getReference("Rent");
        studentRef = FirebaseDatabase.getInstance().getReference("Students");
    }

    @Override
    protected void onStart() {
        super.onStart();

        rentBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentId = studentIdField.getText().toString();
                isbn = getIntent().getStringExtra("isbn");

                if(!studentId.isEmpty()){
                    studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(studentId)) {
                                rentBookToStudent();
                            } else {
                                Toast.makeText(RentActivity.this, "Student does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(RentActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                            Log.d("RENT_ACTIVITY", "Database Error", databaseError.toException());
                        }
                    });
                } else {
                    Toast.makeText(RentActivity.this, "Student ID is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rentBookToStudent() {
        final RentDeposit rentDeposit = new RentDeposit();
        final DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Books");
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.child(isbn).getValue(Book.class);
                if (book != null) {
                    copies = book.getCopies();
                    if(copies > 0) {
                        bookRef.child(isbn).child("copies").setValue(copies - 1);
                        rentRef.child(studentId).child(isbn).setValue(rentDeposit.getCurrentDateString());
                        finish();
                    } else {
                        Toast.makeText(RentActivity.this, "No existing copies in library", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("BOOK DATABASE", "PERMISSION BOOK", databaseError.toException());
            }
        });
    }

}
