namespace IndoorLocalizationWebApi.Controllers
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Threading.Tasks;
    using System.Web.Http;
    using System.Web.Mvc;
    using System.Web.Script.Serialization;
    using IndoorLocalizationWebApi.Utils;

    public class RssiController : ApiController
    {
        private const int StatleTimeout = 10;

        /// <summary>
        /// Stores incoming messages: 3C:71:BF:9E:20:6A_F0:45:DA:F6:40:DF_-89
        /// where: 
        /// first part is source device (ESP32 anchor)
        /// second part is target device (preferebly BLE beacon)
        /// </summary>
        /// <remarks>
        /// The target device may also be ESP32 anchor itself.
        /// </remarks>
        private static List<string> devices = new List<string>();

        /// <summary>
        /// Collection that tracks the devices activity. 
        /// If they are stale for far too long, then it is removed.
        /// </summary>
        private static Dictionary<string, DateTime> devicesActivity
            = new Dictionary<string, DateTime>();

        /// <summary>
        /// GET api/rssi
        /// <para>Main method for retrieving latest data regarding devices RSSI values.</para>
        /// The array is returned as JSON string.
        /// </summary>
        /// <remarks>Called from Android Phone</remarks>
        public async Task<string> Get()
        {
            await RemoveStaleDevices();
            var serializer = new JavaScriptSerializer();
            return serializer.Serialize(devices.ToArray());
        }

        /// <summary>
        /// POST api/rssi/post
        /// Data sent from other devices (ESP32 preferebly).
        /// Returns JSON response back to device which sent data.
        /// </summary>
        public async Task<JsonResult> Post()
        {
            string newMessage = await Request.Content.ReadAsStringAsync();
            string newMessageTrimed = newMessage.Trim();

            // Makes sure that the mac addreses have expected length
            if (newMessageTrimed.Length != 39 && newMessageTrimed.Length != 40)
            {
                return new JsonResult();
            }

            string newMsgKey = DeviceMacAddressExtension.GetKeyFromMessage(newMessageTrimed);
            string newMsgValue = DeviceMacAddressExtension.GetValueFromMessage(newMessageTrimed);

            // Tracks device sent signals activity
            DateTime timeNow = DateTime.Now;
            if (devicesActivity.ContainsKey(newMsgKey))
            {
                devicesActivity[newMsgKey] = timeNow;
            }
            else
            {
                devicesActivity.Add(newMsgKey, timeNow);
            }

            bool isKeyUpdated = false;
            for (int i = 0; i < devices.Count; i++)
            {
                string key = DeviceMacAddressExtension.GetKey(devices, i);
                if (key == newMsgKey)
                {
                    // Update an existing record
                    devices[i] = newMessageTrimed;
                    isKeyUpdated = true;
                    Debug.WriteLine("Update record to: " + newMessageTrimed);
                    break;
                }
            }

            // Adds new record as it does not exist
            if (!isKeyUpdated)
            {
                devices.Add(newMessageTrimed);
                Debug.WriteLine("Adds new record: " + newMessageTrimed);
            }

            await RemoveStaleDevices();

            var jsonRes = new JsonResult
            {
                Data = "Device was: " + (isKeyUpdated ? "updated" : "added")
            };
            return jsonRes;
        }

        private async Task RemoveStaleDevices()
        {
            await Task.Run(() =>
            {
                // Not iterating through dictionary as it may be owned by another thread.
                List<string> dictionaryKeys = new List<string>(devicesActivity.Keys);
                foreach (var key in dictionaryKeys)
                {
                    if ((DateTime.Now - devicesActivity[key]).TotalSeconds > StatleTimeout)
                    {
                        devices.RemoveAll(x => x.StartsWith(key));
                        devicesActivity.Remove(key);
                    }
                }
            });
        }
    }
}
