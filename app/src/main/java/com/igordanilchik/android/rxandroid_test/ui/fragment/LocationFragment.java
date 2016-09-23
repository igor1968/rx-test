package com.igordanilchik.android.rxandroid_test.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.igordanilchik.android.rxandroid_test.R;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocationFragment extends Fragment {
    private static final String LOG_TAG = LocationFragment.class.getSimpleName();

    private static final int MAX_ADDRESSES = 1;
    private static final long LOCATION_TIMEOUT_IN_SECONDS = 5;
    private static final long LOCATION_UPDATE_INTERVAL = 60 * 1000;
    private static final float SUFFICIENT_ACCURACY = 500;
    private static final int REQUEST_PERMISSION = 1;
    private static final String ARG_LOCATION = "ARG_LOCATION";


    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.address)
    TextView address;

    private Unbinder unbinder;

    private GoogleMap googleMap;
    private ReactiveLocationProvider locationProvider;
    @Nullable
    private Location currentLocation = null;
    private Observable<Location> locationUpdatesObservable;
    @Nullable
    private Subscription updatableLocationSubscription = null;

    @NonNull
    public static LocationFragment newInstance() {
        LocationFragment f = new LocationFragment();
        return f;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_location, container, false);
        this.unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleMap != null &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(false);
        }
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (updatableLocationSubscription != null) {
            updatableLocationSubscription.unsubscribe();
            updatableLocationSubscription = null;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(ARG_LOCATION);
        }
        requestLastKnownLocation();

        MapsInitializer.initialize(getActivity());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            Log.d(LOG_TAG, "Map get");
            this.googleMap = googleMap;
            updateMap(currentLocation);
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

        if (currentLocation != null) {
            outState.putParcelable(ARG_LOCATION, currentLocation);
        }
    }

    private ReactiveLocationProvider getLocationProvider()
    {
        if (this.locationProvider == null) {
            this.locationProvider = new ReactiveLocationProvider(getActivity());
        }
        return this.locationProvider;
    }

    private void requestLastKnownLocation()
    {
        Observable<Location> lastKnownLocationObservable = getLocationProvider().getLastKnownLocation();
        lastKnownLocationObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setCurrentLocation, this::logError);
    }

    private Subscription requestUpdatableLocation()
    {
        LocationRequest req = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setExpirationDuration(TimeUnit.SECONDS.toMillis(LOCATION_TIMEOUT_IN_SECONDS))
                .setInterval(LOCATION_UPDATE_INTERVAL);

        locationUpdatesObservable = getLocationProvider().getUpdatedLocation(req)
                .filter(location -> location.getAccuracy() < SUFFICIENT_ACCURACY)
                .timeout(LOCATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, Observable.just((Location) null), AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());

        return locationUpdatesObservable.subscribe(this::updateContent, this::logError);
    }


    private void updateContent(@Nullable Location location) {
        Log.d(LOG_TAG, "updateContent");
        if (location != null) {
            setCurrentLocation(location);
            cameraSettings(new LatLng(location.getLatitude(), location.getLongitude()));

            Observable<List<Address>> reverseGeocodeObservable = getLocationProvider()
                    .getReverseGeocodeObservable(location.getLatitude(), location.getLongitude(), MAX_ADDRESSES);

            reverseGeocodeObservable
                    .subscribeOn(Schedulers.io())               // use I/O thread to query for addresses
                    .observeOn(AndroidSchedulers.mainThread())  // return result in main android thread to manipulate UI
                    .filter(addresses -> addresses.size() > 0)
                    .flatMap(addresses -> Observable.just(addresses.get(0)))
                    .filter(address1 -> address1 != null)
                    .subscribe(address1 -> {
                        String addressString = "";
                        for (int i = 0; i < address1.getMaxAddressLineIndex(); ++i) {
                            addressString += address1.getAddressLine(i) + " ";
                        }
                        address.setText(addressString);
                    }, this::logError);
        }
    }

    private void setCurrentLocation(@Nullable Location location) {
        Log.d(LOG_TAG, "Location set");
        currentLocation = location;
    }

    private void logError(Throwable e) {
        Log.e(LOG_TAG, "Error: ", e);
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void updateMap(@Nullable Location location) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            UiSettings settings = googleMap.getUiSettings();
            settings.setZoomControlsEnabled(true);
            googleMap.setMyLocationEnabled(true);

            if (location == null) {
                updatableLocationSubscription = requestUpdatableLocation();
            } else {
                updateContent(location);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            final HashSet<String> grantedPermissions = new HashSet<>();
            for (int i = 0 ; i < permissions.length; i++)
            {
                String permission = permissions[i];
                int result = grantResults[i];
                if (result == PackageManager.PERMISSION_GRANTED)
                {
                    grantedPermissions.add(permission);
                }
            }

            if (grantedPermissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
                grantedPermissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                updateMap(currentLocation);
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Location permission denied", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private void cameraSettings(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(cameraUpdate);
        setMarker(latLng);
    }

    private void setMarker(LatLng latLng) {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(false));
        marker.setTitle(getString(R.string.marker_title));
    }
}
