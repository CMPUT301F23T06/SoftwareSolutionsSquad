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
    private TextView barcodeText;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    public ScanFragment(TextView textToAutofill) {
        this.barcodeText = textToAutofill;
    }

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

    private void initializeScanner() {
        barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
            .setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
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
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detector) {
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
