package com.example.softwaresolutionssquad.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.softwaresolutionssquad.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanFragment extends DialogFragment {
    private SurfaceView surfaceView;
    private final TextView barcodeText;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    /**
     * Constructor for ScanFragment. Initializes the fragment with a TextView to display the scanned barcode.
     * @param textToAutofill The TextView where the scanned barcode data will be displayed.
     */
    public ScanFragment(TextView textToAutofill) {
        this.barcodeText = textToAutofill;
    }

    /**
     * Creates the dialog for the ScanFragment with custom layout and initializations for the barcode scanner.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return A new Dialog instance to be displayed by the fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_scan, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.CustomAlertDialogTheme));

        surfaceView = view.findViewById(R.id.surfaceView);

        initializeScanner();

        return builder.setView(view).setNegativeButton("Cancel", null).create();
    }

    /**
     * Initializes the barcode scanner and camera source, setting up the necessary detectors and listeners.
     */

    private void initializeScanner() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
            .setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    boolean haveCameraPermission =
                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED;
                    if (haveCameraPermission) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detector) {
                final SparseArray<Barcode> barcodes = detector.getDetectedItems();
                if (barcodes.size() > 0) {
                    barcodeText.setText(barcodes.valueAt(0).displayValue);
                    dismiss();
                }
            }

            @Override
            public void release() {
                Toast.makeText(getContext(), "An error occurred while scanning barcodes. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
