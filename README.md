# Sign3 SDK Integration Guide

This guide provides step-by-step instructions on how to integrate the Sign3 SDK into your Android application to enhance device security and intelligence capabilities.

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
       "requestId": "904a9a7d-f4f2-46ae-97df-24f23acb0528",
       "newDevice": true,
       "deviceId": "e25dae67-8d6d-41d8-b299-bb3985eaa095",
       "vpn": false,
       "proxy": false,
       "emulator": true,
       "remoteAccess": false,
       "cloned": true,
       "geoSpoofed": false,
       "rooted": false,
       "riskScore": "high",
       "ip": "152.59.69.117",
       "ipLocationCity": "Delhi",
       "ipLocationRegion": "National Capital Territory of Delhi",
       "ipLocationCountry": "IN",
       "ipLocationLatitude": 28.6541996,
       "ipLocationLongitude": 77.23729706,
       "ipRiskScore": 0.0,
       "ipNetworkType": "Mobile"
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


