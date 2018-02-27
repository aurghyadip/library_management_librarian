package com.aurghyadip.librarymanagementlibrarian;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EditBookFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private AwesomeValidation awesomeValidationIsbn;
    private AwesomeValidation awesomeValidationEditBook;

    private String isbnNumber;

    private Button searchBtn;
    private Button editBookDetailsBtn;
    private Button scanIsbnBtn;

    private TextInputEditText isbn;
    private TextInputEditText bookTitle;
    private TextInputEditText bookAuthor;
    private TextInputEditText bookDescription;
    private TextInputEditText bookCopies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_book, container, false);

        awesomeValidationIsbn = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidationEditBook = new AwesomeValidation(ValidationStyle.COLORATION);

        firebaseDatabase = FirebaseDatabase.getInstance();

        searchBtn = rootView.findViewById(R.id.edit_search_btn);
        isbn = rootView.findViewById(R.id.edit_search_isbn);
        bookTitle = rootView.findViewById(R.id.edit_book_name);
        bookAuthor = rootView.findViewById(R.id.edit_book_author);
        bookDescription = rootView.findViewById(R.id.edit_book_description);
        bookCopies = rootView.findViewById(R.id.edit_book_copies);
        editBookDetailsBtn = rootView.findViewById(R.id.edit_book_details);
        scanIsbnBtn = rootView.findViewById(R.id.scan_isbn_edit);

        // Validation for ISBN
        awesomeValidationIsbn.addValidation(isbn, "^(97(8|9))?\\d{9}(\\d|X)$", getString(R.string.isbn_error));

        // Validation for Book Details
        //TODO: Extract string resource from here
        awesomeValidationEditBook.addValidation(bookTitle, RegexTemplate.NOT_EMPTY, "Title should not be empty");
        awesomeValidationEditBook.addValidation(bookAuthor, RegexTemplate.NOT_EMPTY, "Author should not be empty");
        awesomeValidationEditBook.addValidation(bookDescription, RegexTemplate.NOT_EMPTY, "Description should not be empty");
        awesomeValidationEditBook.addValidation(bookCopies, RegexTemplate.NOT_EMPTY, "Copies should not be empty, enter at least 1");

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = firebaseDatabase.getReference("Books");

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidationIsbn.validate()) {
                    isbnNumber = isbn.getText().toString();
                    fetchBookDataFromDB();
                }
            }
        });

        scanIsbnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanFromFragment();
            }
        });

        editBookDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidationEditBook.validate()) {
                    Book addBook = new Book(
                            bookTitle.getText().toString(),
                            bookAuthor.getText().toString(),
                            bookDescription.getText().toString(),
                            Integer.valueOf(bookCopies.getText().toString())
                    );
                    databaseReference.child(isbnNumber).setValue(addBook);
                }
                Toast.makeText(getActivity(), bookTitle.getText().toString() + " edited Successfully", Toast.LENGTH_SHORT).show();
                Fragment fragment = new ScanFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        });

    }
    private void scanFromFragment() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(), "No ISBN Found", Toast.LENGTH_SHORT).show();
            }
            else {
                isbnNumber = result.getContents();
                isbn.setText(result.getContents());
                fetchBookDataFromDB();
            }
        }
    }

    private void fetchBookDataFromDB() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(isbnNumber)) {
                    Book book = dataSnapshot.child(isbnNumber).getValue(Book.class);

                    if (book != null) {
                        bookTitle.setText(book.getTitle());
                        bookAuthor.setText(book.getAuthor());
                        bookDescription.setText(book.getDescription());
                        bookCopies.setText(String.valueOf(book.getCopies()));
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("No books found with that ISBN in Database")
                            .setTitle("Book Not Found");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
