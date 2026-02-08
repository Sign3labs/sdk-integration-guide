# Sign3 SDK Integration Guide for Android

The Sign3 SDK is an Android-based fraud prevention toolkit designed to assess device security, detecting potential risks such as rooted devices, VPN connections, or remote access and much more. Providing insights into the device's safety, it enhances security measures against fraudulent activities and ensures a robust protection system.
<br>

## Adding Sign3SDK to Your Project

1. **Configure Project-Level Repository**
   - Open your project level `build.gradle` file and add the following line to the repositories block. Please collect the **username** and **password** from credentials documents.

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

2. **Add Sign3SDK Dependency in App-Level Gradle**

     ```groovy
     dependencies {
         implementation 'com.sign3.intelligence:intelligence-playstore:4.x.x'
     }
     ```
   - Checkout the [latest_version](https://github.com/Sign3labs/sdk-integration-guide/tree/main?tab=readme-ov-file#changelog)

3. **After adding the dependency, sync your project with Gradle files to ensure the library is properly integrated.**

<br>

## App Permission
   1. Add the following permissions in the Manifest file.
   2. Optional permissions are recommended to achieve higher accuracy.

```permission
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
3. The SDK require a minimum SDK version of 23 if your app is targeting below this version must enclose Sign3 API calls within conditional checks.

### For Kotlin

```kotlin
override fun onCreate() {
    super.onCreate()

    // Other initialisation code
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val options = Options.Builder()
            .setClientId("<SIGN3_CLIENT_ID>")
            .setClientSecret("<SIGN3_CLIENT_SECRET>")
            .setSSLPinning(true) // Optional: If you want SSL pinning in API calls, default value is false.
            .setEnvironment(Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
            .build()

        Sign3Intelligence.getInstance(this).initAsync(options) {
            // to check if the SDK is initialized correctly or not
            Log.i("TAG_AppInstance", "Sign3Intelligence init : $it")
        }
    }

}
```

### For Java

```java
@Override
public void onCreate() {
    super.onCreate();

    // Other initialisation code
    Options options = new Options.Builder()
            .setClientId("<SIGN3_CLIENT_ID>")
            .setClientSecret("<SIGN3_CLIENT_SECRET>")
            .setSSLPinning(true) // Optional: If you want SSL pinning in API calls, default value is false.
            .setEnvironment(Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
            .build();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Sign3Intelligence.getInstance(this).initAsync(options);
    }
}
```
<br>

## Optional Parameters
1.	You can add optional parameters like UserId, Phone Number, etc., at any time and update the instance of Sign3Intelligence.
3. Once the options are updated, they get reset. Clients need to explicitly update the options again to ingest them, or else the default value of OTHERS in userEventType will be sent to the backend.
4. You need to call **getIntelligence()** function whenever you update the options.
5. To update the Sign3Intelligence instance with optional parameters, including additional attributes, you can use the following examples.

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
<br>

## Get Session ID

1. The Session ID is the unique identifier of a user's app session and serves as a reference point when retrieving the device result for that session.
2. The Session ID follows the OS lifecycle management, in line with industry best practices. This means that a user's session remains active as long as the device maintains it, unless the user terminates the app or the device runs out of memory and has to kill the app.

### For Kotlin

 ```kotlin

val sessionId = Sign3Intelligence.getInstance(this).getSessionId()
```
 
### For Java

 ```java

String sessionId = Sign3Intelligence.getInstance(this).getSessionId()
```
<br>

## Behavioural Biometrics
The Behavioural biometrics feature in the SDK captures and analyzes how users interact with your app to build a detailed behavioural profile. It monitors device sensor data, including accelerometer, gyroscope, and other sensors, as well as touch interactions such as taps, scrolls, long presses, and additional signals. By examining these patterns, the SDK can detect unusual activity and potential fraud, helping you enhance security and gain deeper insights into user behavior.

### StartAnalyzingBehaviour

- Use this method to start capturing the user’s behavioral data.
- Call it at the point in your app where you want to track interactions, such as on a specific screen or during flows like login, signup, or payment.
- The function returns a capture ID for detailed insights tracking.
- If `startAnalyzingBehaviour()` is called multiple times without calling `stopAnalyzingBehaviour()`, the same capture ID will be returned.

### For Kotlin
```start
val captureId = Sign3Intelligence.getInstance(this).startAnalyzingBehaviour()
```

### For Java
 ```java
String captureId = Sign3Intelligence.getInstance(this).startAnalyzingBehaviour()
```

### StopAnalyzingBehaviour

- Use the function below to safely halt behavioural data collection.
- Manually stop tracking at the appropriate point, such as after a user completes login, signup, or payment.
- The SDK continues collecting data until you explicitly stop it.
- Data collection will automatically stop if the app is killed, but it is recommended to stop it manually.

```stop
Sign3Intelligence.getInstance(this).stopAnalyzingBehaviour()
```

<br>

## Fetch Device Intelligence Result

1. To fetch the device intelligence data refer to the following code snippet.
2. IntelligenceResponse and IntelligenceError models are exposed by the SDK.

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
<br>

## Sample Device Result Response

### Successful Intelligence Response

```response
{
    "requestId": "403ad427-5018-47b9-b6e8-790e17a78201",
    "sessionId": "233sd769-9367-39h9-b9e8-650eb6e87820",
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
     "appliedRules": {
        "rules": {
            "102 : Screen mirrored": 0,
            "105 : Unusual Behaviour": "",
            "109 : Transaction into black listed account": "",
            "110 : Blacklist phone number": "",
            "55 : VPN enabled": 0,
            "58 : Remote access apps installed": "",
            "6 : Professional profiles exists": 0,
            "62 : account takeover high risk": 0,
            "65 : multi carding high risk": 0,
            "67 : account takeover medium risk": 0,
            "68 : multi carding medium risk": 0,
            "75 : social media account count more than 5": 0,
            "77 : Users velocity": 0,
            "91 : Money mule pincode": 0,
            "92 : Blacklisted ip": 0,
            "93 : More than 4 profiles associated with the device": 0,
            "94 : Device identifiers changed": 0,
            "95 : History of factory reset": 0,
            "96 : More than 80 sims used": 0,
            "97 : Phone number is not vintage": 0,
            "98 : Email is not vintage": 0,
            "99 : App is tampered": 50
        },
        "totalScore": 50.0
    },
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
                "id": 1,
                "simSlotIndex": 0,
                "carrierName": "JIO 4G | Jio",
                "eSim": false
            },
            {
                "id": 3,
                "simSlotIndex": 1,
                "carrierName": "airtel",
                "eSim": true
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

| Field                     | Type               | Description                                                                                                                                                                                                                                                                                                                                                                                | Default Value             |
|---------------------------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------|
| requestId                 | string             | A unique identifier for the specific request.                                                                                                                                                                                                                                                                                                                                             | ""          |
| newDevice                 | boolean            | Indicates if the device is new.                                                                                                                                                                                                                                                                                                                                                            | false                     |
| deviceId                  | string             | A unique identifier for the device.                                                                                                                                                                                                                                                                                                                                                        | ""          |
| vpn                       | boolean            | Indicates whether a VPN is active on the device.                                                                                                                                                                                                                                                                                                                                          | false                     |
| proxy                     | boolean            | Indicates whether a proxy server is in use.                                                                                                                                                                                                                                                                                                                                               | false                     |
| emulator                  | boolean            | Indicates if the app is running on an emulator.                                                                                                                                                                                                                                                                                                                                           | false                     |
| remoteAppProviders        | boolean            | Indicates whether any remote applications are installed on the device.                                                                                                                                                                                                                                                                                                                    | false                     |
| mirroredScreen            | boolean            | Indicates if the device's screen is being mirrored.                                                                                                                                                                                                                                                                                                                                       | false                     |
| cloned                    | boolean            | Indicates if the user is using a cloned instance of the app.                                                                                                                                                                                                                                                                                                                              | false                     |
| geoSpoofed                | boolean            | Indicates if the device's location is being faked.                                                                                                                                                                                                                                                                                                                                        | false                     |
| rooted                    | boolean            | Indicates if the device has been modified for root access.                                                                                                                                                                                                                                                                                                                                | false                     |
| sessionRiskScore          | float     | A score representing the risk level of the session.                                                                                                                                                                                                                                                                                                                                       | 0.0                       |
| hooking                   | boolean            | Indicates if the app has been altered by malicious code.                                                                                                                                                                                                                                                                                                                                  | false                     |
| factoryReset              | boolean            | Indicates if a suspicious factory reset has been performed.                                                                                                                                                                                                                                                                                                                               | false                     |
| factoryResetTime                 | long             | Timestamp of last factory reset. If not detected, the default is `-1`                                                                                                                                                                                                                                                                                                                                            | -1 (if not detected)          |
| appTampering              | boolean            | Indicates if the app has been modified in an unauthorized way.                                                                                                                                                                                                                                                                                                                            | false                     |
| clientUserIds             | array of strings   | An array of user IDs assigned by the client that a device has seen till now.                                                                                                                                                                                                                                                                                                               | []                        |
| gpsLocation               | object             | Details of the device's current GPS location, including latitude, longitude, and address information.                                                                                                                                                                                                                                                                                     | {}                        |
| ip                        | string             | The current IP address of the device.                                                                                                                                                                                                                                                                                                                                                     | ""          |
| ipDetails                 | object             | Object added to capture IP-related information and fraudScore related to IP address.                                                                                                                                                                                                                                                                                                       | {}                        |
| sign3UserIds              | array of strings   | This will contain Sign3 generated userIds list till now the device has seen. Note: The logic for generating userId will be configured as per your business logic and can be customized.                                                                                                                                                                                               | []                        |
| simInfo                   | object             | It will contain information like total sims used in a phone in its lifecycle, current sim+slot details.                                                                                                                                                                                                                                                                                    | {}                        |
| remoteAppProvidersCount   | number             | The number of remote application providers detected on the device.                                                                                                                                                                                                                                                                                                                        | 0                         |
| deviceRiskScore           | float     | The risk score of the device. Note: sessionRiskScore is derived from the latest state of the device but deviceRiskScore also factors in the historical state of the device (whether a device was rooted in any of the past sessions).                                                                                                                                                     | 0.0                       |
| deviceMeta                | object             | Contains all device-related information such as brand, model, screen resolution, total storage, etc.                                                                                                                                                                                                                                                                                       | {}                        |
| appAnalytics              | object             | An object containing an affinity field, which holds key-value pairs where each key is a category (e.g., entertainment, tech, gaming), and the value is a floating-point number between 0 and 1 representing the user's affinity score for that category. Higher scores indicate stronger interest, and lower scores suggest less interest. These scores are based on the apps installed on the user's device. | {} |
| additionalData            | object             | Returns the list of applied rules alongside the decision output, enabling the app to take immediate action (e.g., allow, warn, block) based on the exact rules fired.                                                                                                                                                                                     | {} |

<br>

## Changelog
### 5.0.0
 - Analyze every user interaction for potential fraud using behavioral biometrics.
 - Use passive analysis of keystrokes, touches, swipes, sensors, and pointer movements to proactively prevent modern fraud.
 - ANR issues have been identified and fixed.
 - Other minor bugs resolved and overall performance improvements.
### 4.0.7
 - The SDK now returns the list of applied rules alongside the decision output, enabling the app to take immediate action (e.g., allow, warn, block) based on the exact rules fired.
 - Added eSim detection: SDK now provides eSim detection at a particular sim slot directly in its response.
 - Added accessibility signals: SDK now captures additional device signals related to active accessibility services (when enabled) to strengthen risk/fraud detection.
 - Improved Hooking signal detection and reduce false positives.
 - Improved ANR handling from native code.
 - Enhanced error handling in the sdk thereby improving stability.
 - Made the sdk integration easier by removing the mandatory .stop() check.
### 4.0.6
 - Optimised file read processes.
 - Fixed crash issue on android versions 6 & 7.
 - Improved sdk stability on low end devices.
 - Improved coverage of dangerous packages
### 4.0.5
 - Added multiple new signals like debugger attached, usb/wireless debugging, etc.
 - Now you can enable/disable SSL pinning through Options.
 - Improved Location Accuracy.
 - Added permission related signals.
### 4.0.4
 - Optimized intelligence response time.
 - Other bug fixes and improvements.
### 4.0.3
 - Improved Location Accuracy.
 - Added permission related signals.
### 4.0.2
 - Added AIRPLANE mode signal.
 - Fixed Android Keystore Exception bug found in some devices.
 - Bug fixes and improvements.
### 4.0.1
 - Root detection bug resolved for devices with se-linux flag.
 - Fastened sdk initialization time by 30%.
 - Compatible with Android SDK version greater then 34
### 4.0.0
 - Expanded Android Compatibility: SDK now supports Android 16 (previously supported only upto Android 14)
 - Kotlin Updated: Upgraded Kotlin version to 2.1.0 for better language features, performance, and compatibility.
 - Gradle Updated: Upgraded Gradle to 8.0+ to support latest build tools and Kotlin updates.
### 3.4.2
 - Added AIRPLANE mode signal.
 - Fixed Android Keystore Exception bug found in some devices.
 - Bug fixes and improvements.
### 3.4.1
 - Root detection bug resolved for devices with se-linux flag.
 - Fastened sdk initialization time by 30%.
 - Compatible with Android SDK version up to 34
### 3.4.0
 - Advance root detection checks added.
 - Minor bug fixes and performance improvements
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
 - Bug fixing for Root detection.
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
