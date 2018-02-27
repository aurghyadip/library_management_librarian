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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookFragment extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private VolumeInfo volumeInfo;

    private AwesomeValidation awesomeValidation;

    private TextInputEditText isbn;
    private TextInputEditText bookTitle;
    private TextInputEditText bookAuthor;
    private TextInputEditText bookDescription;
    private TextInputEditText bookCopies;

    private Button addBookBtn;
    private Button searchBookBtn;
    private Button scanIsbnBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        isbn = rootView.findViewById(R.id.add_isbn);
        bookTitle = rootView.findViewById(R.id.add_title);
        bookAuthor = rootView.findViewById(R.id.add_author);
        bookDescription = rootView.findViewById(R.id.add_description);
        bookCopies = rootView.findViewById(R.id.add_copies);
        addBookBtn = rootView.findViewById(R.id.add_book_btn);
        searchBookBtn = rootView.findViewById(R.id.search_isbn_add);
        scanIsbnBtn = rootView.findViewById(R.id.scan_isbn_add);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = firebaseDatabase.getReference("Books");

        // Validating the data through AwesomeValidation Library, same as ScanFragment
        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        //TODO: Extract string resource
        awesomeValidation.addValidation(isbn, "^(97(8|9))?\\d{9}(\\d|X)$", "ISBN Error");
        awesomeValidation.addValidation(bookTitle, RegexTemplate.NOT_EMPTY, "Title should not be empty");
        awesomeValidation.addValidation(bookAuthor, RegexTemplate.NOT_EMPTY, "Author should not be empty");
        awesomeValidation.addValidation(bookDescription, RegexTemplate.NOT_EMPTY, "Description should not be empty");
        awesomeValidation.addValidation(bookCopies, RegexTemplate.NOT_EMPTY, "Copies should not be empty, enter at least 1");

        searchBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add new validation
                getBookInfoFromAPI(isbn.getText().toString());
            }
        });

        scanIsbnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanFromFragment();
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(isbn.getText().toString())) {
                                Book newBook = new Book(
                                        bookTitle.getText().toString(),
                                        bookAuthor.getText().toString(),
                                        bookDescription.getText().toString(),
                                        Integer.valueOf(bookCopies.getText().toString())
                                );
                                databaseReference.child(isbn.getText().toString()).setValue(newBook);
                                //TODO: Add navigation back to show that the data has been entered.
                                //Added fragment transaction
                                Toast.makeText(getActivity(), bookTitle.getText().toString() + " added Successfully", Toast.LENGTH_SHORT).show();
                                Fragment fragment = new ScanFragment();
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                fragmentTransaction.commit();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Please check ISBN number and enter it again")
                                        .setTitle("ISBN Exists in Database");

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("AddBookFragment:", "Pushing to database failed", databaseError.toException());
                        }
                    });
                }
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
                //TODO: Add the book details to the fields
                isbn.setText(result.getContents());
                getBookInfoFromAPI(result.getContents());
            }
        }
    }

    private void getBookInfoFromAPI(String query) {
        Call<BookInfo> bookInfo = BooksAPI.getBooksService().getBookInfo("isbn:" + query);

        bookInfo.enqueue(new Callback<BookInfo>() {
            @Override
            public void onResponse(Call<BookInfo> call, Response<BookInfo> response) {
                if (response.isSuccessful()) {
                    BookInfo info = response.body();
                    if (info != null) {
                        if (info.getTotalItems() > 0){
                            Item item = info.getItems().get(0);
                            volumeInfo = item.getVolumeInfo();
                            bookTitle.setText(volumeInfo.getTitle());
                            bookAuthor.setText(volumeInfo.getAuthors().get(0));
                            bookDescription.setText(volumeInfo.getDescription());
                        } else {
                            Toast.makeText(getActivity(), "Book not found in Google DB", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BookInfo> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
