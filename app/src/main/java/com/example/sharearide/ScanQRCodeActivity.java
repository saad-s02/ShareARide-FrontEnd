package com.example.sharearide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sharearide.utils.QueryServer;
import com.example.sharearide.utils.ServerCallback;
import com.google.gson.JsonObject;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class ScanQRCodeActivity extends AppCompatActivity implements ServerCallback {

    private Button scan_btn, submit_btn;
    private TextView textview, image_picker;
    private ImageView qr_code;
    private boolean isValid = false;
    private String taxiId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        // set toolbar format
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        // initialize
        scan_btn = (Button) findViewById(R.id.btn_scan);
        image_picker = (TextView) findViewById(R.id.image_picker);
        qr_code = (ImageView) findViewById(R.id.qr_code);
        submit_btn = (Button) findViewById(R.id.submit);

        image_picker.setPaintFlags(image_picker.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(ScanQRCodeActivity.this);

                // Customize the scanner configuration
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.QR_CODE);

                // Launch the scanner activity
                intentIntegrator.initiateScan();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid) {
                    Intent intent = new Intent(ScanQRCodeActivity.this, TempOfferActivity.class);
                    intent.putExtra("taxiId", taxiId);
                    startActivity(intent);
                }
            }
        });

        ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                new LoadImageTask().execute(uri);
            }
        });

        image_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerLauncher.launch("image/*");

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Retrieve the scan result from the Intent
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // Handle the scan result
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                getScanQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getScanQRCode(String qrcode) {
        QueryServer.scanQRCode(this, qrcode);
    }

    public void onDone(JsonObject response) {
        textview = (TextView) findViewById(R.id.textView);
        String taxistatus = response.get("taxistatus").getAsString();
        if (taxistatus.equals("available")) {
            textview.setText("The taxi is available");
            isValid = true;
        } else if (taxistatus.equals("in use")){
            textview.setText("The taxi is in use, find another taxi");
            isValid = false;
        }
    }

    public Context getContext() {
        return this;
    }

    private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            try {
                InputStream in = getContentResolver().openInputStream(uris[0]);
                return BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            // Display the image in the ImageView
            qr_code.setImageBitmap(bitmap);

            // Recognize the QR code image as a QR code
            recognizeQrCode(bitmap);
        }
    }

    private void recognizeQrCode(@NonNull Bitmap bitmap) {
        // Convert the bitmap to a binary bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        // Set up the hints for the MultiFormatReader
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, true);

        // Use the MultiFormatReader to recognize the QR code
        MultiFormatReader reader;
        reader = new MultiFormatReader();
        reader.setHints(hints);
        try {
            Result result = reader.decode(binaryBitmap);
            String qrCodeContent = result.getText();
            // handle the content right here
            taxiId = qrCodeContent;
            getScanQRCode(qrCodeContent);
        } catch (ReaderException e) {
            // Handle the error here
            // ...
        }
    }
}
