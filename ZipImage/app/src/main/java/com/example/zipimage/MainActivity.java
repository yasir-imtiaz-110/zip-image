package com.example.zipimage;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {
    private static int BUFFER_SIZE = 8192;
    Button uploadImgBtn;
    ImageView imageView;
    private static final String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadImgBtn = findViewById(R.id.uploadImgBtnID);
        imageView = findViewById(R.id.imageView);

        //permission to write and read from external storage
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1100 && resultCode == RESULT_OK) {
                Uri selectedUri = data.getData();
//                String requiredValue = data.getStringExtra("key");
//                String[] proj = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(selectedUri, proj, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//
//                String path = new File(cursor.getString(column_index)).getAbsolutePath();
////                ExifInterface exif = new ExifInterface(path);
////                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

                imageView.setImageURI(selectedUri);
                String root = Environment.getExternalStorageDirectory().toString();
                File defaultFile = new File(root + "/PARSL_IMAGES");
                if (!defaultFile.exists()) {
                    defaultFile.mkdirs();
                }
                if(defaultFile.exists()){
                    Toast.makeText(this, "Folder Exists", Toast.LENGTH_SHORT).show();
                }


                String absolutPathString = getPath(MainActivity.this, selectedUri);
//                String resource = getClass().getClassLoader().getResource("PARSL_IMAGES").toURI().getPath();
                String myResource = root+"/PARSL_IMAGES/";
                copyFile(absolutPathString,myResource);
//                zip(absolutePathArray,resource);
//                String myArray = getRealPathFromURI(selectedUri);
//                String myArray = convertMediaUriToPath(selectedUri);

                String[] absolutePathArray = new String[1];
                absolutePathArray[0] = absolutPathString;
//                String absolutePathArray [] = {absolutPathString};
//                Bitmap thumbnail = BitmapFactory.decodeFile(path.getAbsolutePath());
//                imageView.setImageBitmap(thumbnail);

//                File srcFile = new File(absolutPathString);
//                File zipFile = new File("/main/res/raw/myzipfile");
//                File zipFile = new File(Environment.getExternalStorageDirectory()+"/myZip");
//                if(!zipFile.exists()) {
//                    Toast.makeText(getApplicationContext(),"Directory does not exist, create it",
//                            Toast.LENGTH_LONG).show();
//                }
//                else if(zipFile.exists()) {
//                    Toast.makeText(this,"Directory Exists",
//                            Toast.LENGTH_LONG).show();
//                }
//                zipFile(srcFile, zipFile);
//                output1(absolutPathString, "main/res/raw");
//                Toast.makeText(MainActivity.this, "The absolute path is " + absolutePathArray[0], Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, absolutePathArray.getClass().getSimpleName(), Toast.LENGTH_LONG).show();

            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Exception1",
                    Toast.LENGTH_SHORT).show();
            Log.e(tag, "The exception caught while executing the process. (error1)", ex);
        }
    }


//    public String getRealPathFromURI(Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }


//    public String convertMediaUriToPath(Uri uri) {
//        String [] proj={MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, proj,  null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String path = cursor.getString(column_index);
//        cursor.close();
//        return path;
//    }

    //code to get Absolute path
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = true;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void zip(String[] files, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    public void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        WindowManager.LayoutParams LOGGER;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

//            LOGGER.debug("Copied file to " + outputPath);
//            Toast.makeText(getContext().this, "File Copies Successfully", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "File Copies Successfully",Toast.LENGTH_SHORT).show();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void zipFile(File srcFile, File zipFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile);
             ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zos.putNextEntry(new ZipEntry(srcFile.getName()));
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
        }
    }

    public static void output1(String filepath, String zipfilepath) throws Exception {
        // 1.使用 File 类绑定一个文件
        // 定义要压缩的文件
        File file = new File(filepath);
        // 定义压缩文件名称
        File zipFile = new File(zipfilepath);

        // 2.把 File 对象绑定到流对象上
        InputStream input = new FileInputStream(file);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));

        // 3.进行读或写操作
        zipOut.putNextEntry(new ZipEntry(file.getName()));
        zipOut.setComment("This is a zip file.");
        int temp = 0;
        while ((temp = input.read()) != -1) { // 读取内容
            zipOut.write(temp); // 压缩输出


        }
    }
}