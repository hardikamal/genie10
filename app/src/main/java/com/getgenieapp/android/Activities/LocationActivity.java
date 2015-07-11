package com.getgenieapp.android.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getgenieapp.android.CustomViews.Adapters.CustomChatAdapter;
import com.getgenieapp.android.CustomViews.Adapters.CustomPlaceAdapter;
import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.Database.DBDataSource;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LocationActivity extends GenieBaseActivity {
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

    private int LOCATIONRESULT = 1;
    int PLACE_PICKER_REQUEST = 1;
    GoogleMap map;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        return _Location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        ButterKnife.inject(this);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        ArrayList<MessageValues> messageValues = dbDataSource.getAllFav();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomPlaceAdapter chatAdapter = new CustomPlaceAdapter(messageValues, this);
        recyclerView.setAdapter(chatAdapter);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(DataFields.small1000TimeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        refreshLocation.performClick();
                    }
                });
            }
        }).start();

        fontChangeCrawlerRegular.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.refreshLocation)
    public void onClickRefresh() {
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
        pickplacebutton.performClick();
    }

    @OnClick(R.id.pickplacebutton)
    public void onClickPickPlaceButton() {
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
        locationButton.performClick();
    }

    @OnClick(R.id.locationButton)
    public void onClickLocationButton() {
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
        LayoutInflater inflater = this.getLayoutInflater();
        final View promptsView = inflater.inflate(R.layout.saveaddresslayout, null);
        final EditText saveas = (EditText) promptsView.findViewById(R.id.saveas);

        ((TextView) promptsView.findViewById(R.id.address)).setText("Address : " + messageValues.getText());
        ((TextView) promptsView.findViewById(R.id.lat)).setText("Latitude : " + String.valueOf(messageValues.getLat()));
        ((TextView) promptsView.findViewById(R.id.lng)).setText("Longitude : " + String.valueOf(messageValues.getLng()));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(promptsView)
                .setTitle("Save As")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (saveas.getText().toString().trim().length() > 0 && !dbDataSource.CheckIfExists(saveas.getText().toString().trim())) {
                            dbDataSource.addFavNormal(new MessageValues(messageValues.get_id(), messageValues.getText(), messageValues.getLng()
                                    , messageValues.getLat(), saveas.getText().toString().trim()));
                            setResultBackToActivity(messageValues);
                        } else {
                            SnackBar snackBar = new SnackBar(LocationActivity.this, getString(R.string.gaveanametoplace));
                            snackBar.show();
                            showSaveLaterBox(messageValues);
                        }
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
            SnackBar snackBar = new SnackBar(this, "Not able to access Location");
            snackBar.show();
        }
        return new MessageValues(3, _Location, longitude, latitude);
    }
}