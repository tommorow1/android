package com.example.bloold.buildp.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.bloold.buildp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by bloold on 25.10.17.
 */

public class MediaFilePicker {
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        }
    };

    public static final String EXTRA_CUR_FILE_PATH = "EXTRA_CUR_FILE_PATH";

    @Retention(SOURCE)
    @IntDef({TYPE_PHOTO, TYPE_VIDEO})
    public @interface MediaFileType {

    }

    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_VIDEO = 2;

    private static final int REQUEST_IMAGE_PICK = 253;
    private static final int REQUEST_CAMERA_CAPTURE = 254;
    private static final int REQUEST_PICK_GALLERY = 255;

    private static final String FILE_IMAGE_EXTENSION = ".jpg";
    private static final String FILE_VIDEO_EXTENSION = ".mp4";

    private final WeakReference<Activity> activity;
    private final WeakReference<OnFilePickerListener> listener;
    //private Fragment fragment;
    String mCurrentFilePath;

    @MediaFileType
    private int mediaFileType;

    public MediaFilePicker(Activity activity, OnFilePickerListener listener) {
        this(activity, listener, TYPE_PHOTO, null);
    }

    public MediaFilePicker(Activity activity, OnFilePickerListener listener, Bundle savedState) {
        this(activity, listener, TYPE_PHOTO, savedState);
    }

    public MediaFilePicker(Activity activity, OnFilePickerListener listener, @MediaFileType int mediaFileType, Bundle savedState) {
        this.activity = new WeakReference<>(activity);
        this.listener = new WeakReference<>(listener);
        this.mediaFileType = mediaFileType;
        if (savedState != null) {
            mCurrentFilePath = savedState.getString(EXTRA_CUR_FILE_PATH);
        }
    }

    public Bundle saveState() {
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CUR_FILE_PATH, mCurrentFilePath);
        return bundle;
    }

    /**
     * Sets the fragment that contains this control. This allows the picker to be embedded inside a
     * Fragment, and will allow the fragment to receive the
     * {@link Fragment#onActivityResult(int, int, Intent) onActivityResult}
     * call rather than the Activity.
     *
     * @param fragment the android.support.v4.app.Fragment that contains this control
     */
    public void setFragment(Fragment fragment) {
        //this.fragment = fragment;
    }

    public boolean requestCameraIntent(@MediaFileType int mediaFileType) {
        this.mediaFileType = mediaFileType;
        return requestCameraIntent();
    }

    /**
     * Start activity for result to capture image from camera
     *
     * @return true if intent successfully dispatched, false - otherwise
     */
    public boolean requestCameraIntent() {
        final Intent imageCaptureIntent = new Intent(getCameraIntentAction());
        File file;
        try {
            file = createTempFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            // TODO: 06.02.2016 maybe return errorCodes?
            return false;
        }
        if (file != null) {
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(activity.get(), activity.get().getApplicationContext().getPackageName() + ".provider", file));
            if (imageCaptureIntent.resolveActivity(activity.get().getPackageManager()) == null) {
                return false;
            }
            startActivityForResult(imageCaptureIntent, REQUEST_CAMERA_CAPTURE);
            return true;
        }
        return false;
    }

    private String getCameraIntentAction() {
        switch (mediaFileType) {
            case TYPE_VIDEO:
                return MediaStore.ACTION_VIDEO_CAPTURE;
            default:
                return MediaStore.ACTION_IMAGE_CAPTURE;
        }
    }

    public boolean requestGalleryIntent(@MediaFileType int mediaFileType) {
        this.mediaFileType = mediaFileType;
        return requestGalleryIntent();
    }

    public boolean requestGalleryIntent() {
        final Intent selectIntent;
        switch (mediaFileType) {
            case TYPE_VIDEO:
                selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
                selectIntent.setType("video/*");
                break;
            default:
                selectIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        //final Intent selectIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (selectIntent.resolveActivity(activity.get().getPackageManager()) != null) {
            startActivityForResult(selectIntent, REQUEST_PICK_GALLERY);
            return true;
        } else {
            return false;
        }
    }

    private void startActivityForResult(final Intent intent, final int requestCode) {
        if (activity != null) {
            activity.get().startActivityForResult(intent, requestCode);
        } else {
            final Activity activity = this.activity.get();
            if (activity != null) {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    private String generateFileName() {
        switch (mediaFileType) {
            case TYPE_VIDEO:
                return "MP4_" + DATE_FORMATTER.get().format(new Date());
            default:
                return "JPEG_" + DATE_FORMATTER.get().format(new Date());
        }
    }


    /**
     * Create a temp file, which will be user for saving image
     *
     * @return file
     * @throws IOException if any error has been occurred
     */
    private File createTempFile() throws IOException {
        final String dirType;
        final String extension;
        switch (mediaFileType) {
            case TYPE_VIDEO:
                dirType = Environment.DIRECTORY_MOVIES;
                extension = FILE_VIDEO_EXTENSION;
                break;
            default:
                dirType = Environment.DIRECTORY_PICTURES;
                extension = FILE_IMAGE_EXTENSION;
        }
        final File storageDir = new File(Environment.getExternalStoragePublicDirectory(dirType), activity.get().getString(R.string.app_name));
        //create dirs if not exist
        storageDir.mkdirs();
        final File imageFile = File.createTempFile(generateFileName(), extension, storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentFilePath = imageFile.getAbsolutePath();
        return imageFile;
    }


    private void galleryAddPic(File file) {
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileContentUri = Uri.fromFile(file); // With 'permFile' being the File objectFromRealm
        mediaScannerIntent.setData(fileContentUri);
        activity.get().sendBroadcast(mediaScannerIntent); // With 'this' being the context, e.g. the activity
		/*MediaScannerConnection.scanFile(activity.process(),
	            new String[]{mCurrentFilePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalRfStorage", "-> uri=" + uri);
                    }
                });*/
    }

    public boolean onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return false;
        }
        switch (requestCode) {
            case REQUEST_CAMERA_CAPTURE: {
                final OnFilePickerListener pickerListener = listener.get();
                Log.d("filePisk", "REQUEST_CAMERA_CAPTURE" + pickerListener.toString());
                if (pickerListener != null) {
                    File file = new File(mCurrentFilePath);
                    pickerListener.onFilePicked(file);
                    if (mediaFileType == TYPE_PHOTO) {
                        galleryAddPic(file);
                    }
                }
            }
            return true;
            case REQUEST_PICK_GALLERY: {
                try {
                    new SaveFileTask(listener, createTempFile(), activity).execute(data.getData());
                    Log.d("filePisk", "REQUEST_PICK_GALLERY");
                } catch (IOException e) {
                    // TODO: 19.02.16 call faile
                }
            }
            return true;
        }
        return false;
    }

    public int getMediaFileType() {
        return mediaFileType;
    }

    public void setMediaFileType(int mediaFileType) {
        this.mediaFileType = mediaFileType;
    }


    public interface OnFilePickerListener {
        void onFilePicked(File file);
    }

    static class SaveFileTask extends AsyncTask<Uri, Void, File> {
        private static final String TAG = SaveFileTask.class.getSimpleName() + "_debug";
        private final WeakReference<OnFilePickerListener> listener;
        private final WeakReference<Activity> activity;
        private File destination;

        public SaveFileTask(WeakReference<OnFilePickerListener> listener, File imageFile, WeakReference<Activity> activity) {
            this.listener = listener;
            this.destination = imageFile;
            this.activity = activity;
        }

        @Override
        protected File doInBackground(Uri... params) {

            final InputStream inputStream;
            try {
                inputStream = activity.get().getContentResolver().openInputStream(params[0]);
            } catch (FileNotFoundException e) {
                return null;
            }
            FileOutputStream fo = null;
            try {
                fo = new FileOutputStream(destination);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    fo.write(buffer, 0, read);
                }
            } catch (FileNotFoundException fileNotFoundE) {
                Log.e(TAG, "Error: Destination file not found", fileNotFoundE);
            } catch (IOException ioE) {
                Log.e(TAG, "Error: IOException", ioE);
            } finally {
                try {
                    if (fo != null) {
                        fo.close();
                    }
                } catch (IOException ignored) {

                }
            }
            return destination;
        }

        @Override
        protected void onPostExecute(File file) {
            final OnFilePickerListener pickerListener = listener.get();

            Log.d("filePiskedExecute", file.getName().toString());

            if (pickerListener != null) {
                pickerListener.onFilePicked(file);
            }
        }
    }
}


