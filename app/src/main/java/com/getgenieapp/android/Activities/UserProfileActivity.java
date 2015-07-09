package com.getgenieapp.android.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.CustomViews.ProgressBar.LoadingViewFlat;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Extras.GraphicsUtil;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.Register;
import com.getgenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserProfileActivity extends GenieBaseActivity {
    @InjectView(R.id.orderdetails)
    Button orderdetails;
    @InjectView(R.id.name)
    EditText name;
    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.address)
    EditText address;
    @InjectView(R.id.userIcon)
    CircularButton userIcon;
    int radius;
    final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE = 1;
    final int PIC_CROP = 2;
    private Uri picUri;
    private final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Picture", "Cancel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);

        userIcon.setButtonColor(getResources().getColor(R.color.colorPrimary));
        userIcon.setShadowColor(getResources().getColor(R.color.colorPrimary));

        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);

        logging.LogV("User Profile Activity");
        name.setText("Genie Admin");
        email.setText("admin@getgenieapp.com");
        address.setText("India");
        radius = userIcon.getLayoutParams().width;
        getPicture();
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                picUri = data.getData();
                // carry out the crop operation
                performCrop();
            }
            // user is returning from cropping the image
            else if (requestCode == PIC_CROP) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                // retrieve a reference to the ImageView
                // display the returned cropped image
                GraphicsUtil graphicUtil = new GraphicsUtil();
                //picView.setImageBitmap(graphicUtil.getRoundedShape(thePic));
                userIcon.setImageBitmap(graphicUtil.getCircleBitmap(thePic, radius));
                setPicture();
            } else if (requestCode == PICK_IMAGE) {
                try {
                    ContentResolver resolver = getContentResolver();
                    Uri actualUri = data.getData();
                    List<String> uriPath = actualUri.getPathSegments();
                    long imageId = Long.parseLong(uriPath.get(uriPath.size() - 1));
                    Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail(resolver, imageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    //There is no thumb-nail with this Image
                    if (thumb == null) {
                        SnackBar snackBar = new SnackBar(this, "Failed to get thumbnail for our image.");
                        snackBar.show();

                        //so create thumb-nail from image itself
                        Cursor cursor = resolver
                                .query(actualUri,
                                        new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                                        null, null, null);
                        cursor.moveToFirst();
                        final String imageFilePath = cursor.getString(0);
                        cursor.close();
                        GraphicsUtil graphicUtil = new GraphicsUtil();
                        //picView.setImageBitmap(graphicUtil.getRoundedShape(thePic));
                        userIcon.setImageBitmap(graphicUtil.getCircleBitmap(this.createImageThumbnail(imageFilePath,
                                255, 255), radius));
                        setPicture();
                    }
                    //We got the thumb-nail from gallery, rotate if needed else use on ImageView
                    else {
                        String thumbPath = getFilePath(actualUri);
                        ExifInterface exif;
                        try {
                            exif = new ExifInterface(thumbPath);
                            int orientation = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION, 0);
                            Matrix matrix = new Matrix();
                            if (orientation == 6) {
                                matrix.postRotate(90);
                            } else if (orientation == 3) {
                                matrix.postRotate(180);
                            } else if (orientation == 8) {
                                matrix.postRotate(270);
                            }
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            bitmap = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(),
                                    thumb.getHeight(), matrix, true);
                            if (thumb != bitmap) {
                                thumb.recycle();
                                thumb = null;
                            }
                            GraphicsUtil graphicUtil = new GraphicsUtil();
                            userIcon.setImageBitmap(graphicUtil.getCircleBitmap(bitmap, radius));
                            setPicture();
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setPicture() {
        userIcon.setDrawingCacheEnabled(true);
        Bitmap bitmap = userIcon.getDrawingCache();
        try {
            FileOutputStream ostream = new FileOutputStream(DataFields.profilePicturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPicture() {
        if (new File(DataFields.profilePicturePath).exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(DataFields.profilePicturePath, options);
            GraphicsUtil graphicUtil = new GraphicsUtil();
            userIcon.setImageBitmap(graphicUtil.getCircleBitmap(bitmap, radius));
        }
    }

    @OnClick(R.id.locationButton)
    public void onCLickLocation() {
        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage(getString(R.string.gettinglocationinfo));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    Address adrs = listAddresses.get(0);
                    String _Location = "";
                    for (int i = 0; i < adrs.getMaxAddressLineIndex(); i++) {
                        _Location += adrs.getAddressLine(i) + " ";
                    }
                    address.setText(_Location);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SnackBar snackBar = new SnackBar(this, "Not able to access Location");
            snackBar.show();
        }
        progressBar.dismiss();
        progressBar.cancel();
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            SnackBar snackBar = new SnackBar(this, "Whoops - your device doesn't support the crop action!");
            snackBar.show();
        }
    }

    private Bitmap bitmap = null;

    public Bitmap createImageThumbnail(String imagePath, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
                / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
                / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }
        bmpFactoryOptions.inJustDecodeBounds = false;

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        bitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
        return bitmap;
    }

    private String getFilePath(Uri data) {
        String path = "";
        path = data.getPath();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(data, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    @OnClick(R.id.userIcon)
    public void onClickUserIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    try {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch (ActivityNotFoundException anfe) {
                        SnackBar snackBar = new SnackBar(UserProfileActivity.this, "Whoops - your device doesn't support capturing images!");
                        snackBar.show();
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickIntent, PICK_IMAGE);
                } else if (options[item].equals("Remove Picture")) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        userIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_white_48dp, UserProfileActivity.this.getTheme()));
                    } else {
                        userIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_white_48dp));
                    }
                    if (new File(DataFields.profilePicturePath).exists())
                        new File(DataFields.profilePicturePath).delete();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @OnClick(R.id.orderdetails)
    public void onClickOrderDetails() {
        logging.LogV("Open Order Details");
        startActivity(new Intent(this, OrderDetailsActivity.class));
    }

    @OnClick(R.id.orderdetailsbutton)
    public void onClickOrderDetailsButton() {
        logging.LogV("Open Order Details");
        startActivity(new Intent(this, OrderDetailsActivity.class));
    }

    @OnClick(R.id.update)
    public void onClickUpdate() {
        if (name.getText().toString().trim().length() > 0) {
            if (email.getText().toString().trim().length() > 0 && !utils.isValidEmail(email.getText().toString())) {
                SnackBar snackBar = new SnackBar(this, getString(R.string.entervalidemail));
                snackBar.show();
                return;
            }
            final ProgressDialog progressBar = new ProgressDialog(this);
            progressBar.setCancelable(true);
            progressBar.setMessage(getString(R.string.updatinguserinformation));
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();
            JSONObject json = new JSONObject();
            try {
                json.put("name", name.getText().toString());
                json.put("email", email.getText().toString());
                json.put("address", address.getText().toString());
                JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.UPDATEUSER, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressBar.dismiss();
                                progressBar.cancel();
                                if (response != null) {
                                    logging.LogV("Response ", response.toString());
                                    // todo implement response
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        SnackBar snackBar = new SnackBar(UserProfileActivity.this, getString(R.string.errorwhileupdatinguserinformation));
                        snackBar.show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                        return params;
                    }
                };

                genieApplication.addToRequestQueue(req);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SnackBar snackBar = new SnackBar(this, getString(R.string.entervalidinformation));
            snackBar.show();
        }
    }
}