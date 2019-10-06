package com.example.subtitulo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Image extends AppCompatActivity {

    String selectedImagePath;
    EditText imgPath;
    Uri selectedImage = null;
    ImageView mimageView,resultView;
    Bitmap b = null;
    Bitmap bitmap = null;
    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private final int PICK_IMAGE_CAMERA = 1;
    String time;
    String encodedImage = null;
    CameraPhoto cameraPhoto;
    //final int CAMERA_REQUEST = 13323;
    public int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();
        mimageView = (ImageView) this.findViewById(R.id.imageviewer);
        resultView = (ImageView) this.findViewById(R.id.resultView);
        StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder1.build());
        mimageView = (ImageView) this.findViewById(R.id.imageviewer);

        cameraPhoto = new CameraPhoto(getApplicationContext());
    }

    public void connectServer(View v){
        EditText ipv4AddressView = findViewById(R.id.IPAddress);
        String ipv4Address = "192.168.43.104";
        EditText portNumberView = findViewById(R.id.portNumber);
        String portNumber = portNumberView.getText().toString();
        //String postUrl= "http://27419d2f.ngrok.io";
        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/";
        Toast.makeText(getApplicationContext(),postUrl,Toast.LENGTH_SHORT).show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        TextView responseText = findViewById(R.id.responseText);
        responseText.setText("Please wait ...");

        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        resultView.setVisibility(View.VISIBLE);
                        resultView.setImageBitmap(bitmap);
                        try {
                            responseText.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server");
                    }
                });
            }


        });
    }

    public void selectImage(View v) {

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);

    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case 0: if (resCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                selectedImagePath = getPath(getApplicationContext(), uri);
                imgPath = findViewById(R.id.imgPath);
                imgPath.setVisibility(View.VISIBLE);
                imgPath.setText(selectedImagePath);
                Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();
            }
            case 1:
                mimageView.setVisibility(View.VISIBLE);
                super.onActivityResult(reqCode, resCode, data);
                try {
                    if (selectedImage != null) {
                        if (selectedImage.toString().startsWith("file:")) {

                            File DKNY = new File(selectedImage.getPath());
                            selectedImagePath = selectedImage.getPath();
                            InputStream in = null;
                            try {
                                final int IMAGE_MAX_SIZE = 1200000;
                                in = getContentResolver().openInputStream(selectedImage);
                                // Decode image size
                                BitmapFactory.Options o = new BitmapFactory.Options();
                                o.inJustDecodeBounds = true;
                                BitmapFactory.decodeStream(in, null, o);
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                int scale = 1;
                                while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                                        IMAGE_MAX_SIZE) {
                                    scale++;
                                }

                                in = getContentResolver().openInputStream(selectedImage);
                                if (scale > 1) {
                                    scale--;
                                    // scale to max possible inSampleSize that still yields an image
                                    // larger than target
                                    o = new BitmapFactory.Options();
                                    o.inSampleSize = scale;
                                    b = BitmapFactory.decodeStream(in, null, o);

                                    // resize to desired dimensions
                                    int height = b.getHeight();
                                    int width = b.getWidth();

                                    double y = Math.sqrt(IMAGE_MAX_SIZE
                                            / (((double) width) / height));
                                    double x = (y / height) * width;

                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                                            (int) y, true);
                                    b.recycle();
                                    b = scaledBitmap;

                                    System.gc();
                                } else {
                                    b = BitmapFactory.decodeStream(in);
                                }
                                in.close();
                                mimageView.setImageBitmap(b);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                b.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                //String image_byte=String.valueOf(byteArray);

                                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void takeImageFromCamera(View view) {
        //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(cameraIntent, CAMERA_REQUEST);

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/KJHACK/";
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";

        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException ignored) {

        }

        selectedImage = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
        startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
    }
    public void uploadImage(View view) {

        Toast.makeText(getApplicationContext(), "Upload Success!", Toast.LENGTH_SHORT).show();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        //String image_byte=String.valueOf(byteArray);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }


    }

    public void share(View v){
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        final File photoFile = new File(selectedImagePath);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
        startActivity(Intent.createChooser(shareIntent, "Share image using"));

    }

}