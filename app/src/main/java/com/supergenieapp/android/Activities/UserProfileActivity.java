package com.supergenieapp.android.Activities;

import android.app.Activity;
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
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.supergenieapp.android.CustomViews.Button.ButtonRectangle;
import com.supergenieapp.android.CustomViews.Button.CircularButton;
import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Extras.GraphicsUtil;
import com.supergenieapp.android.GenieBaseActivity;
import com.supergenieapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class UserProfileActivity extends GenieBaseActivity {
    @InjectView(R.id.name)
    EditText name;
    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.address)
    EditText address;
    @InjectView(R.id.number)
    EditText number;
    @InjectView(R.id.userIcon)
    CircularButton userIcon;
    @InjectView(R.id.update)
    ButtonRectangle update;
    int radius;

    final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE = 2;

    private final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Picture", "Cancel"};
    boolean canClose = false;
    HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
        mixPanelTimerStart(UserProfileActivity.class.getName());
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        mixPanelTimerStop(UserProfileActivity.class.getName());
        mixPanelBuildHashMap("General Run " + UserProfileActivity.class.getName(), mixpanelDataAdd);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);
        if (getIntent().getExtras() != null) {
            canClose = getIntent().getBooleanExtra("canclose", false);
        }

        update.setTextColor(getResources().getColor(R.color.white));
        userIcon.setButtonColor(getResources().getColor(R.color.colorPrimary));
        userIcon.setShadowColor(getResources().getColor(R.color.colorPrimary));

        getWindow().setBackgroundDrawableResource(R.drawable.wallpaper_wallpaper);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        radius = userIcon.getLayoutParams().width;
        getUserData();
        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    private void getUserData() {
        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage(getString(R.string.updatinguserinformation));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        mixpanelDataAdd.put("Server Call", "Get User Profile");
        mixPanelTimerStart(DataFields.getServerUrl() + DataFields.USERPROFILE);
        JsonObjectRequest req = new JsonObjectRequest(DataFields.getServerUrl() + DataFields.USERPROFILE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        progressBar.dismiss();
                        progressBar.cancel();
                        mixPanelTimerStop(DataFields.getServerUrl() + DataFields.USERPROFILE);
                        mixpanelDataAdd.put("Server Call", "User data Success");
                        mixPanelTimerStop(DataFields.getServerUrl() + DataFields.USERPROFILE);
                        try {
                            if (response.has("phone")) {
                                number.setText(response.getString("phone"));
                            }
                            if (response.has("name")) {
                                name.setText(response.getString("name"));
                            }
                            if (response.has("email") && response.getString("email") != null && response.getString("email").length() > 0) {
                                email.setText(response.getString("email"));
                            }
                            if (response.has("address") && response.getString("address") != null && response.getString("address").length() > 0) {
                                address.setText(response.getString("address"));
                            }
                            if (new File(DataFields.profilePicturePath).exists()) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = BitmapFactory.decodeFile(DataFields.profilePicturePath, options);
                                GraphicsUtil graphicUtil = new GraphicsUtil();
                                userIcon.setImageBitmap(graphicUtil.getCroppedBitmap(bitmap, radius));
                                setPicture();
                            } else if (response.has("image_url") && response.getString("image_url") != null && response.getString("image_url").length() > 0) {
                                new AsyncTask<String, Void, Bitmap>() {
                                    @Override
                                    protected Bitmap doInBackground(String... url) {
                                        String imageLink = null;
                                        Bitmap bitmap = null;
                                        if (url[0] != null) {
                                            try {
                                                bitmap = BitmapFactory.decodeStream(new URL(imageLink).openStream());
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return bitmap;
                                    }

                                    @Override
                                    protected void onPostExecute(Bitmap res) {
                                        userIcon.setImageBitmap(res);
                                    }
                                }.execute(response.getString("image_url"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                progressBar.cancel();
                mixPanelTimerStop(DataFields.getServerUrl() + DataFields.USERPROFILE);
                mixpanelDataAdd.put("Server Call", "Users Server 500 Error");
                mixPanelBuild(DataFields.getServerUrl() + DataFields.USERPROFILE + " 500 Error");
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                return params;
            }
        };
        genieApplication.addToRequestQueue(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mixpanelDataAdd.put("Pressed", "Home/Back Menu");
                mixPanelBuild("Home/Back Menu Pressed");
                onBackPressed();
                return true;
            case R.id.action_previous_orders:
                mixpanelDataAdd.put("Pressed", "Previous Orders Menu");
                mixPanelBuild("Previous Orders Menu Pressed");
                Intent profileIntent = new Intent(this, OrderDetailsActivity.class);
                profileIntent.putExtra("canclose", true);
                startActivity(profileIntent);
                if (canClose)
                    finish();
                return true;
            case R.id.action_share:
                mixpanelDataAdd.put("Pressed", "Share Menu");
                mixPanelBuild("Profile Share Pressed");
                String shareBody = getString(R.string.bodytext);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.trygenie));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareus)));
                return true;
            case R.id.action_contact:
                mixpanelDataAdd.put("Pressed", "Contact us");
                mixPanelBuild("Profile Contact us Pressed");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.supportemailaddress), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subjecttoemail));
                emailIntent.putExtra(Intent.EXTRA_TEXT, utils.getDeviceInformationFormEmail());
                startActivity(Intent.createChooser(emailIntent, getString(R.string.sendemail)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            deletePicture();
            // user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                mixpanelDataAdd.put("Pressed", "Take a picture");
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                ThumbnailUtils.extractThumbnail(bitmap, radius * 2, radius * 2);
                GraphicsUtil graphicUtil = new GraphicsUtil();
                userIcon.setImageBitmap(graphicUtil.getCroppedBitmap(bitmap, radius));
                try {
                    FileOutputStream ostream = new FileOutputStream(DataFields.profilePicturePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == PICK_IMAGE) {
                mixpanelDataAdd.put("Pressed", "Pick picture");
                ContentResolver resolver = getContentResolver();
                Uri actualUri = data.getData();
                List<String> uriPath = actualUri.getPathSegments();
                if (uriPath.size() > 0) {
                    if (uriPath.get(uriPath.size() - 1).matches("[0-9]+")) {
                        long imageId = Long.parseLong(uriPath.get(uriPath.size() - 1));
                        Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail(resolver, imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
                        //There is no thumb-nail with this Image
                        if (thumb != null) {
                            Cursor cursor = resolver
                                    .query(actualUri,
                                            new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                                            null, null, null);
                            cursor.moveToFirst();
                            final String imageFilePath = cursor.getString(0);
                            cursor.close();
                            GraphicsUtil graphicUtil = new GraphicsUtil();
                            //picView.setImageBitmap(graphicUtil.getRoundedShape(thePic));
                            userIcon.setImageBitmap(graphicUtil.getCroppedBitmap(this.createImageThumbnail(imageFilePath,
                                    255, 255), radius));
                            try {
                                FileOutputStream ostream = new FileOutputStream(DataFields.profilePicturePath);
                                thumb.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
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
                                Bitmap bitmap = null;
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
                                userIcon.setImageBitmap(graphicUtil.getCroppedBitmap(bitmap, radius));
                                try {
                                    FileOutputStream ostream = new FileOutputStream(DataFields.profilePicturePath);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                    ostream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        // TODO: 8/3/2015
                        Crouton.makeText(UserProfileActivity.this, getString(R.string.imagefailedtodeliver), Style.INFO, R.id.body).show();
                    }
                } else {
                    Crouton.makeText(UserProfileActivity.this, getString(R.string.imagefailedtodeliver), Style.INFO, R.id.body).show();
                }
            }
        }
    }

    @OnClick(R.id.userIcon)
    public void onClickUserIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mixPanelBuild("User Profile Picture Clicked " + options[item]);
                if (options[item].equals("Take Photo")) {
                    try {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch (ActivityNotFoundException anfe) {
                        Crouton.makeText(UserProfileActivity.this, getString(R.string.devicedoesnotsupportmessage), Style.INFO, R.id.body).show();
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickIntent, PICK_IMAGE);
                } else if (options[item].equals("Remove Picture")) {
                    deletePicture();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void deletePicture() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            userIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_white_48dp, UserProfileActivity.this.getTheme()));
        } else {
            userIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_white_48dp));
        }
        if (new File(DataFields.profilePicturePath).exists())
            new File(DataFields.profilePicturePath).delete();
    }

    @OnClick(R.id.update)
    public void onClickUpdate() {
        mixpanelDataAdd.put("Pressed", "Update");
        if (name.getText().toString().trim().length() > 0) {
            if (email.getText().toString().trim().length() > 0 && !utils.isValidEmail(email.getText().toString())) {
                Crouton.makeText(UserProfileActivity.this, getString(R.string.entervalidemail), Style.INFO, R.id.body).show();
                return;
            }
            mixPanelBuild("User Updated Profile");
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
                        progressBar.dismiss();
                        progressBar.cancel();
                        Crouton.makeText(UserProfileActivity.this, getString(R.string.errorwhileupdatinguserinformation), Style.INFO, R.id.body).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("x-access-token", sharedPreferences.getString(DataFields.TOKEN, ""));
                        return params;
                    }
                };
                genieApplication.addToRequestQueue(req);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Crouton.makeText(UserProfileActivity.this, getString(R.string.entervalidinformation), Style.INFO, R.id.body).show();
        }
    }

    private void setPicture() {
        mixPanelBuild("User Updated Profile picture");
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

    @OnClick(R.id.locationButton)
    public void onCLickLocation() {
        mixPanelBuild("User Updated Profile Location");
        mixpanelDataAdd.put("Pressed", "Location Button to update user location");
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
            Crouton.makeText(UserProfileActivity.this, getString(R.string.notabletoaccessthelocation), Style.INFO, R.id.body).show();
        }
        progressBar.dismiss();
        progressBar.cancel();
    }

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
        Bitmap bitmap = null;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        bitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
        return bitmap;
    }

    private String getFilePath(Uri data) {
        String path = data.getPath();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}