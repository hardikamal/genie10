package com.getgenieapp.android.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getgenieapp.android.CustomViews.Adapters.CustomPlaceAdapter;
import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.Place.PlaceAutocompleteAdapter;
import com.getgenieapp.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

// Location Activity is used by user to share location to server and save fav locations for future usage.
// Pick places API is used to pick places (this PAY per use api is developed by google). Pick places API gives option to share current location, But i did my own implementation to
// cost associated with API.
// Result sends back to Chat activity
// Elements injected thru Butterknife.
// Recycler view is used to show fav places
// On click on fav place will share the location
// Adapter is CustomPlaceAdapter

public class LocationActivity extends GenieBaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    @InjectView(R.id.refreshLocation)
    CircularButton refreshLocation;
    @InjectView(R.id.location)
    CircularButton location;
    @InjectView(R.id.locationButton)
    Button locationButton;
    @InjectView(R.id.list)
    RecyclerView recyclerView;
    @InjectView(R.id.pickplaces)
    CircularButton pickPlaces;
    @InjectView(R.id.pickplacebutton)
    Button pickplacebutton;
    @InjectView(R.id.autocomplete_places)
    AutoCompleteTextView mAutocompleteView;
    @InjectView(R.id.save)
    CircularButton save;

    private HashMap<String, Object> mixpanelDataAdd = new HashMap<>();

    private int LOCATIONRESULT = 1;
    int PLACE_PICKER_REQUEST = 1;
    GoogleMap map;
    AlertDialog.Builder dialog;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(4.428586, 105.769779), new LatLng(37.059575, 65.230231));

    @Override
    protected void onStart() {
        super.onStart();
        mixPanelTimerStart(LocationActivity.class.getName());
        logging.LogI("On Start");
    }

    @Override
    protected void onDestroy() {
        logging.LogI("On Destroy");
        mixPanelTimerStop(LocationActivity.class.getName());
        mixPanelBuildHashMap("General Run " + LocationActivity.class.getName(), mixpanelDataAdd);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mixPanelBuild("Back Pressed from Location screen");
        mixpanelDataAdd.put("Pressed", "Back");
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        ButterKnife.inject(this);

        hideKeyboard(this);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        ArrayList<MessageValues> messageValues = dbDataSource.getAllFav();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomPlaceAdapter chatAdapter = new CustomPlaceAdapter(messageValues, this);
        recyclerView.setAdapter(chatAdapter);
        dialog = new AlertDialog.Builder(this);

        checkLocationStatus();

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(DataFields.small1000TimeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mixpanelDataAdd.put("Location", "Refresh");
                runOnUiThread(new Runnable() {
                    public void run() {
                        refreshLocation.performClick();
                    }
                });
            }
        }).start();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mAutocompleteView.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAutocompleteView.getText().toString().trim().length() > 0) {
                    save.setVisibility(View.VISIBLE);
                } else {
                    save.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        mAutocompleteView.setAdapter(mAdapter);

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mixpanelDataAdd.put("Pick Places", "Result on Activity");
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                if (String.valueOf(place.getAddress()).length() > 0) {
                    MessageValues messageValues = new MessageValues(3, String.valueOf(place.getAddress()), place.getLatLng().longitude, place.getLatLng().latitude);
                    showSaveLaterBox(messageValues);
                } else {
                    MessageValues messageValues = new MessageValues(3, getPlace(place), place.getLatLng().longitude, place.getLatLng().latitude);
                    showSaveLaterBox(messageValues);
                }
            }
        }
    }

    private String getPlace(Place place) {
        String _Location = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                Address adrs = listAddresses.get(0);

                for (int i = 0; i < adrs.getMaxAddressLineIndex(); i++) {
                    _Location += adrs.getAddressLine(i) + " ";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (_Location.length() == 0) {
            _Location = "Latitude : " + place.getLatLng().latitude + " Longitude : " + place.getLatLng().longitude;
        }
        mixpanelDataAdd.put("Return Location", _Location);
        return _Location;
    }


    private void checkLocationStatus() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            mixpanelDataAdd.put("Location", "Unavailable");
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.locationisoff));
            dialog.setPositiveButton(getString(R.string.opensettings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getString(R.string.goback), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    paramDialogInterface.cancel();
                    onBackPressed();
                }
            });
            dialog.show();
        } else {
            mixpanelDataAdd.put("Location", "Available");
        }
    }

    @OnClick(R.id.refreshLocation)
    public void onClickRefresh() {
        mixpanelDataAdd.put("Pressed", "Refresh Button");
        MessageValues messageValues = getLocation();
        LatLng currentLocation = new LatLng(messageValues.getLat(), messageValues.getLng());

        map.addMarker(new MarkerOptions().position(currentLocation)
                .title("Your Current Location:\n" + messageValues.getText()));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    @OnClick(R.id.pickplaces)
    public void onClickPickPlaces() {
        mixpanelDataAdd.put("Pressed", "Pick Places Button");
        pickplacebutton.performClick();
    }

    @OnClick(R.id.pickplacebutton)
    public void onClickPickPlaceButton() {
        mixpanelDataAdd.put("Pressed", "Picked Places Button");
        mixPanelBuild("Location Pick Places Clicked");
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.location)
    public void onClickLocation() {
        mixpanelDataAdd.put("Pressed", "Location Button");
        locationButton.performClick();
    }

    @OnClick(R.id.save)
    public void onClickSave() {
        mixpanelDataAdd.put("Pressed", "Save Button");
        if (mAutocompleteView.getText().toString().trim().length() > 0) {
            showSaveLaterBoxNoAlert(new MessageValues(3, "Location : " + mAutocompleteView.getText().toString(), 0.00, 0.00));
        }
    }

    @OnClick(R.id.search)
    public void onClickSearch() {
        mixpanelDataAdd.put("Pressed", "Search Button");
        mixPanelBuild("Search Location Button Clicked");
        mAutocompleteView.setVisibility(View.VISIBLE);
        mAutocompleteView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @OnClick(R.id.locationButton)
    public void onClickLocationButton() {
        mixpanelDataAdd.put("Pressed", "Share my Location Button");
        mixPanelBuild("Share My Location Button Clicked");
        MessageValues messageValues = getLocation();
        showSaveLaterBox(messageValues);
    }

    private void setResultBackToActivity(MessageValues messageValues) {
        Intent intent = new Intent();
        intent.putExtra("lat", messageValues.getLat());
        intent.putExtra("lng", messageValues.getLng());
        intent.putExtra("address", messageValues.getText());
        setResult(LOCATIONRESULT, intent);
        finish();
    }

    private void showSaveLaterBox(final MessageValues messageValues) {
        if (messageValues.getLat() != 0.0 || messageValues.getLng() != 0.0) {
            LayoutInflater inflater = this.getLayoutInflater();
            final View promptsView = inflater.inflate(R.layout.saveaddresslayout, null);
            final EditText saveas = (EditText) promptsView.findViewById(R.id.saveas);

            ((TextView) promptsView.findViewById(R.id.address)).setText("Address : " + messageValues.getText());

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setView(promptsView)
                    .setTitle("Save as Favorite Place")
                    .setNegativeButton("Save as Favorites", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            mixpanelDataAdd.put("Location", "Saved as a Fav");
                            mixPanelBuild("Save as Fav location button clicked");
                            if (saveas.getText().toString().trim().length() > 0 && !dbDataSource.CheckIfExists(saveas.getText().toString().trim())) {
                                dbDataSource.addFavNormal(new MessageValues(messageValues.get_id(), messageValues.getText(), messageValues.getLng()
                                        , messageValues.getLat(), saveas.getText().toString().trim()));
                                setResultBackToActivity(messageValues);
                            } else {
                                Crouton.makeText(LocationActivity.this, getString(R.string.gaveanametoplace), Style.ALERT).show();
                                showSaveLaterBox(messageValues);
                            }
                        }
                    })
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mixpanelDataAdd.put("Location", "Not Saved");
                            setResultBackToActivity(messageValues);
                        }
                    });
            alert.show();
        } else {
            mixpanelDataAdd.put("Location", "Invalid");
            dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(true);
            dialog.setMessage(getString(R.string.notabletogetyourlocation));
            dialog.setPositiveButton(getString(R.string.opensettings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    mixpanelDataAdd.put("Pressed", "Settings Button");
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getString(R.string.goback), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    mixpanelDataAdd.put("Pressed", "Cancel Button");
                    paramDialogInterface.cancel();
                    onBackPressed();
                }
            });
            dialog.show();
        }
    }

    private void showSaveLaterBoxNoAlert(final MessageValues messageValues) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View promptsView = inflater.inflate(R.layout.saveaddresslayout, null);
        final EditText saveas = (EditText) promptsView.findViewById(R.id.saveas);

        ((TextView) promptsView.findViewById(R.id.address)).setText("Address : " + messageValues.getText());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(promptsView)
                .setTitle("Save as Favorite Place")
                .setNegativeButton("Save as Favorites", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mixpanelDataAdd.put("Location", "Saved as a Fav");
                        if (saveas.getText().toString().trim().length() > 0 && !dbDataSource.CheckIfExists(saveas.getText().toString().trim())) {
                            dbDataSource.addFavNormal(new MessageValues(messageValues.get_id(), messageValues.getText(), messageValues.getLng()
                                    , messageValues.getLat(), saveas.getText().toString().trim()));
                            setResultBackToActivity(messageValues);
                        } else {
                            Crouton.makeText(LocationActivity.this, getString(R.string.gaveanametoplace), Style.ALERT).show();
                            showSaveLaterBox(messageValues);
                        }
                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mixpanelDataAdd.put("Location", "Not Saved");
                        setResultBackToActivity(messageValues);
                    }
                });
        alert.show();
    }

    private MessageValues getLocation() {
        double longitude = 0.00;
        double latitude = 0.00;
        String _Location = "";

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            longitude = locations.getLongitude();
            latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    Address adrs = listAddresses.get(0);

                    for (int i = 0; i < adrs.getMaxAddressLineIndex(); i++) {
                        _Location += adrs.getAddressLine(i) + " ";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Crouton.makeText(LocationActivity.this, getString(R.string.notabletoacceslocation), Style.ALERT).show();
        }
        return new MessageValues(3, _Location, longitude, latitude);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            MessageValues messageValues = new MessageValues(3, String.valueOf(place.getAddress()), place.getLatLng().longitude, place.getLatLng().latitude);
            LatLng currentLocation = new LatLng(messageValues.getLat(), messageValues.getLng());

            map.addMarker(new MarkerOptions().position(currentLocation)
                    .title("Your Current Location:\n" + messageValues.getText()));

            // Move the camera instantly to hamburg with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
            showSaveLaterBox(messageValues);
            mixpanelDataAdd.put("Google Location Search", "Returned Results");
            places.release();
        }
    };

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            mixpanelDataAdd.put("Google Location Search", "Result Clicked");
            mixPanelBuild("Google Location Service Resulted shared");
        }
    };
}