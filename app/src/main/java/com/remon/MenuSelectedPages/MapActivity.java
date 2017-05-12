package com.remon.MenuSelectedPages;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.remon.R;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    SupportMapFragment mapFragment;
    protected GoogleMap mMap;
    double longitude = 0;
    double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng N = new LatLng(latitude, longitude);
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(N));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17)); //구글맵 확대

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) { // 마커 커스텀 적용 (list_iteml)
                return null;
            }
        });

    }
}
