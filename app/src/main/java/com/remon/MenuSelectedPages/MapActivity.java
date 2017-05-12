package com.remon.MenuSelectedPages;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.remon.ListViewClasses.ListViewAdapter;
import com.remon.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    Intent getIntent;
    SupportMapFragment mapFragment;
    protected GoogleMap mMap;
    //GoogleApiClient mClient;

    double longitude = 0;
    double latitude = 0;

    TextView tv2;
    Button btn_screenshot;
    EditText searchEdit;

    ListViewAdapter adapter;
    ListView listview;

    String page_id; //intent로 받아오는 page_id

    LocationManager mLocationManager;


    final int MY_PERMISSIONS_EXTERNAL_STORAGE = 1;
    final int MY_PERMISSIONS_ACCESS_FINE_LOCATION =2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        getIntent = getIntent();
        page_id = getIntent.getStringExtra("page_id");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        tv2 = (TextView) findViewById(R.id.address);
        btn_screenshot = (Button) findViewById(R.id.screenshot);
        searchEdit = (EditText) findViewById(R.id.editSearch);

        //리스트 뷰 세팅
        adapter = new ListViewAdapter(); // listview 생성 및 adapter 지정.
        listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(adapter);

        tv2.setText("위치를 찾는중입니다...");
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String searchText = searchEdit.getText().toString();
                adapter.fillter(searchText);
            }
        }); //EditText 검색창 수정될 때


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("map_test_1", "check");
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                Log.d("map_test_2", "check");
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

                if(page_id.equals("ambul")) //응급실찾기 버튼일 때 리스트 업데이트
                {
                    Log.d("map_test_3", "check");

                }
            }
            else //gps 안켜졌을 경우
            {
                Toast.makeText(MapActivity.this, "GPS를 켜주세요", Toast.LENGTH_LONG).show();
                finish();
            }
            //EditText 검색창 수정될 때
            searchEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String searchText = searchEdit.getText().toString();
                    adapter.fillter(searchText);
                }
            });
        }
        else
            checkPermission(); //GPS Permission물어보기
    }//onCreate


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다
            //Log.d("location_check", location+"");
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            mapFragment.getMapAsync(MapActivity.this); //지도 업데이트
            tv2.setText(getAddress(latitude, longitude));

            try { //업데이트 제거
                mLocationManager.removeUpdates(mLocationListener);
            } catch (SecurityException e) {
                Toast.makeText(getBaseContext(), "removeUpdates Error", Toast.LENGTH_LONG).show();
            }
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("onProviderDisabled", provider);
        }
        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("onProviderEnabled", provider);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("onStatusChanged", "provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng N = new LatLng(latitude, longitude);
        String address = getAddress(latitude, longitude);

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
                View v = getLayoutInflater().inflate(R.layout.listview_form, null);
                String title = arg0.getTitle();
                String contents = arg0.getSnippet();
                StringTokenizer tokens = new StringTokenizer(contents, "|||");
                int size = tokens.countTokens();

                String snippet = tokens.nextToken();
                if (size == 4) {
                    TextView tvEmer = (TextView) v.findViewById(R.id.tv_emergency);
                    TextView tvPatient = (TextView) v.findViewById(R.id.tv_patient);
                    TextView tvOper = (TextView) v.findViewById(R.id.tv_operation);
                    String accept_emer = tokens.nextToken();
                    String accept_oper = tokens.nextToken();
                    String accept_patient = tokens.nextToken();

                    if (Integer.parseInt(accept_emer) > 0) tvEmer.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(accept_patient) == 1)
                        tvPatient.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(accept_oper) > 0) tvOper.setVisibility(View.VISIBLE);
                }

                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                TextView tvInfo = (TextView) v.findViewById(R.id.tv_info);

                tvTitle.setTextColor(0xFFFF0000);
                tvTitle.setText(title);
                tvInfo.setText(snippet);
                return v;
            }
        });
        Marker myPosition = mMap.addMarker(new MarkerOptions().position(N).title("현재위치").snippet(address));
        myPosition.showInfoWindow();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {

            }
        });

    }

    //주소 얻어오기
    public String getAddress(double lat, double lon) {
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> address;
        try {
            address = geocoder.getFromLocation(lat, lon, 1); //maxresult = 1
            if (address != null && address.size() > 0) {
                String currentAddress = address.get(0).getAddressLine(0) + "".replace("대한민국 ", "");
                nowAddress = currentAddress;
            }
        } catch (IOException e) {

        }
        return nowAddress;
    }

    private void checkPermission() {
        //permission 물어보기
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //저장소 쓰기 권한
        if (requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) //permission 허가
        {
            CaptureMapScreen();
        }
        else if(requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_DENIED)//permission 거부
        {
            Toast.makeText(MapActivity.this, "PERMISSION DENIED", Toast.LENGTH_LONG).show(); //Don't access EXTERNAL_STORAGE
        }

        //GPS 권한
        if(requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {//permission 허가 시
            checkPermission();
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }
        else if(requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION  && grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            //permission 허가 거부 시
            Toast.makeText(MapActivity.this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    //지도 캡쳐
    private void CaptureMapScreen() {
        Log.d("test_location", getApplicationContext().getFilesDir().getAbsolutePath() + "");
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(getBaseContext(), "SnapShot ERROR, NO BITMAP", Toast.LENGTH_LONG).show();
                } else {
                    String fileName = "MyLocation.jpg";
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes); //50일 때png는 182KB, JPEG는 38.13KB
                    FileOutputStream fileOutputStream;

                    if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE);
                    } else
                    {
                        try {
                            //Environment.getExternalStorageDirectory().getAbsolutePath() = 내장메모리 + 외장메모리
                            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
                            fileOutputStream.write(bytes.toByteArray());
                            fileOutputStream.close();

                            //갤러리 새로고침
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
                            Uri uri = Uri.fromFile(f);
                            mediaScanIntent.setData(uri);
                            getApplicationContext().sendBroadcast(mediaScanIntent);

                            Toast.makeText(getBaseContext(), "complete capture", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            Log.d("snapshot_error", e.getMessage());
                        }
                    }
                }
            }
        });
    }
}


