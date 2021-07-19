package com.testpro.dressmatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.testpro.adapter.DressSlideAdapter;
import com.testpro.dressmatch.databinding.ActivityMainBinding;
import com.testpro.helper.DatabaseHandler;
import com.testpro.model.DressSlideItem;
import com.testpro.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback  {

    ActivityMainBinding bnd;

    DatabaseHandler db = new DatabaseHandler(this);

    private List<DressSlideItem> dressSlideShrt;
    private List<DressSlideItem> dressSlidePnt;
    DressSlideAdapter dressSlideAdapterShrt;
    DressSlideAdapter dressSlideAdapterPnt;

    private String mSelectedImagePath, fileNm;
    private boolean prmsn = false;      // File saving permission
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int SELECT_SHRT_CAM = 101;
    private static final int SELECT_SHRT_GALRY = 102;
    private static final int SELECT_PNT_CAM = 103;
    private static final int SELECT_PNT_GALRY = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bnd = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bnd.getRoot());

        customizeToolbar();
        checkPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        initializeComponent();
        initializeListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                prmsn = true;
                //Toast.makeText(MainActivity.this, "Storage permission request Granted", Toast.LENGTH_LONG).show();
            } else {
                prmsn = false;
                Toast.makeText(MainActivity.this, "Error, Storage permission request denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void customizeToolbar(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initializeComponent(){
        dressSlideShrt = new ArrayList<>();
        dressSlidePnt = new ArrayList<>();
        dressSlideShrt = db.getAllDresses("S");
        dressSlidePnt = db.getAllDresses("P");

        if(dressSlideShrt.size()>0){
            dressSlideAdapterShrt =new DressSlideAdapter(this, dressSlideShrt);
            bnd.vpShrt.setAdapter(dressSlideAdapterShrt);
        }
        if(dressSlidePnt.size()>0){
            dressSlideAdapterPnt =new DressSlideAdapter(this, dressSlidePnt);
            bnd.vpPnt.setAdapter(dressSlideAdapterPnt);
        }
        chkFabRndmEnable();
    }

    private void initializeListener(){
        bnd.fabShrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prmsn) {
                    mediaOptionDialog(true);
                }else{
                    checkPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                }
            }
        });

        bnd.fabPnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prmsn) {
                    mediaOptionDialog(false);
                }else{
                    checkPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                }
            }
        });

        bnd.fabRndmClcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int shrtCount = db.getDressCount("S");
                int pntCount = db.getDressCount("P");

                Random r = new Random();
                int rndmShrt=r.nextInt(shrtCount-0)+0, rndmPnt=r.nextInt(pntCount-0)+0;
                while (rndmShrt==bnd.vpShrt.getCurrentItem()){
                    rndmShrt=r.nextInt(shrtCount-0)+0;
                }
                while (rndmPnt==bnd.vpPnt.getCurrentItem()){
                    rndmPnt=r.nextInt(pntCount-0)+0;
                }
                bnd.vpShrt.setCurrentItem(rndmShrt);
                bnd.vpPnt.setCurrentItem(rndmPnt);
            }
        });

    }

    private void mediaOptionDialog(boolean shrt_slcted) {
        // if true shirt is selected, if false pant is selected
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.file_chosing_option_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // this is optional
        }
        LinearLayout llCam = dialog.findViewById(R.id.llCam);
        LinearLayout llGalry = dialog.findViewById(R.id.llGalry);

        llCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shrt_slcted){
                    startCamera(SELECT_SHRT_CAM);
                }else{
                    startCamera(SELECT_PNT_CAM);
                }
                dialog.dismiss();
            }
        });

        llGalry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shrt_slcted){
                    imageFromGallery(SELECT_SHRT_GALRY);
                }else{
                    imageFromGallery(SELECT_PNT_GALRY);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void imageFromGallery(int typ) {
        /* Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), typ); */
        Intent pickPhoto = new Intent(Intent. ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, typ);
    }

    public void startCamera(int typ) {
        //Create folder !exist
        File drctry = new File(Environment.getExternalStorageDirectory()+"/"+getApplicationContext().getString(R.string.app_fldr_name));
        if(!drctry.exists())    drctry.mkdir();
        fileNm = rndmImgNm();
        File file  = new File(Environment.getExternalStorageDirectory()+"/"+getApplicationContext().getString(R.string.app_fldr_name), fileNm);

        if (file != null) {
            // save image here
            Uri relativePath = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, relativePath);
            startActivityForResult(intent, typ);
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case SELECT_SHRT_CAM:
                    db.addDress(new DressSlideItem("S", fileNm));
                    dressSlideShrt.clear();
                    dressSlideShrt = db.getAllDresses("S");
                    dressSlideAdapterShrt = new DressSlideAdapter(this, dressSlideShrt);
                    bnd.vpShrt.setAdapter(dressSlideAdapterShrt);
                    bnd.vpShrt.setCurrentItem(db.getDressCount("S"));
                    chkFabRndmEnable();
                    break;
                case SELECT_SHRT_GALRY:
                    //mSelectedImagePath = getPath(MainActivity.this, data.getData());
                    mSelectedImagePath = FileUtil.getPath(MainActivity.this, data.getData());
                    System.out.println("mSelectedImagePath : " + mSelectedImagePath);
                    try {
                        File drctry = new File(Environment.getExternalStorageDirectory()+"/"+getApplicationContext().getString(R.string.app_fldr_name));
                        if(!drctry.exists())    drctry.mkdir();
                        if (drctry.canWrite()) {
                            fileNm = rndmImgNm();
                            String destinationImagePath= "/"+fileNm;   // this is the destination image path.
                            File source = new File(mSelectedImagePath);
                            File destination= new File(drctry, destinationImagePath);
                            if (source.exists()) {
                                FileChannel src = new FileInputStream(source).getChannel();
                                FileChannel dst = new FileOutputStream(destination).getChannel();
                                dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                                src.close();
                                dst.close();
                                // add file in DB
                                db.addDress(new DressSlideItem("S", fileNm));
                                dressSlideShrt.clear();
                                dressSlideShrt = db.getAllDresses("S");
                                dressSlideAdapterShrt = new DressSlideAdapter(this, dressSlideShrt);
                                bnd.vpShrt.setAdapter(dressSlideAdapterShrt);
                                bnd.vpShrt.setCurrentItem(db.getDressCount("S"));
                                chkFabRndmEnable();
                                // showing all files in DB
                                /* List<DressSlideItem> item = db.getAllDresses("S");
                                String allItem="";
                                for (DressSlideItem dm : item) {
                                    allItem = allItem + "Id: " + dm.getId() + " ,Type: " + dm.getDressTyp() + " ,Name: " + dm.getImgNm() + "\n";
                                }
                                Toast.makeText(getApplicationContext(), allItem, Toast.LENGTH_LONG).show(); */
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "SDCARD Not writable.", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e) {
                        System.out.println("Error :" + e.getMessage());
                    }
                    break;
                case SELECT_PNT_CAM:
                    db.addDress(new DressSlideItem("P", fileNm));
                    dressSlidePnt.clear();
                    dressSlidePnt = db.getAllDresses("P");
                    dressSlideAdapterPnt = new DressSlideAdapter(this, dressSlidePnt);
                    bnd.vpPnt.setAdapter(dressSlideAdapterPnt);
                    bnd.vpPnt.setCurrentItem(db.getDressCount("P"));
                    chkFabRndmEnable();
                    break;
                case SELECT_PNT_GALRY:
                    //mSelectedImagePath = getPath(MainActivity.this, data.getData());
                    mSelectedImagePath = FileUtil.getPath(MainActivity.this, data.getData());
                    System.out.println("mSelectedImagePath : " + mSelectedImagePath);
                    try {
                        File drctry = new File(Environment.getExternalStorageDirectory()+"/"+getApplicationContext().getString(R.string.app_fldr_name));
                        if(!drctry.exists())    drctry.mkdir();
                        if (drctry.canWrite()) {
                            fileNm = rndmImgNm();
                            String destinationImagePath= "/"+fileNm;   // this is the destination image path.
                            File source = new File(mSelectedImagePath);
                            File destination= new File(drctry, destinationImagePath);
                            if (source.exists()) {
                                FileChannel src = new FileInputStream(source).getChannel();
                                FileChannel dst = new FileOutputStream(destination).getChannel();
                                dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                                src.close();
                                dst.close();
                                // add file in DB
                                db.addDress(new DressSlideItem("P", fileNm));
                                dressSlidePnt.clear();
                                dressSlidePnt = db.getAllDresses("P");
                                dressSlideAdapterPnt = new DressSlideAdapter(this, dressSlidePnt);
                                bnd.vpPnt.setAdapter(dressSlideAdapterPnt);
                                bnd.vpPnt.setCurrentItem(db.getDressCount("P"));
                                chkFabRndmEnable();
                                // showing all files in DB
                                /*List<DressSlideItem> item = db.getAllDresses("P");
                                String allItem="";
                                for (DressSlideItem dm : item) {
                                    allItem = allItem + "Id: " + dm.getId() + " ,Type: " + dm.getDressTyp() + " ,Name: " + dm.getImgNm() + "\n";
                                }
                                Toast.makeText(getApplicationContext(), allItem, Toast.LENGTH_LONG).show(); */
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "SDCARD Not writable.", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e) {
                        System.out.println("Error :" + e.getMessage());
                    }
                    break;
            }
        }
    }

    private String rndmImgNm(){
        Random r = new Random();
        String ranLet="";

        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i <= 3; i++) {
            ranLet = ranLet + abc.charAt(r.nextInt(abc.length()));
        }

         return ranLet+String.valueOf(System.currentTimeMillis())+".jpg";
    }

    private void chkFabRndmEnable(){
        if(dressSlideShrt.size()>1 && dressSlidePnt.size()>1){          // If either shirt or pant is not added
            bnd.fabRndmClcn.setEnabled(true);
        }else{
            bnd.fabRndmClcn.setEnabled(false);
        }
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission }, requestCode);
            prmsn = false;
        } else {
            prmsn = true;
            //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

}