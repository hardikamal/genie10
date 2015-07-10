package com.getgenieapp.android.Activities;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.getgenieapp.android.CustomViews.Button.CircularButton;
import com.getgenieapp.android.CustomViews.Misc.SnackBar;
import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.GenieBaseActivity;
import com.getgenieapp.android.Objects.MessageValues;
import com.getgenieapp.android.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
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
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        ButterKnife.inject(this);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

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
    }

    @OnClick(R.id.refreshLocation)
    public void onClickRefresh()
    {
        MessageValues messageValues = getLocation();
        LatLng currentLocation = new LatLng(messageValues.getLat(), messageValues.getLng());

        map.addMarker(new MarkerOptions().position(currentLocation)
                .title("Your Current Location:\n"+messageValues.getText()));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
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