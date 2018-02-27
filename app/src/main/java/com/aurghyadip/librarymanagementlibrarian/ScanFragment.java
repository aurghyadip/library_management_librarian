package com.aurghyadip.librarymanagementlibrarian;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//TODO: Add data handling and Barcode Scanner

public class ScanFragment extends Fragment {

    private Button scanBooks;
    private Button scanIsbn;
    private TextInputEditText isbn;

    private AwesomeValidation awesomeValidation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        scanBooks = rootView.findViewById(R.id.search_books);
        scanIsbn = rootView.findViewById(R.id.barcode_scan);
        isbn = rootView.findViewById(R.id.book_search_field);

        //Awesome validation block
        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(isbn, "^(97(8|9))?\\d{9}(\\d|X)$", getString(R.string.isbn_error));

        scanIsbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanFromFragment();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scanBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    startBookDetailsActivity(isbn.getText().toString());
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
                startBookDetailsActivity(result.getContents());
            }
        }
    }
    private void startBookDetailsActivity(String isbnNumber) {
        Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
        intent.putExtra("isbn", isbnNumber);
        startActivity(intent);
    }

}
