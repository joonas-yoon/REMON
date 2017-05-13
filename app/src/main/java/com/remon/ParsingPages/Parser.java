package com.remon.ParsingPages;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.remon.ListViewClasses.ListViewAdapter;
import com.remon.MedicalClasses.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Parser {

    DocumentBuilder parser;

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
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        return Radius * c;
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

    public void one_attr_parser(String attr, ArrayList<HashMap<String, Object>> retList, String addr) {
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

    public void List_parser_formh(ArrayList<HashMap<String, ArrayList<String>>> retList, String addr, double latitude, double longitude) {

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        try {
            parser = f.newDocumentBuilder();
        } catch(ParserConfigurationException e) {
            Log.d("parsing_error_catch3", e.getMessage());
            e.printStackTrace();
        }
        Log.d("parsing_error_catch5", latitude+"/"+longitude);
        //addr = "http://openapi.jeonju.go.kr/rest/medicalnew/getMedicalDistancelList?serviceKey=";
        //servicekey = "z%2BUi3qnnemU8I3aokp%2Fk%2FVYt3kg4r7Zi8KAb%2BxI%2BlfDwhTnsQsekuGpOEtzgD4qOxOIaxZGLo%2Bh%2BuJ%2FPD4bvGA%3D%3D";
        //parameter = "&numOfRows=999&pageSize=100&pageNo=1&startPage=1&posy=" + 35.844385 + "&posx=" + 127.137730 + "&searchDts=15";
        //addr = addr + servicekey + parameter;

        Document xmlDoc = null;
        try {
            xmlDoc = parser.parse(addr);
        } catch(Exception e) {
            Log.d("parsing_error_catch", e.getMessage());
        }
        try {
            Element root = xmlDoc.getDocumentElement();
            int length = root.getElementsByTagName("mediName").getLength();
            Log.d("parsing_checking22", length+"");
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
                Log.d("parsing_checking21", name.getTextContent()+"");
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
            //Toast.makeText(getBaseContext(), e+"", Toast.LENGTH_LONG).show();
        }
    }

    public void List_parser_forEmer(ArrayList<HashMap<String, ArrayList<String>>> retList, String addr) {
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
            Node dutyAddr = root.getElementsByTagName("dutyAddr").item(i);
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
            strList.add(dutyAddr.getTextContent()+"");
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

    public void update_list_formh(ArrayList<MedicalInfo> list_data, GoogleMap mMap, ListViewAdapter adapter)
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

    public void update_list_forEmer(ArrayList<EmergencyroomInfo> list_data, GoogleMap mMap, ListViewAdapter adapter, double latitude, double longitude)
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
    Comparator<EmergencyroomInfo> compare = new Comparator<EmergencyroomInfo>() { // SerialClass compare 함수
        @Override
        public int compare(EmergencyroomInfo lhs, EmergencyroomInfo rhs) {
            if (lhs.getDistance() > rhs.getDistance()) return 1;
            if (lhs.getDistance() == rhs.getDistance()) return 0;
            else return -1;
        }
    };

}
