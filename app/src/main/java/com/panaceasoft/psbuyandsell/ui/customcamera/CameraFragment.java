package com.panaceasoft.psbuyandsell.ui.customcamera;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.binding.FragmentDataBindingComponent;
import com.panaceasoft.psbuyandsell.databinding.FragmentCameraBinding;
import com.panaceasoft.psbuyandsell.ui.common.PSFragment;
import com.panaceasoft.psbuyandsell.utils.AutoClearedValue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends PSFragment implements SurfaceHolder.Callback{


    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private boolean camCondition = false;
    private static final int REQUEST_GET_ACCOUNT = 112;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String imagePath;
    private ProgressDialog progressDialog;

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    @VisibleForTesting
    private AutoClearedValue<FragmentCameraBinding> binding;

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentCameraBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.message__loading));
        progressDialog.setCancelable(false);

        surfaceHolder = binding.get().surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        Camera.PictureCallback mPictureCallback = (data, c) -> {

            Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
            realImage = rotate(realImage);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Uri uri;
            String dataImagePath;
            uri  = convertBitmapToUri(realImage);

            dataImagePath = convertToImagePath(uri, filePathColumn);

            navigationController.navigateBackToItemEntryFromCustomCamera(getActivity(),dataImagePath, uri);

            progressDialog.cancel();
            if(getActivity() != null){
                getActivity().finish();
            }
        };

        binding.get().captureButton.setOnClickListener(v -> {
            try {
                camera.takePicture(null, null, mPictureCallback);
                progressDialog.show();
                binding.get().captureButton.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Uri convertBitmapToUri(Bitmap bitmapImage){
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        OutputStream fos = null;
//        File image;
        Uri imageUri;
//        String filePath;
        String name = System.currentTimeMillis() + ".jpg";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(fos != null) {
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            }
            try {
                Objects.requireNonNull(fos).close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
//            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//            image = new File(imagesDir, name);
//            try {
//                fos = new FileOutputStream(image);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
////            imageUri = image.toURI();
//            imagePath = image.getPath();
////            filePath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmapImage, "Title", null);
//            imageUri = Uri.parse(imagePath);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            String path = "";
            if(getActivity() != null) {
                path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmapImage, "Title", null);
            }
            return Uri.parse(path);
        }


        return imageUri;

    }

    private String convertToImagePath(Uri selectedImage, String[] filePathColumn) {

        if (getActivity() != null && selectedImage != null) {
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);

                cursor.close();
            }
        }
        return imagePath;
    }

    private static Bitmap rotate(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        try {
            // stop the camera
            if (camCondition) {
                camera.stopPreview();
                camCondition = false;
            }

            setCameraParameter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setCameraParameter() {
        // condition to check whether your device have camera or not
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE); //applying effect on camera
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();

                camCondition = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        try {
            camera.stopPreview();
            camera.release();
            camera = null;
            camCondition = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = 0;

        if(getContext() != null) {
            result = ContextCompat.checkSelfPermission(getContext(), CAMERA);
        }
        return result == PackageManager.PERMISSION_GRANTED ;

    }

    private void requestPermission() {
        if(getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{GET_ACCOUNTS, CAMERA}, REQUEST_GET_ACCOUNT);
            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}
