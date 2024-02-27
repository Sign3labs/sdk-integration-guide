# Sign3 SDK Integration Guide

The Sign3 SDK is an Android-based fraud prevention toolkit designed to assess device security, detecting potential risks such as rooted devices, VPN connections, or remote access and much more. Providing insights into the device's safety, it enhances security measures against fraudulent activities and ensures a robust protection system.
<br>

## Adding Sign3SDK to Your Project

### Using Gradle Dependency

1. **Add Sign3SDK to the Dependency Block**
   - Open your app's `build.gradle` file and add the following line to the dependencies block:

     ```groovy
     dependencies {
         implementation 'com.sign3.intelligence:intelligence:<latest_version>'
     }
     ```
   - For the most recent latest version, see the [Changelog](https://github.com/ashishgupta6/Sign3SDK-Integration-Guide?tab=readme-ov-file#changelog)
2. **After adding the dependency, sync your project with Gradle files to ensure the library is properly integrated.**

### Manual Implementation

1. **Download & add the `sign3intelligence-release.aar` file to your `libs` folder.** 
2. **Include the SDK in Your Build**
   - In your app's `build.gradle` file, add the following line:

     ```groovy
     implementation(files("libs/sign3intelligence-release.aar"))
     ```
3. **Sync your project with Gradle files to complete the manual implementation.**

<br>

## Initializing the SDK

To ensure successful generation and processing of device fingerprints, initialize the SDK in the `onCreate()` method of your Application class.

### For Java

```java
import com.sign3.intelligence.Options
import com.sign3.intelligence.Sign3Intelligence
```

```java
Options options = new Options.Builder()
           .setSign3ClientId("SIGN3_CLIENT_ID")
           .setSign3ClientSecret("SIGN3_CLIENT_SECRET")
           .setEnvironment(Options.ENV_PROD)
           .build();

Sign3Intelligence.getInstance(this).initAsync(options, new Callback<Boolean>() {
       @Override
       public void onResult(Boolean result) {
           Log.i("AppInstance", "Sign3Intelligence init : " + result);
       }
});
```

### For Kotlin

```kotlin
import com.sign3.intelligence.Options
import com.sign3.intelligence.Sign3Intelligence
```

```kotlin
val options = Options.Builder()
   .setSign3ClientId("SIGN3_CLIENT_ID")
   .setSign3ClientSecret("SIGN3_CLIENT_SECRET")
   .setEnvironment(Options.ENV_PROD)
   .build()


Sign3Intelligence.getInstance(this).initAsync(options) {
   // to check if the SDK is initialized correctly or not
   Log.i("AppInstance", "Sign3Intelligence init : $it")
}
```

<br>

## Get Device Result

Our SDK will capture an initial device fingerprint upon SDK initialization and return an additional set of device intelligence ONLY if the device fingerprint changes along one session. This ensures a truly optimised end to end protection of your ecosystem.

### For Java

```java
import com.sign3.intelligence.Sign3Intelligence
import com.sign3.intelligence.IntelligenceListener
import com.sign3.intelligence.model.IntelligenceError
import com.sign3.intelligence.model.IntelligenceResponse
```
```java
Sign3Intelligence.getInstance(this).getIntelligence(new IntelligenceListener() {
       @Override
       public void onSuccess(IntelligenceResponse response) {
           // Do something with the response
       }


       @Override
       public void onError(IntelligenceError error) {
           // Something went wrong, handle the error message
       }
});
```

### For Kotlin

```kotlin
import com.sign3.intelligence.Sign3Intelligence
import com.sign3.intelligence.IntelligenceListener
import com.sign3.intelligence.model.IntelligenceError
import com.sign3.intelligence.model.IntelligenceResponse
```
```kotlin
Sign3Intelligence.getInstance(this).getIntelligence(object : IntelligenceListener {
   override fun onSuccess(response: IntelligenceResponse) {
        // Do something with the response
   }


   override fun onError(error: IntelligenceError) {
        // Something went wrong, handle the error message
   }
})
```

The SDK provides a method getIntelligence that asynchronously fetches this data. Upon successful data retrieval, the method triggers the onSuccess callback, where the response containing the device intelligence information is processed. In the event of an error during this process, the onError callback is invoked.

<br>

## Sample Device Result Response

```response
{
  "requestId": "9ca5fa19-cc8c-4d67-ac43-ae301e50478a",
  "newDevice": false,
  "deviceId": "6c805eea-cb6a-4a1f-8896-eb7b4804556d",
  "vpn": false,
  "proxy": false,
  "emulator": true,
  "remoteAccess": false,
  "cloned": false,
  "geoSpoofed": false,
  "rooted": false,
  "riskScore": "high",
  "ip": "183.83.153.87",
  "ipLocationCity": "Chennai",
  "ipLocationRegion": "Tamil Nadu",
  "ipLocationCountry": "IN",
  "ipLocationLatitude": 12.89960003,
  "ipLocationLongitude": 80.22090149,
  "ipRiskScore": 0.0,
  "ipNetworkType": "Residential",
  "hooking": true,
  "factoryReset": false,
  "appTampering": false,
  "gpsLocation": {
    "latitude": 37.4222755,
    "longitude": -122.0842218,
    "countryName": "United States",
    "countryCode": "US",
    "adminArea": "California",
    "subAdminArea": "Santa Clara County",
    "featureName": "1650",
    "postalCode": "94043",
    "locality": "Mountain View",
    "subLocality": null,
    "address": "1650 Amphitheatre Pkwy, Mountain View, CA 94043, USA"
  }
}

```
<br>

The device result JSONObject has following keys:


1. **requestId**: A unique identifier for the request, used to track the intelligence request and its response.
2. **newDevice**: Indicates whether the device is being seen for the first time (true) or not (false).
3. **deviceId**: A unique identifier for the device, typically used to track it across sessions or requests.
4. **vpn**: A boolean flag indicating whether the device is connected via a Virtual Private Network (true) or not (false).
5. **proxy**: Specifies if the device's internet connection is routed through a proxy server (true) or not (false).
6. **emulator**: Determines if the device is an emulator (true) or a physical device (false).
7. **remoteAccess**: Indicates whether the device is being accessed remotely (true) or not (false).
8. **cloned**: A boolean flag showing if the device is a clone (true) or original (false).
9. **geoSpoofed**: Specifies if the device's geographic location is being spoofed (true) or is genuine (false).
10. **rooted**: Indicates whether the device has root access (true) or not (false).
11. **riskScore**: A qualitative assessment of the device's security risk ("high", "medium", "low"), reflecting its overall trustworthiness.
12. **ip**: The IP address currently assigned to the device.
13. **ipLocationCity**: The city derived from the device's IP address location.
14. **ipLocationRegion**: The region or state derived from the device's IP address location.
15. **ipLocationCountry**: The country code derived from the device's IP address location.
16. **ipLocationLatitude**: The latitude location based on its IP address.
17. **ipLocationLongitude**: The longitude location based on its IP address.
18. **ipRiskScore**: A numeric score representing the risk associated with the device's IP address, with higher scores indicating greater risk.
19. **ipNetworkType**: The type of network the device is connected to (e.g., "Mobile"), providing insight into the nature of the internet connection.
20. **hooking**: Indicates if the device is using any form of hooking techniques, often used for intercepting function calls or messages.
21. **factoryReset**: Specifies if the device has been recently factory reset.
22. **gpsLocation**: A detailed object containing geographic location information obtained via GPS, including latitude, longitude, country name and code, administrative areas, postal code, 
        locality, and specific address.
23. **appTampering**: Indicates whether there's evidence of tampering with the application, such as modifying its code or behavior.


<br>

# Changelog

### 2.0.8
- Add new Detector features
- Add Some Device Signals
- Modified existing Detector codes
- Upgrade all dependencies with latest version
- Upgrade versions for newly device (Target API level : 34 Minimum API level : 21)
- Modified some deprecated methods
- Bug fixing and performance improvements


### 2.0.7
- Emulator detection improvements
- Handle data_roaming exception in android 13 device
- Bugfixes and compatibility improvements  

### 2.0.6
- Bugfixes and compatibility improvements 

### 2.0.5
- Bugfixes and stability improvements

### 2.0.4
- Bugfixes and stability improvements
  
### 2.0.3
- Bugfixes and stability improvements 

### 2.0.2
- Bugfixes and stability improvements

### 2.0.1
- Bugfixes and performance improvements

### 2.0.0
- First stable build


