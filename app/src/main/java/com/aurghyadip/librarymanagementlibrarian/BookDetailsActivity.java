package com.aurghyadip.librarymanagementlibrarian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BookDetailsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    private String isbn;

    private Toolbar toolbar;

    private TextView authorView;
    private TextView titleView;
    private TextView descriptionView;
    private TextView copiesView;
    int copies;

    Button depositBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        toolbar = findViewById(R.id.toolbar_book_details);
        setSupportActionBar(toolbar);

        authorView = findViewById(R.id.book_author);
        titleView = findViewById(R.id.book_title);
        descriptionView = findViewById(R.id.book_description);
        copiesView = findViewById(R.id.book_copies);
        depositBtn = findViewById(R.id.deposit_book_btn);

        isbn = getIntent().getStringExtra("isbn");

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Books");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(isbn)) {
                    Book book = dataSnapshot.child(isbn).getValue(Book.class);

                    if (book != null) {
                        titleView.setText(book.getTitle());
                        authorView.setText(book.getAuthor());
                        descriptionView.setText(book.getDescription());
                        copies = book.getCopies();
                        copiesView.setText(String.valueOf(book.getCopies()));
                    }
                } else {
                    LinearLayout hasBookDetails = findViewById(R.id.has_book_details_layout);
                    LinearLayout doesNotHaveBookDetails = findViewById(R.id.does_not_have_book_details);

                    hasBookDetails.setVisibility(View.GONE);
                    doesNotHaveBookDetails.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(BookDetailsActivity.this, "Failed to load book.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this, DepositActivity.class);
                intent.putExtra("isbn", isbn);
                intent.putExtra("copies", copies);
                startActivity(intent);
            }
        });
    }
}
