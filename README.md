# Sign3 SDK Integration Guide for Android

The Sign3 SDK is an Android-based fraud prevention toolkit designed to assess device security, detecting potential risks such as rooted devices, VPN connections, or remote access and much more. Providing insights into the device's safety, it enhances security measures against fraudulent activities and ensures a robust protection system.
<br>

## Adding Sign3SDK to Your Project

### Using Project Level Gradle Dependency

1. **Add Sign3SDK to the Dependency Block**
   - Open your project level `build.gradle` file and add the following line to the dependencies block. Please collect the **username** and **password** from credentials documents.

     ```groovy
      repositories {
          google()
          mavenCentral()
          maven { url 'https://jitpack.io' }
          maven {
              url "https://sign3.jfrog.io/artifactory/intelligence-generic-local/"
              credentials {
                  username = "provided in credential doc"
                  password = "provided in credential doc"
              }
          }
      }
      ```

### Using App Level Gradle Dependency

1. **Add Sign3SDK to the Dependency Block**
   - Open your app's `build.gradle` file and add the following line to the dependencies block.

     ```groovy
     dependencies {
         // For non play store application
         implementation 'com.sign3.intelligence:intelligence:<latest_version>'

         // For play store application
         implementation 'com.sign3.intelligence:intelligence-playstore:<latest_version>'
     }
     ```
   - For the most recent latest version, connect with Sign3.
   - Checkout the [latest_version](https://github.com/Sign3labs/sdk-integration-guide/tree/main?tab=readme-ov-file#changelog)
2. **After adding the dependency, sync your project with Gradle files to ensure the library is properly integrated.**

<br>

## App Permission
   1. Add the following permissions in the Manifest file.
   2. Optional permissions are recommended to achieve higher accuracy.

```java
<uses-permission android:name="android.permission.INTERNET" />
<!-- optional -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<!-- Below mentioned optional permissions are taken to calculate sim affinity to the device  -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

<br>

## Initializing the SDK

1. Initialize the SDK in the `onCreate()` method of your Application class.
2. Use the ClientID and Client Secret shared with the credentials document.
3. to enable a more in-depth root detection, you would need to add `enabledSign3Service`.
4. The SDK require a minimum SDK version of 23 if your app is targeting below this version must enclose Sign3 API calls within conditional checks.

### For Java

```java
Options options = new Options.Builder()
           .setClientId("<SIGN3_CLIENT_ID>")
           .setClientSecret("<SIGN3_CLIENT_SECRET>")
           .setEnvironment(Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
           .build();

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    Sign3Intelligence.getInstance(this).initAsync(options);
}
```

### For Kotlin

```kotlin
val options = Options.Builder()
   .setClientId("<SIGN3_CLIENT_ID>")
   .setClientSecret("<SIGN3_CLIENT_SECRET>")
   .setEnvironment(Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
   .build()

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    Sign3Intelligence.getInstance(this).initAsync(options) {
       // to check if the SDK is initialized correctly or not
       Log.i("AppInstance", "Sign3Intelligence init : $it")
    }
}
```
**Note**: If you have any custom **Application Class**, please add the following code in the **onCreate()** method of that Application class before any other initialisation code.

### For Java

```java
@Override
public void onCreate() {
     super.onCreate();
     if (Sign3Intelligence.Companion.stop()) return;
     // Other initialisation code
}
```

### For Kotlin

```kotlin
override fun onCreate() {
   super.onCreate()
   if (Sign3Intelligence.stop()) return
   // Other initialisation code
}
```
<br>

## Optional Parameters
1.	You can add optional parameters like UserId, Phone Number, etc., at any time and update the instance of Sign3Intelligence.
3. Once the options are updated, they get reset. Clients need to explicitly update the options again to ingest them, or else the default value of OTHERS in userEventType will be sent to the backend.
4. You need to call **getIntelligence()** function whenever you update the options.
5. To update the Sign3Intelligence instance with optional parameters, including additional attributes, you can use the following examples.


### For Java

```java
// Example of Additional Attributres
Map<String, String> attributes = new HashMap<>();

attributes.put("SIGN_UP_TIMESTAMP", String.valueOf(System.currentTimeMillis()));
attributes.put("SIGNUP_METHOD", "PASSWORD/OTP/SOCIAL/TRUECALLER/OTHERS");
attributes.put("REFERRED_BY", "UserID/Referral Code");
attributes.put("PREFERRED_LANGUAGE", "English/Spanish/etc.");

UpdateOptions updateOptions = new UpdateOptions.Builder()
       .setUserId("<user-id>") // Set user id here, you can reference the result in future
       .setPhoneNumber("<1234567890>") // you can add the customer phone number
       .setPhoneInputType(PhoneInputType.PASTED) //See PhoneInputType class
       .setOtpInputType(OtpInputType.AUTO_FILLED) //See OtpInputType class
       .setUserEventType(UserEventType.SIGNUP) //See UserEventType class
       .setMerchantId("<merchant_id>") // Set Merchant Id
       .setAdditionalAttributes(attributes) // Set attributes to store key-value pairs.    
       .build();

Sign3Intelligence.getInstance(context).updateOptions(updateOptions)

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
// Example of Additional Attributres
val attributes = hashMapOf<String, String>()

attributes["TRANSACTION_ID"] = UUID.randomUUID().toString()
attributes["DEPOSIT"] = "<AMOUNT>"
attributes["WITHDRAWAL"] = "<AMOUNT>"
attributes["METHOD"] = "UPI/CARD/NET_BANKING/WALLET"
attributes["STATUS"] = "SUCCESS/FAILURE"
attributes["CURRENCY"] = "USD/INR/GBP/etc."
attributes["TIMESTAMP"] = "${System.currentTimeMillis()}"

val updateOptions = UpdateOptions.Builder()
        .setUserId("<user-id>") // Set user id here, you can reference the result in future
        .setPhoneNumber("<1234567890>") // you can add the customer phone number
        .setPhoneInputType(PhoneInputType.PASTED) // See PhoneInputType class
        .setOtpInputType(OtpInputType.AUTO_FILLED) // See OtpInputType class
        .setUserEventType(UserEventType.TRANSACTION) // See UserEventType class
        .setMerchantId("<merchant_id>") // Set Merchant Id
        .setAdditionalAttributes(attributes) // Set attributes to store key-value pairs.   
        .build()

Sign3Intelligence.getInstance(context).updateOptions(updateOptions)

Sign3Intelligence.getInstance(this).getIntelligence(object : IntelligenceListener {
   override fun onSuccess(response: IntelligenceResponse) {
        // Do something with the response
   }

   override fun onError(error: IntelligenceError) {
        // Something went wrong, handle the error message
   }
})

```
<br>

## Get Session ID

1. The Session ID is the unique identifier of a user's app session and serves as a reference point when retrieving the device result for that session.
2. The Session ID follows the OS lifecycle management, in line with industry best practices. This means that a user's session remains active as long as the device maintains it, unless the user terminates the app or the device runs out of memory and has to kill the app.
 
### For Java

 ```java

String sessionId = Sign3Intelligence.getInstance(this).getSessionId()
```

### For Kotlin

 ```kotlin

val sessionId = Sign3Intelligence.getInstance(this).getSessionId()
```
<br>

## Fetch Device Intelligence Result

1. To fetch the device intelligence data refer to the following code snippet.
2. IntelligenceResponse and IntelligenceError models are exposed by the SDK.

### For Java

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
Sign3Intelligence.getInstance(this).getIntelligence(object : IntelligenceListener {
   override fun onSuccess(response: IntelligenceResponse) {
        // Do something with the response
   }


   override fun onError(error: IntelligenceError) {
        // Something went wrong, handle the error message
   }
})
```

<br>

## Sample Device Result Response

### Successful Intelligence Response

```response
{
    "requestId": "403ad427-5018-47b9-b6e8-790e17a78201",
    "newDevice": false,
    "deviceId": "43fccb70-d64a-4c32-a251-f07c082d7034",
    "vpn": false,
    "proxy": false,
    "emulator": true,
    "cloned": false,
    "geoSpoofed": false,
    "rooted": false,
    "ip": "106.219.161.71",
    "remoteAppProviders": false,
    "remoteAppProvidersCount": 3,
    "mirroredScreen": false,
    "hooking": true,
    "factoryReset": true,
    "appTampering": true,
    "sessionRiskScore": 99.50516,
    "deviceRiskScore": 99.50516,
    "clientUserIds": [
        "difansd23r32",
        "2390ksdfaksd"
    ],
    "sign3UserIds": [
        "13asefnn324"
    ],
    "gpsLocation": {
        "address": "F2620, Block F, Sushant Lok III, Sector 57, Gurugram, Haryana 122011, India",
        "adminArea": "Haryana",
        "countryCode": "IN",
        "countryName": "India",
        "featureName": "F2620",
        "latitude": "28.420385999999997",
        "locality": "Gurugram",
        "longitude": "77.088926",
        "postalCode": "122011",
        "subAdminArea": "Gurgaon Division",
        "subLocality": "Sector 57"
    },
    "ipDetails": {
        "country": "IN",
        "fraudScore": 27.0,
        "city": "New Delhi",
        "isp": null,
        "latitude": 28.60000038,
        "region": "National Capital Territory of Delhi",
        "asn": "",
        "longitude": 77.19999695
    },
    "simInfo": {
        "simIds": [
            {
                "simSlotIndex": 0,
                "carrierName": "Android",
                "id": 1
            }
        ],
        "totalSimUsed": 10
    },
    "appAnalytics": {
        "affinity": {
            "entertainment": 0.5,
            "gaming": 0.6,
            "productivity": 0.8,
            "food_and_drink": 0.4
        }
    },
    "deviceMeta": {
        "cpuType": "Qualcomm Technologies, Inc SM7325",
        "product": "I2126i",
        "androidVersion": "14",
        "storageAvailable": "29340553216",
        "storageTotal": "111156146176",
        "model": "I2126",
        "screenResolution": "1080x2316",
        "brand": "iQOO",
        "totalRAM": "7679795200"
    },
    "additionalData": {},
    "factoryResetTime": 1743419662000
}
```
### Error Response

```error
{
  "requestId": "7e12d131-2d90-4529-a5c7-35f457d86ae6",
  "errorMessage": "Sign3 Server Error"
}
```

<br>

## Intelligence Response

The intelligence response includes the following keys:

- **requestId**: A unique identifier for the specific request.
- **newDevice**: Indicates if the device is new.
- **deviceId**: A unique identifier for the device.
- **vpn**: Indicates whether a VPN is active on the device.
- **proxy**: Indicates whether a proxy server is in use.
- **emulator**: Indicates if the app is running on an emulator.
- **remoteAppProviders**: Indicates whether any remote applications are installed on the device.
- **mirroredScreen**: Indicates if the device's screen is being mirrored.
- **cloned**: Indicates if the user is using a cloned instance of the app.
- **geoSpoofed**: Indicates if the device's location is being faked.
- **rooted**: Indicates if the device has been modified for root access.
- **sessionRiskScore**: A score representing the risk level of the session.
- **hooking**: Indicates if the app has been altered by malicious code.
- **factoryReset**: Indicates if a suspicious factory reset has been performed.
- **appTampering**: Indicates if the app has been modified in an unauthorized way.
- **clientUserIds**: An array of user IDs assigned by the client that a device has seen till now.
- **gpsLocation**: Details of the device's current GPS location, including latitude, longitude, and address information.
- **ip**: The current IP address of the device.
- **ipDetails**: Object added to capture ip related information and fraudScore related to ip address.
- **sign3UserIds**: This will contain Sign3 generated userIds list till now the device has seen. Note: The logic for generating userId will be configured as per your business logic and can be customized.
- **simInfo**: It will contain information like total sims used in a phone in its lifecycle, current sim+slot details.
- **remoteAppProvidersCount**: The number of remote application providers detected on the device.
- **deviceRiskScore**: The risk score of the device. Note: sessionRiskScore is derived from the latest state of the device but deviceRiskScore also factors in the historical state of the device (whether a device was rooted in any of the past sessions).
- **deviceMeta**: Contains all device-related information such as brand, model, screen resolution, total storage, etc.
- **appAnalytics**: This object provides a categorized mapping of applications, as classified by the Google Play Store. Categories may include, but are not limited to, books_and_reference, business, communication, lifestyle, etc. For applications lacking detailed categorization on the Play Store, their packages are grouped under a category labeled others (e.g., "com.onecode.battle.gfx.pro"). Note that by default, it is not included in the response. Please get in touch with us to enable the feature.
- **additionalData**: Reserved for any extra or custom data not present in the IntelligenceResponse, providing a customized response based on specific requirements.
<br>

## Changelog
### 3.3.0
 -  Contract change in AppAnalytics.
 -  Handled potential crashes when SDK methods are accessed before proper initialization.
 -  Fixed a critical production crash issue affecting stability.
### :warning: 3.2.9 - To be deprecated due to internal issue. Do not use.
 -  Added functionality for factory reset time detection.
 -  Added app wise affinity score of user installed apps.
### :warning: 3.2.8 - To be deprecated due to internal issue. Do not use.
 - Minor bug fixes and improvements.
### :warning: 3.2.7 - To be deprecated due to internal issue. Do not use.
 - Bug fixes and improvements.
### :warning: 3.2.6 - To be deprecated due to internal issue. Do not use.
 - Add new fields in the IntelligenceResponse object DeviceMeta & AppAnalytics.
### 3.2.5
 - Bug fixes and improvements.
### 3.2.4
 - Minor bug fixes and improvements.
### 3.2.3
 - Added new signal and bug fixes.
### 3.2.2
 - Addition of Sim Info and few other fields in Intelligence Response.
### 3.2.1
 - Bug fixing and minor changes.
### 3.2.0
 - Fixed crash issue in android 6 & 7 devices.
 - Fixed native code issue on some devices.
 - Fixed camera supported FPS, BioMetricStatus issue.
 - Sign3 APIs now require a minimum SDK version of 23, apps targeting below this version must enclose Sign3 API calls within conditional checks.
### 3.1.0
 - bug fixing for Root detection.
### 3.0.3
 - Adding the enum field UserEventType.OTHERS using the setUserEventType method.
 - Setting the additional attributes using the setAdditionalAttributes method to Update Option.
### 3.0.2
 - Resolve dependency issue with Isolated Service
 - Fix ANR issue causing application unresponsiveness
### 3.0.1
 - Temporarily remove Isolated service dependency, to be reintegrated in 3.0.2
### 3.0.0
 - Improved existing detection logic
 - Improved stability of device hash
 - Add new device signals to generate better fingerprint
 - Introducing new detection remote control, screen mirroring, hooking, factory reset, App tampering & current location
 - SDK performance improvements and bug fixing
