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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.remon.ListViewClasses.ListViewAdapter;
import com.remon.ListViewClasses.ListViewItem;
import com.remon.MedicalClasses.EmergencyroomInfo;
import com.remon.MedicalClasses.MedicalInfo;
import com.remon.R;
import com.remon.SendMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{

    String page_id; //intent로 받아오는 page_id

    LocationManager mLocationManager;
    SupportMapFragment mapFragment;
    protected GoogleMap mMap;
    double longitude = 0;
    double latitude = 0;

    Button btn_screenshot;
    EditText edit_search;
    TextView view_address;

    ListViewAdapter adapter;
    ListView listview;

    //for parsing
    DocumentBuilder parser;
    ArrayList<HashMap<String, Object>> hpidList;
    ArrayList<HashMap<String, ArrayList<String>>> hospitalList;
    ArrayList result_list;

    String addr,servicekey, parameter; //for parsing address
    //---------------------

    final int MY_PERMISSIONS_EXTERNAL_STORAGE = 1;
    final int MY_PERMISSIONS_ACCESS_FINE_LOCATION =2;
    final int MY_PERMISSIONS_EXTERNAL_STORAGE2 =3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        page_id = getIntent().getStringExtra("page_id");

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        edit_search = (EditText) findViewById(R.id.editSearch);
        btn_screenshot = (Button) findViewById(R.id.screenshot);
        view_address = (TextView) findViewById(R.id.address);

        adapter = new ListViewAdapter(); // listview 생성 및 adapter 지정.
        listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(adapter);

        //for parsing
        hpidList = new ArrayList<>();
        hospitalList = new ArrayList<>();
        //------------------------------------------

        view_address.setText("위치를 찾는중입니다.");

        //GPS수신
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {//permission 있을 경우
            if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                if(page_id.equals("ambul")) //응급실찾기 버튼일 때 리스트 업데이트
                {
                    new StartParsing_Ambul().execute(); //background로 parsing
                    edit_search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String searchText = edit_search.getText().toString();
                            adapter.fillter(searchText);
                        }
                    }); //검색창 리스너
                    btn_screenshot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CaptureMapScreen();
                        }
                    });
                }
                else if(page_id.equals("mEmerge") || page_id.equals("m119"))
                {
                    new StartParsing_Ambul().execute(); //background로 parsing
                }
            }
            else //gps 안켜졌을 경우
            {
                Toast.makeText(MapActivity.this, "GPS를 켜주세요", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else
        {//permission 없을 경우
            check_GPS_Permission();
        }

        if(ContextCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
        {//권한 없으면 물어보기
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE2);
        }

    } //onCreate

    private void check_GPS_Permission()
    {
        //GPS permission 물어보기
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
    }
    private void check_Storage_Permission()
    {
        //저장소 Permission물어보기
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //권한 허가 또는 거부 후에 일어나는 액션 설정
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Storage Permission
        if (requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) //permission 허가
        {
            CaptureMapScreen();
        }
        else if(requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_DENIED)//permission 거부
        {
            Toast.makeText(MapActivity.this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
        }

        //GPS permission
        if(requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {//permission 허가 시
            check_GPS_Permission();
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }
        else if(requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION  && grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            Toast.makeText(MapActivity.this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
            finish();
        }

        if(requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE2  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(MapActivity.this, "PERMISSION GRANTED", Toast.LENGTH_LONG).show();
        }
        else if(requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE2  && grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            Toast.makeText(MapActivity.this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
        }
    }

    @Override  //지도 준비되었을 때.
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
            public View getInfoContents(Marker arg0) { // 마커 커스텀 적용 (list_item)
                View v = getLayoutInflater().inflate(R.layout.list_item, null);

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
        //지도에 마킹
        Marker myPosition = mMap.addMarker(new MarkerOptions().position(N).title("현재위치").snippet(address));
        myPosition.showInfoWindow();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {

            }
        });

        if(page_id.equals("ambul"))
        {//응급실 찾기는 위치 찾기와 파싱이 동시에 진행되기 때문에 바로 지도에 표시해도 된다.
            if(result_list != null) update_list_Ambul(result_list);
        }
        else if(page_id.equals("hospital") || page_id.equals("pharmacy"))
        {//병원과 약국찾기는 위치정보를 가지고 파싱을하기 때문에 위치를 찾고난 뒤에 파싱을 한다.
            new StartParsing_MedicalSearch().execute(); //background로 API받아오기
        }

        listview.setOnItemClickListener(new ListViewClickListener());

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    private void CaptureMapScreen() {
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

                    if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        //권한 있을 때
                        try {
                            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
                            fileOutputStream.write(bytes.toByteArray());
                            fileOutputStream.close();

                            //갤러리 새로고침
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
                            Uri uri = Uri.fromFile(f);
                            mediaScanIntent.setData(uri);
                            getApplicationContext().sendBroadcast(mediaScanIntent);

                            Toast.makeText(getBaseContext(), "Complete Capture", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            Log.d("snapshot_error", e.getMessage());
                        }
                    }
                    else
                    {//권한 없을 때
                        check_Storage_Permission();
                    }
                }
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


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            mapFragment.getMapAsync(MapActivity.this); //지도 업데이트
            view_address.setText(getAddress(latitude, longitude));

            try { //업데이트 제거
                mLocationManager.removeUpdates(mLocationListener);
            } catch (SecurityException e) {
                Toast.makeText(getBaseContext(), "RemoveUpdates Error", Toast.LENGTH_LONG).show();
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

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) { // 두 좌표 사이의 거리를 구하는 함수
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        return Radius * c;
    }

    Comparator<EmergencyroomInfo> compare = new Comparator<EmergencyroomInfo>() { // SerialClass compare 함수
        @Override
        public int compare(EmergencyroomInfo lhs, EmergencyroomInfo rhs) {
            if (lhs.getDistance() > rhs.getDistance()) return 1;
            if (lhs.getDistance() == rhs.getDistance()) return 0;
            else return -1;
        }
    };



    private class ListViewClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            ListViewItem obj = (ListViewItem) adapterView.getAdapter().getItem(position);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(obj.getMarker().getPosition().latitude, obj.getMarker().getPosition().longitude)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            obj.getMarker().showInfoWindow();
        }
    }

    public String add_parameter(String p) {
        String ret_parameter = "";
        ret_parameter = ret_parameter + "&" + p;
        ret_parameter = ret_parameter + "&" + "numOfRows=999";
        ret_parameter = ret_parameter + "&" + "pageSize=100";
        ret_parameter = ret_parameter + "&" + "pageNo=1";
        ret_parameter = ret_parameter + "&" + "startPage=1";
        return ret_parameter;
    }

    public void one_attr_parser(String attr, ArrayList<HashMap<String, Object>> retList) {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        try {
            parser = f.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document xmlDoc = null;
        String parseUrl = addr;
        try {
            xmlDoc = parser.parse(parseUrl);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        Element root = xmlDoc.getDocumentElement();
        int length = root.getElementsByTagName(attr).getLength();
        for (int i = 0; i < length; i++) {
            Node name = root.getElementsByTagName(attr).item(i);
            HashMap<String, Object> parseTest = new HashMap<String, Object>();
            parseTest.put(attr, name.getTextContent());
            retList.add(parseTest);
        }
    }

    public void List_parser_Ambul(ArrayList<HashMap<String, ArrayList<String>>> retList) {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        try {
            parser = f.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Log.d("parsing_error1", e.getMessage());
            e.printStackTrace();
        }

        Document xmlDoc = null;
        String parseUrl = addr;
        try {
            xmlDoc = parser.parse(parseUrl);
        } catch (SAXException | IOException e) {
            Log.d("parsing_error2", e.getMessage());
            e.printStackTrace();
        }

        Element root = xmlDoc.getDocumentElement();
        int length = root.getElementsByTagName("dutyName").getLength();
        for (int i = 0; i < length; i++) {
            Node name = root.getElementsByTagName("dutyName").item(i);
            Node addr = root.getElementsByTagName("dutyAddr").item(i);
            Node tel = root.getElementsByTagName("dutyTel1").item(i);
            Node lat = root.getElementsByTagName("wgs84Lat").item(i);
            Node lon = root.getElementsByTagName("wgs84Lon").item(i);

            Node accept_emergency_room = root.getElementsByTagName("hvec").item(i);
            Node accept_patient_room = root.getElementsByTagName("dutyHayn").item(i);
            Node accept_operation_room = null;

            try {
                accept_operation_room = root.getElementsByTagName("hvoc").item(i);
            } catch (Exception e) {
            }

            HashMap<String, ArrayList<String>> parseTest = new HashMap<String, ArrayList<String>>();

            ArrayList<String> strList = new ArrayList<String>();

            strList.add(name.getTextContent()+"");
            strList.add(addr.getTextContent()+"");
            strList.add(tel.getTextContent()+"");
            strList.add(lat.getTextContent()+"");
            strList.add(lon.getTextContent()+"");
            strList.add(accept_emergency_room.getTextContent()+"");
            strList.add(accept_patient_room.getTextContent()+"");
            if (accept_operation_room != null)
                strList.add(accept_operation_room.getTextContent()+"");

            parseTest.put("hpInfo", strList);
            retList.add(parseTest);
        }
    }

    public void List_parser_MedicalSearch(ArrayList<HashMap<String, ArrayList<String>>> retList) {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        try {
            parser = f.newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            Log.d("parsing_error_catch3", e.getMessage());
            e.printStackTrace();
        }
        Log.d("parsing_error_catch5", latitude+"/"+longitude);

        Document xmlDoc = null;
        try {
            xmlDoc = parser.parse(addr);
        } catch(Exception e) {
            Log.d("parsing_error_catch", e.getMessage());
        }
        try {
            Element root = xmlDoc.getDocumentElement();
            int length = root.getElementsByTagName("mediName").getLength();
            for (int i = 0; i < length; i++) {
                Node name = root.getElementsByTagName("mediName").item(i);
                Node address = root.getElementsByTagName("mediAddr").item(i);
                Node tel = root.getElementsByTagName("mediTel").item(i);
                Node distance = root.getElementsByTagName("distance").item(i);
                Node lat = root.getElementsByTagName("posy").item(i);
                Node lon = root.getElementsByTagName("posx").item(i);
                Node classify = root.getElementsByTagName("mediCdmStr").item(i);

                HashMap<String, ArrayList<String>> parseTest = new HashMap<String, ArrayList<String>>();
                ArrayList<String> strList = new ArrayList<String>();
                strList.add(name.getTextContent()+"");
                strList.add(address.getTextContent()+"");
                strList.add(tel.getTextContent()+"");
                strList.add(distance.getTextContent()+"");
                strList.add(lat.getTextContent()+"");
                strList.add(lon.getTextContent()+"");
                strList.add(classify.getTextContent()+"");
                parseTest.put("mediInfo", strList);
                retList.add(parseTest);
            }
        } catch(Exception e) {
            Log.d("parsing_checking2", e.getMessage());
        }
    }

    public void update_list_Ambul(ArrayList<EmergencyroomInfo> list_data)
    {
        if(list_data!=null)
        {
            int size = list_data.size();
            for (int i = 0; i < size; i++) {
                double lat = Double.parseDouble(list_data.get(i).getLatitude());
                double lon = Double.parseDouble(list_data.get(i).getLongitude());
                LatLng point = new LatLng(lat, lon);
                list_data.get(i).setDistance(CalculationByDistance(new LatLng(latitude, longitude), point));
            }
            Collections.sort(list_data, compare); // 리스트를 거리순으로 정렬
            for (int i = 0; i < size; i++) {
                double lat = Double.parseDouble(list_data.get(i).getLatitude());
                double lon = Double.parseDouble(list_data.get(i).getLongitude());
                double distance = list_data.get(i).getDistance();
                String hospital_name = list_data.get(i).getHospitalName();
                String addr = list_data.get(i).getAddress();
                String tel = list_data.get(i).getTelNum();
                String accept_emergency_room = list_data.get(i).getAcceptEmergencyRoom();
                String accept_operation_room = list_data.get(i).getAcceptOperationRoom();
                String accept_patient_room = list_data.get(i).getAcceptPatientRoom();

                int ck_emer = Integer.parseInt(accept_emergency_room);
                String emergency_room_state = ck_emer > 0 ? "응급실 가능" : "응급실 불가";
                int ck_oper = Integer.parseInt(accept_operation_room);
                String operation_room_state = ck_oper > 0 ? "수술실 가능" : "수술실 불가";
                int ck_patient = Integer.parseInt(accept_patient_room);
                String patient_room_state = ck_patient == 1 ? "입원실 가능" : "입원실 불가";
                LatLng point = new LatLng(lat, lon);
                String snippet = "주소 : " + addr + '\n' + "TEL : " + tel + " 거리 : " + String.format("%.2f", distance) + "Km" + "|||" + accept_emergency_room + "|||" + accept_operation_room + "|||" + accept_patient_room;

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                Marker location = mMap.addMarker(new MarkerOptions().position(point).title(hospital_name).snippet(snippet).icon(bitmapDescriptor));
                adapter.addItem(hospital_name, "거리 : " + String.format("%.2f", distance) + "Km   " + emergency_room_state + " | " + patient_room_state + " | " + operation_room_state, location);
            }
            adapter.notifyDataSetChanged();
        }
        else
        {
            Log.d("list_not_shown", "not");
        }
    }

    public void update_list_MedicalSearch(ArrayList<MedicalInfo> list_data)
    {
        if(list_data!=null)
        {
            int size = list_data.size();

            for (int i = 0; i < size; i++) {
                double lat = Double.parseDouble(list_data.get(i).getLatitude());
                double lon = Double.parseDouble(list_data.get(i).getLongitude());
                LatLng point = new LatLng(lat, lon);
                String distance = list_data.get(i).getDistance();
                String medical_name = list_data.get(i).getMedicalName();
                String addr = list_data.get(i).getAddress();
                String tel = list_data.get(i).getTelNum();
                String classify = list_data.get(i).getClassify();
                String snippet = "주소 : " + addr + '\n' + "TEL : " + tel;
                BitmapDescriptor bitmapDescriptor;

                if (classify.compareTo("약국") == 0) {
                    bitmapDescriptor
                            = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE);
                } else {
                    bitmapDescriptor
                            = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN);
                }
                String desc = addr + "\n거리 : " + String.format("%.2f", Double.parseDouble(distance)) + "Km";
                Marker location = mMap.addMarker(new MarkerOptions().position(point).title(medical_name).snippet(snippet).icon(bitmapDescriptor));
                adapter.addItem(medical_name, desc, location);
            }
            adapter.notifyDataSetChanged();
        }
        else
        {
            Log.d("list_not_shown", "not");
        }
    }


    //parsing thread for Ambul
    private class StartParsing_Ambul extends AsyncTask<Void, Void, ArrayList<EmergencyroomInfo>> {
        @Override
        protected ArrayList<EmergencyroomInfo> doInBackground(Void... voids)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            addr = "http://openapi.e-gen.or.kr/openapi/service/rest/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire?";
            servicekey = "serviceKey=z%2BUi3qnnemU8I3aokp%2Fk%2FVYt3kg4r7Zi8KAb%2BxI%2BlfDwhTnsQsekuGpOEtzgD4qOxOIaxZGLo%2Bh%2BuJ%2FPD4bvGA%3D%3D";
            parameter = add_parameter("STAGE1=%EC%A0%84%EB%9D%BC%EB%B6%81%EB%8F%84");
            addr = addr + servicekey + parameter;

            one_attr_parser("hpid", hpidList);
            String new_addr = "http://openapi.e-gen.or.kr/openapi/service/rest/ErmctInfoInqireService/getEgytBassInfoInqire?";
            for (int i = 0; i < hpidList.size(); i++) {
                String p = hpidList.get(i).get("hpid").toString();
                parameter = add_parameter("HPID=" + p);
                addr = new_addr + servicekey + parameter;
                try {
                    List_parser_Ambul(hospitalList);
                } catch (Exception e) {
                    Toast.makeText(MapActivity.this, "파싱 실패", Toast.LENGTH_LONG).show();
                }
            }
            int size = hospitalList.size();
            ArrayList<EmergencyroomInfo> hospital = new ArrayList<EmergencyroomInfo>();
            // 0 : 병원이름, 1 : 주소, 2 : 전화번호, 3 : 위도, 4 : 경도, 5 : 응급실, 6 : 입원실, 7 : 수술실
            for (int i = 0; i < size; i++) {
                String hospital_name = hospitalList.get(i).get("hpInfo").get(0);
                String address = hospitalList.get(i).get("hpInfo").get(1);
                address.replace("&nbsp;", " ");
                String tel = hospitalList.get(i).get("hpInfo").get(2);
                String lat = hospitalList.get(i).get("hpInfo").get(3);
                String lon = hospitalList.get(i).get("hpInfo").get(4);
                String accept_emer = hospitalList.get(i).get("hpInfo").get(5);
                String accept_patient = hospitalList.get(i).get("hpInfo").get(6);
                String accept_oper = "0";
                if (hospitalList.get(i).get("hpInfo").size() == 8)
                    accept_oper = hospitalList.get(i).get("hpInfo").get(7);
                EmergencyroomInfo temp = new EmergencyroomInfo(hospital_name, address, tel, lat, lon, accept_emer, accept_oper, accept_patient);
                hospital.add(temp);
            }
            result_list = hospital;
            return hospital;
        }

        @Override
        protected void onPostExecute(ArrayList<EmergencyroomInfo> result)
        {
            if(page_id.equals("ambul"))
            {
                if(latitude!=0 && longitude !=0) update_list_Ambul(result); //지도가 먼저 파싱되면 update
            }
            else if(page_id.equals("m119"))//119일 경우에는 지도를 캡쳐하고 메세지를 보낸다.
            {
                if(latitude!=0 && longitude !=0)
                {
                    if (ContextCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {//권한 없으면 물어보기
                        ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE2);
                    }

                    int size = result.size();
                    for (int i = 0; i < size; i++) {
                        double lat = Double.parseDouble(result.get(i).getLatitude());
                        double lon = Double.parseDouble(result.get(i).getLongitude());
                        LatLng point = new LatLng(lat, lon);
                        result.get(i).setDistance(CalculationByDistance(new LatLng(latitude, longitude), point));
                    }
                    Collections.sort(result, compare); // 리스트를 거리순으로 정렬

                    SystemClock.sleep(4000); //바로 찍으면 지도가 흐릿함.

                    CaptureMapScreen(); //지도 찍기
                    new SendMessage(MapActivity.this, "m119", getAddress(latitude, longitude), "", result); //문자 보내기
                    finish();
                }
            }

            else if(page_id.equals("mEmerge"))
            {
                if(latitude!=0 && longitude !=0) //지도가 구해진 상태에서 파싱이 끝나면 캡쳐 하면 된다.
                {
                    int size = result.size();
                    for (int i = 0; i < size; i++) {
                        double lat = Double.parseDouble(result.get(i).getLatitude());
                        double lon = Double.parseDouble(result.get(i).getLongitude());
                        LatLng point = new LatLng(lat, lon);
                        result.get(i).setDistance(CalculationByDistance(new LatLng(latitude, longitude), point));
                    }
                    Collections.sort(result, compare); // 리스트를 거리순으로 정렬
                    //화면 이동
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(result.get(0).getLatitude()), Double.parseDouble(result.get(0).getLongitude()))));
                    SystemClock.sleep(4000); //바로 찍으면 지도가 흐릿함.
                    CaptureMapScreen(); //지도 찍기
                    new SendMessage(MapActivity.this, "mEmerge", getAddress(Double.parseDouble(result.get(0).getLatitude())
                            ,Double.parseDouble(result.get(0).getLongitude())), result.get(0).getHospitalName(),null); //문자 보내기
                    finish();
                }
            }

        }
    }

    //parsing thread for Medical Search
    private class StartParsing_MedicalSearch extends AsyncTask<Void, Void, ArrayList<MedicalInfo>>
    {
        @Override
        protected ArrayList<MedicalInfo> doInBackground(Void... voids)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            if(page_id.equals("hospital")) //병원일 때
            {
                addr = "http://openapi.jeonju.go.kr/rest/medicalnew/getMedicalDistancelList?serviceKey=";
                servicekey = "z%2BUi3qnnemU8I3aokp%2Fk%2FVYt3kg4r7Zi8KAb%2BxI%2BlfDwhTnsQsekuGpOEtzgD4qOxOIaxZGLo%2Bh%2BuJ%2FPD4bvGA%3D%3D";
                parameter = "&numOfRows=999&pageSize=100&pageNo=1&startPage=1&posy=" + latitude + "&posx=" + longitude + "&searchDts=10";
                addr = addr + servicekey + parameter;

                ArrayList<HashMap<String, ArrayList<String>>> mhList = new ArrayList<HashMap<String, ArrayList<String>>>();
                try {
                    List_parser_MedicalSearch(mhList);
                    Log.d("parsing_checking", "check");
                }catch(Exception e)
                {
                    Toast.makeText(MapActivity.this, "파싱 실패", Toast.LENGTH_LONG).show();
                }
                int size = mhList.size();
                Log.d("parsing_checking10", size+"");
                ArrayList<MedicalInfo> medical = new ArrayList<MedicalInfo>();
                // 0 : 병원이름, 1 : 주소, 2 : 전화번호, 3 : 위도, 4 : 경도
                for (int i = 0; i < size; i++) {
                    String medical_name = mhList.get(i).get("mediInfo").get(0);
                    Log.d("medi_name", medical_name);
                    String address = mhList.get(i).get("mediInfo").get(1);
                    address = address.replaceAll("&nbsp;", " ");
                    String tel = mhList.get(i).get("mediInfo").get(2);
                    String distance = mhList.get(i).get("mediInfo").get(3);
                    String lat = mhList.get(i).get("mediInfo").get(4);
                    String lon = mhList.get(i).get("mediInfo").get(5);
                    String classify = mhList.get(i).get("mediInfo").get(6);
                    if (classify.compareTo("약국") == 0) continue;
                    MedicalInfo temp = new MedicalInfo(medical_name, address, tel, distance, lat, lon, classify);
                    medical.add(temp);
                }
                result_list = medical;
                return medical;

            }
            else if(page_id.equals("pharmacy")) //약국일 때
            {
                addr = "http://openapi.jeonju.go.kr/rest/medicalnew/getMedicalDistancelList?serviceKey=";
                servicekey = "z%2BUi3qnnemU8I3aokp%2Fk%2FVYt3kg4r7Zi8KAb%2BxI%2BlfDwhTnsQsekuGpOEtzgD4qOxOIaxZGLo%2Bh%2BuJ%2FPD4bvGA%3D%3D";
                parameter = "&numOfRows=999&pageSize=15&pageNo=1&startPage=1&posy=" + latitude + "&posx=" + longitude + "&searchDts=15";
                addr = addr + servicekey + parameter;
                ArrayList<HashMap<String, ArrayList<String>>> mhList = new ArrayList<HashMap<String, ArrayList<String>>>();
                try {
                    List_parser_MedicalSearch(mhList);
                }catch(Exception e)
                {
                    Toast.makeText(MapActivity.this, "파싱 실패", Toast.LENGTH_LONG).show();
                }
                int size = mhList.size();

                ArrayList<MedicalInfo> medical = new ArrayList<MedicalInfo>();
                // 0 : 병원이름, 1 : 주소, 2 : 전화번호, 3 : 위도, 4 : 경도
                for(int i=0; i<size; i++) {
                    String medical_name = mhList.get(i).get("mediInfo").get(0);
                    String address = mhList.get(i).get("mediInfo").get(1);
                    address = address.replaceAll("&nbsp;", " ");
                    String tel = mhList.get(i).get("mediInfo").get(2);
                    String distance = mhList.get(i).get("mediInfo").get(3);
                    String lat = mhList.get(i).get("mediInfo").get(4);
                    String lon = mhList.get(i).get("mediInfo").get(5);
                    String classify = mhList.get(i).get("mediInfo").get(6);
                    if(classify.compareTo("약국") != 0) continue;
                    MedicalInfo temp = new MedicalInfo(medical_name, address, tel, distance, lat, lon, classify);
                    medical.add(temp);
                }
                result_list = medical;
                return medical;
            }
            else return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MedicalInfo> result) {
            if(latitude!=0 && longitude !=0) update_list_MedicalSearch(result);
        }
    }
}

