package com.example.indoorlocalizationv2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import com.example.indoorlocalizationv2.logic.BLELogic;
import com.example.indoorlocalizationv2.logic.IndoorLocalizationDatabase;
import com.example.indoorlocalizationv2.logic.TrilaterationLogic;
import com.example.indoorlocalizationv2.models.BLEDevice;
import com.example.indoorlocalizationv2.models.BLEPosition;
import com.example.indoorlocalizationv2.models.CircularQueue;
import com.example.indoorlocalizationv2.models.DiscoveredDeviceInfo;
import com.example.indoorlocalizationv2.models.LocalizationInfo;
import com.example.indoorlocalizationv2.models.RSSITableValue;
import com.example.indoorlocalizationv2.models.entities.DeviceLog;
import com.example.indoorlocalizationv2.utils.BluetoothExtension;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocalizationInfoFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView _lvLocalizationInfo;
    private LocalizationInfoListAdapter _localizListAdapter;

    private List<LocalizationInfo> _localizationInfoList;
    private MainActivity _mainActivity;
    private BLELogic _bleLogic;
    private Button _btnConnect;

    private boolean _isDisconnectButtonPressed;
    private boolean _isLoggingEnabled;
    private int _loggingCounter;

    private List<RSSITableValue> _rssiTable;
    private final int prevousRSSIValuesQueueSize = 10;
    private HashMap<String, CircularQueue> _previousRSSIValuesHashMap;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        _bleLogic.stopBluetoothScanner(mLeScanCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _bleLogic.stopBluetoothScanner(mLeScanCallback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _mainActivity = (MainActivity) getActivity();
        _bleLogic = new BLELogic(_mainActivity);
        _previousRSSIValuesHashMap = new HashMap<>();

        if (!_bleLogic.isBluetoothSupported()) {
            _mainActivity.finish();
        }

        if (!_bleLogic.isCoarseLocationEnabled()) {
            _bleLogic.enableCoarseLocation();
        }

        return inflater.inflate(R.layout.fragment_localization_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Localization list initialize
        _lvLocalizationInfo = view.findViewById(R.id.listview_localization_info);
        if (_lvLocalizationInfo != null) {
            _localizationInfoList = new ArrayList<>();
            _rssiTable = new ArrayList<>();

            // Average RSSI values for each distance
            // _rssiTable.add(new RSSITableValue(0.2, -53));
            // _rssiTable.add(new RSSITableValue(0.4, -70));
            // _rssiTable.add(new RSSITableValue(0.6, -74));
            // _rssiTable.add(new RSSITableValue(0.8, -71));
            // _rssiTable.add(new RSSITableValue(1.0, -69));
            // _rssiTable.add(new RSSITableValue(1.2, -74));
            // _rssiTable.add(new RSSITableValue(1.4, -75));
            // _rssiTable.add(new RSSITableValue(1.6, -80));
            // _rssiTable.add(new RSSITableValue(1.8, -88));
            // _rssiTable.add(new RSSITableValue(2.0, -84));

            // Initialize localization list adapter
            _localizListAdapter = new LocalizationInfoListAdapter(view.getContext(), _localizationInfoList);
            _lvLocalizationInfo.setAdapter(_localizListAdapter);
            this.initializeEventHandlersForControls(view);

            // TODO: Just some dummy data
//            BLEPosition rightAnchor = new BLEPosition();
//            rightAnchor.setX(2.0f);
//            BLEPosition topAnchor = new BLEPosition();
//            topAnchor.setZ(2.0f);
//            BLEPosition frontAnchor = new BLEPosition();
//            frontAnchor.setY(1.20f);
//
//            BLEPosition b1 = new BLEPosition();
//            b1.setX(0.40f);
//            b1.setZ(0.20f);
//            b1.setY(0.30f);
//            _localizationInfoList.add(new LocalizationInfo(b1, null, frontAnchor, rightAnchor, topAnchor));
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public float calculateDistance(int rssi) {
        float txPower = -71; // Usually variates between -59 and -65
        if (rssi == 0) {
            return -1.0f;
        }

        float ratio = rssi * 1.0f / txPower;
        if (ratio < 1.0) {
            return (float) Math.pow(ratio, 10);
        } else {
            // Smooths the calculated distance
            double accuracy = (0.79976) * Math.pow(ratio, 6.7095) + 0.111;
            return (float) Math.round(accuracy * 100) / 100;
        }
    }

    public float normalizeCalculatedDistance(float calculatedDistance) {
        float[][] predefinedValues = new float[][]
                {
                        new float[]{0.2f, -53},
                        new float[]{0.4f, -59},
                        new float[]{0.6f, -65},
                        new float[]{0.8f, -69},
                        new float[]{1.0f, -73},
                        new float[]{1.2f, -76},
                        new float[]{1.4f, -78},
                        new float[]{1.6f, -80},
                        new float[]{1.8f, -83},
                        new float[]{2.0f, -86},
                };

        float normalizedDistance;
        for (int i = 0; i < predefinedValues.length; i++) {
            if (calculatedDistance <= predefinedValues[i][0]) {
                normalizedDistance = (calculatedDistance + predefinedValues[i][0]) / 2;
                return (float)Math.round(normalizedDistance * 100) / 100;
            }
        }

        // This will be called when the calculated distance is more than expected max distance
        return (float)Math.round(calculatedDistance * 100) / 100;
    }

    /**
     * Click event for buttons which are subscribed to this event.
     *
     * @param btnView
     */
    @Override
    public void onClick(View btnView) {
        switch (btnView.getId()) {
            case R.id.localiz_btn_connect:
                Button connectBtn = getView().findViewById(R.id.localiz_btn_connect);
                if (!_bleLogic.isScanning()) {
                    connectBtn.setText(R.string.localiz_btn_disconnect);
                    _localizationInfoList.clear();
                    _localizListAdapter.notifyDataSetChanged();
                    _bleLogic.startBluetoothScanner(mLeScanCallback);
                    _isDisconnectButtonPressed = false;
                } else {
                    connectBtn.setText(R.string.localiz_btn_connect);
                    _bleLogic.stopBluetoothScanner(mLeScanCallback);
                    _isDisconnectButtonPressed = true;
                }

                break;
            default:
                break;
        }
    }

    private void initializeEventHandlersForControls(View view) {
        _btnConnect = view.findViewById(R.id.localiz_btn_connect);
        _btnConnect.setOnClickListener(this);

        CheckBox chboxLogData = view.findViewById(R.id.localiz_chbx_log_data);
        chboxLogData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO: logging logic here
                _isLoggingEnabled = isChecked;
            }
        });
    }

    /**
     * Note: Should the adapter be ArrayAdapter ?
     *
     * @param device
     * @param rssi
     */
    public void addDeviceToListOnLEScan(BluetoothDevice device, int rssi) {

        String macAddress = device.getAddress();
        boolean isUserDefinedDevice = _bleLogic.getDefinedDevicesHashMap().containsKey(macAddress);
        if (!isUserDefinedDevice) {
            return;
        }

        BLEDevice deviceInfo = new BLEDevice(device, true);
        deviceInfo.setMacAddress(macAddress);
        _bleLogic.addOrUpdateDevice(macAddress, deviceInfo);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request
                .Builder()
                .url("http://192.168.1.100:45455/api/rssi")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    final String myNewResponse = myResponse;
                    _httpClientHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            List<String> devices = new ArrayList<>();

                            try {

                                JsonParser parser = new JsonParser();
                                String retVal = parser.parse(myNewResponse).getAsString();
                                JSONArray itemArray = new JSONArray(retVal);
                                for (int i = 0; i < itemArray.length(); i++) {
                                    String value = itemArray.getString(i);
                                    devices.add(value);
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            updateDevicesUI(devices);
                        }
                    });
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    private BLEPosition UpdateAnchorInformation(BLEPosition anchorToUpdate, HashMap<String, DiscoveredDeviceInfo> definedDevices, String sourceAddress, String targetAddress, int newRSSI) {
        if (anchorToUpdate == null) {
            BLEPosition definedPosition = definedDevices.get(sourceAddress).getPosition();
            anchorToUpdate = new BLEPosition(definedPosition.getX(), definedPosition.getY(), definedPosition.getZ(), 0);
            anchorToUpdate.setName(definedDevices.get(sourceAddress).getDefinedDeviceName());
            anchorToUpdate.setMacAddress(sourceAddress);
        }

        anchorToUpdate.setRSSI(newRSSI);
        anchorToUpdate.setDistance(this.calculateDistanceAndAverageItsValue(sourceAddress, targetAddress, newRSSI));
        return anchorToUpdate;
    }

    private void updateDevicesUI(List<String> notifications) {
        if (notifications.size() == 0) {
            return;
        }

        HashMap<String, DiscoveredDeviceInfo> definedDevices = _bleLogic.getDefinedDevicesHashMap();
        HashMap<String, BLEDevice> scannedDevices = _bleLogic.getScannedDevices();

        for (String notification : notifications) {
            // Gets source and target addresses from notification data
            String nSourceAddress = BluetoothExtension.getSourceDeviceAddress(notification);
            String nTargetAddress = BluetoothExtension.getTargetDeviceAddress(notification);

            // Checks whether devices in notification are defined in database
            if (definedDevices.containsKey(nSourceAddress) && definedDevices.containsKey(nTargetAddress)) {
                // No need to continue if one of devices is not enabled
                if ((!scannedDevices.containsKey(nSourceAddress) || !scannedDevices.containsKey(nTargetAddress))) {
                    continue;
                }

                float foundAnchorDistance = 0.0f;
                int rssi = BluetoothExtension.getRssi(notification);
                String anchorType = definedDevices.get(nSourceAddress).getDefinedDeviceType();
                boolean isBeaconShownInList = false;
                for (LocalizationInfo lInfo : _localizationInfoList) {

                    if (lInfo.getBeacon() != null
                        && lInfo.getBeacon().getMacAddress().equals(nTargetAddress)) {

                        if (anchorType.equals(getString(R.string.manage_rdio_device_type_l_anchor))) {
                            BLEPosition leftAnchor = lInfo.getLeftAnchor();
                            leftAnchor = this.UpdateAnchorInformation(leftAnchor, definedDevices, nSourceAddress, nTargetAddress, rssi);
                            lInfo.setLeftAnchor(leftAnchor);
                            foundAnchorDistance = leftAnchor.getDistance();
                        } else if (anchorType.equals(getString(R.string.manage_rdio_device_type_f_anchor))) {
                            BLEPosition frontAnchor = lInfo.getFrontAnchor();
                            frontAnchor = this.UpdateAnchorInformation(frontAnchor, definedDevices, nSourceAddress, nTargetAddress, rssi);
                            lInfo.setFrontAnchor(frontAnchor);
                            foundAnchorDistance = frontAnchor.getDistance();
                        } else if (anchorType.equals(getString(R.string.manage_rdio_device_type_r_anchor))) {
                            BLEPosition rightAnchor = lInfo.getRightAnchor();
                            rightAnchor = this.UpdateAnchorInformation(rightAnchor, definedDevices, nSourceAddress, nTargetAddress, rssi);
                            lInfo.setRightAnchor(rightAnchor);
                            foundAnchorDistance = rightAnchor.getDistance();
                        } else if (anchorType.equals(getString(R.string.manage_rdio_device_type_t_anchor))) {
                            BLEPosition topAnchor = lInfo.getTopAnchor();
                            topAnchor = this.UpdateAnchorInformation(topAnchor, definedDevices, nSourceAddress, nTargetAddress, rssi);
                            lInfo.setTopAnchor(topAnchor);
                            foundAnchorDistance = topAnchor.getDistance();
                        }

                        if (lInfo.getLeftAnchor() != null
                                && lInfo.getFrontAnchor() != null
                                && lInfo.getRightAnchor() != null
                                && lInfo.getTopAnchor() != null) {

                            // Calculate the position
                            TrilaterationLogic logic = new TrilaterationLogic(
                                    lInfo.getLeftAnchor(), lInfo.getFrontAnchor(), lInfo.getRightAnchor(), lInfo.getTopAnchor()
                            );
                            BLEPosition coordinates = logic.calculateBeaconPosition();
                            lInfo.getBeacon().setX(coordinates.getX());
                            lInfo.getBeacon().setY(coordinates.getY());
                            lInfo.getBeacon().setZ(coordinates.getZ());
                        }

                        isBeaconShownInList = true;
                        break;
                    }
                }

                if (!isBeaconShownInList) {
                    // If beacon was not found in the UI list then adds it to the first
                    BLEPosition beacon = new BLEPosition();
                    beacon.setName(definedDevices.get(nTargetAddress).getDefinedDeviceName());
                    beacon.setMacAddress(nTargetAddress);
                    _localizationInfoList.add(new LocalizationInfo(beacon, null, null, null, null));
                }

                if (_isLoggingEnabled && _loggingCounter % 5 == 0) {
                    String macAddress = nSourceAddress + "_" + nTargetAddress;
                    String definedDeviceName = definedDevices.get(nSourceAddress).getDefinedDeviceName() + "<-" + definedDevices.get(nTargetAddress).getDefinedDeviceName();
                    String definedDeviceType = definedDevices.get(nSourceAddress).getDefinedDeviceType() + "<-" + definedDevices.get(nTargetAddress).getDefinedDeviceType();
                    this.logData(macAddress, definedDeviceName, definedDeviceType, rssi, foundAnchorDistance);
                    _loggingCounter = 0;
                }

                if (_isLoggingEnabled) {
                    _loggingCounter++;
                }

                // Reflects the updates in the UI
                _localizListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void logData(String deviceAddress, String deviceName, String deviceType, int rssi, float distance) {
        DeviceLog log = new DeviceLog();
        log.setMacAddress(deviceAddress);
        log.setDeviceName(deviceName);
        log.setDeviceType(deviceType);
        log.setDistance(distance);
        String flag = ((EditText) getView().findViewById(R.id.localiz_et_log_flag)).getText().toString();
        log.set_flag(flag);
        log.setRssi(rssi);

        IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
        database.deviceLogDao().insert(log);
        IndoorLocalizationDatabase.destroyInstance();
    }

    private float calculateDistanceAndAverageItsValue(String sourceAddress, String targetAddress, int rssi) {
        float aproximateDistance = this.calculateDistance(rssi);
        float normalizedDistance = this.normalizeCalculatedDistance(aproximateDistance);
        float averageDistance = normalizedDistance;

        // Adds/updates the history of devices previous RSSI values (averaging filter + Feedback filter)
        String previousRSSIValuesKey = sourceAddress + "_" + targetAddress;
        if (!_previousRSSIValuesHashMap.containsKey(previousRSSIValuesKey)) {
            CircularQueue newQueue = new CircularQueue(prevousRSSIValuesQueueSize);
            newQueue.add(normalizedDistance);
            _previousRSSIValuesHashMap.put(previousRSSIValuesKey, newQueue);
        } else {
            // Gets average distance value for given source/target device
            CircularQueue queue = _previousRSSIValuesHashMap.get(previousRSSIValuesKey);
            queue.add(normalizedDistance);
            averageDistance = queue.getAverageValue();
        }

        return (float)Math.round(averageDistance * 100) / 100;
    }

    private Handler _httpClientHandle = new Handler();
    private Callback _httpClientCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            // TODO: nothing
        }

        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                String myResponse = response.body().string();
                final String myNewResponse = myResponse;
                _httpClientHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        List<String> devices = gson.fromJson(myNewResponse, listType);
                        updateDevicesUI(devices);
                    }
                });
            }
        }
    };

    private Handler _leScanHandler = new Handler();
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            final int newRSSI = rssi;
            _leScanHandler.post(new Runnable() {
                @Override
                public void run() {
                    addDeviceToListOnLEScan(device, newRSSI);
                }
            });
        }
    };
}
