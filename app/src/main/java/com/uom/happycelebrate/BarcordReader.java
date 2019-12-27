package com.uom.happycelebrate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.zxing.Result;
import com.uom.happycelebrate.data.QRUniqueCode;
import com.uom.happycelebrate.utils.VideoFinder;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BarcordReader extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private TextureView textureView;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private String redirect_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);


        redirect_page = getIntent().getStringExtra("redirect_page");

        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }


    }


    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BarcordReader.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
        Toast.makeText(BarcordReader.this,myResult,Toast.LENGTH_SHORT).show();

        QRUniqueCode.qr_code = result.getText();
        redirect();

//        new VideoFinder() {
//            @Override
//            public void getData(DataSnapshot dataSnapshot) {
//
////                for (DataSnapshot item: dataSnapshot.getChildren()) {
//                   QRUniqueCode.video_url = dataSnapshot.getValue().toString();
//                   Toast.makeText(BarcordReader.this,dataSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
//                   redirect();
//
//            }
//        }.searchData(result.getText());



//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview(BarcordReader.this);
//            }
//        });
//        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
//                startActivity(browserIntent);
//            }
//        });
//        builder.setMessage(result.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
    }


    private  void  redirect(){


        if(redirect_page.equals("CREATE_CARD")){


            Toast.makeText(BarcordReader.this,"hereeeeeeeeeee redired",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BarcordReader.this, CreateCard.class);
            startActivity(intent);
            BarcordReader.this.finish();

        }else if(redirect_page.equals("AR_PAGE")){

            Toast.makeText(BarcordReader.this,"hereeeeeeeeeee redired",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BarcordReader.this, ArActionPage.class);
            startActivity(intent);
            BarcordReader.this.finish();

        }



    }



}
