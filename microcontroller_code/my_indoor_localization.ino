#include <WiFi.h>
#include <HTTPClient.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEScan.h>
#include "esp_bt_main.h"
#include "esp_bt_device.h"

const char* myWiFiSSID = "No WiFi for you";
const char* password = "";
const char deviceName[2] = "a";
const byte MAX_ALLOWED_DEVICES = 10;
const byte BLE_SCAN_DURATION = 4;
String currDeviceAddr = "";

/**
   Gets the current device unique address.
*/
String getCurrentDeviceAddress() {
  const uint8_t* point = esp_bt_dev_get_address();
  String result = "";
  for (byte i = 0; i < 6; i++)
  {
    char hexNr[3];
    sprintf(hexNr, "%02X", (short)point[i]);
    result += hexNr;
    if (i < 5)
    {
      result.concat(':');
    }
  }
  
  return result;
}

void setup()
{
  Serial.begin(9600);
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  BLEDevice::init(deviceName);
  currDeviceAddr = String(getCurrentDeviceAddress().c_str());
}

void loop() 
{
  short foundBLEDevicesCount = 0;
  
  // Scanns for networks and tries to connect only if not already connected
  if (WiFi.status() != WL_CONNECTED){
    short networksCount = WiFi.scanNetworks();
    if (networksCount > 0) 
    { 
      for (short i = 0; i < networksCount; i++)
      {
        String ssid = WiFi.SSID(i);
        if (ssid == myWiFiSSID)
        {
          // Tries to connect to WiFI
          WiFi.begin(ssid.c_str(), password);

          // Only continues if the connection succeeds
          while (WiFi.status() != WL_CONNECTED) 
          {
            delay(1000);
            Serial.println("Connecting to WiFi..");
          }
        } 
      }
    }
  }

  // Scans for BLE devices
  BLEScan *scan = BLEDevice::getScan();
  scan -> setActiveScan(true);
  
  // Scans the incoming BLE signals for the given duration
  BLEScanResults results = scan -> start(BLE_SCAN_DURATION);
  if (results.getCount() > 0)
  {
    char devicesRssiValues[MAX_ALLOWED_DEVICES][41];
    
    // Goes through each BLE result in order to prepare for next incoming HTTP_GET request
    for (byte i = 0; i < results.getCount(); i++)
    {
      BLEAdvertisedDevice device = results.getDevice(i);

      // Filters the devices which may be placed outside the room.
      // Limits how many devices are viewed
      if (device.getRSSI() > -90 && i < MAX_ALLOWED_DEVICES)
      {
        // Removes colons from target device
        String targAddrStr = String(device.getAddress().toString().c_str());
        targAddrStr.toUpperCase();
        
        // Holds the related devices address CRC_8 hash (<srcCRC8>_<targetCRC8>_<RSSI>)
        String deviceInfoStr = String(currDeviceAddr);
        deviceInfoStr.concat('_');
        deviceInfoStr.concat(targAddrStr);
        deviceInfoStr.concat('_');
        deviceInfoStr.concat(String(device.getRSSI()));
        strcpy(devicesRssiValues[i], deviceInfoStr.c_str());
        foundBLEDevicesCount++;
      }
    }

    if (foundBLEDevicesCount > 0)
    {
      for (byte i = 0; i < foundBLEDevicesCount; i++)
      {
        HTTPClient http;
        http.begin("http://192.168.1.100:45455/api/rssi/post");
        http.addHeader("Content-Type", "application/json");
        String data = String(devicesRssiValues[i]);
        Serial.println(data);
        int httpResponseCode = http.POST(data);
        Serial.println(httpResponseCode);
        http.end();
      }
    }
  }
}
