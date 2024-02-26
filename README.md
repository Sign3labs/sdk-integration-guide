# Sign3 SDK Integration Guide

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

### For Java

```java
Options options = new Options.Builder()
           .setClientId("<SIGN3_CLIENT_ID>")
           .setClientSecret("<SIGN3_CLIENT_SECRET>")
           .setEnvironment(Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
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
val options = Options.Builder()
   .setClientId("<SIGN3_CLIENT_ID>")
   .setClientSecret("<SIGN3_CLIENT_SECRET>")
   .setEnvironment(Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
   .build()


Sign3Intelligence.getInstance(this).initAsync(options) {
   // to check if the SDK is initialized correctly or not
   Log.i("AppInstance", "Sign3Intelligence init : $it")
}
```

<br>

## Optional Parameters
You can add optional parameters like userId anytime and update the instance of Sign3Intelligence.

### For Java

```java

 UpdateOptions updateOptions = new UpdateOptions.Builder()
        .setUserId("<user-id>") // Set user id here, you can reference the result in future
        .setPhoneNumber("<1234567890>") // you can add the customer phone number
        .setPhoneInputType(PhoneInputType.PASTED) //See PhoneInputType class
        .setOtpInputType(OtpInputType.AUTO_FILLED) //See OtpInputType class
        .setUserEventType(UserEventType.LOGIN) //See UserEventType class
        .setMerchantId("<merchant_id>") // Set Merchant Id 


Sign3Intelligence.getInstance(context).updateOptions(updateOptions)
```

### For Kotlin

```kotlin

val updateOptions = UpdateOptions.Builder()
        .setUserId("<user-id>") // Set user id here, you can reference the result in future
        .setPhoneNumber("<1234567890>") // you can add the customer phone number
        .setPhoneInputType(PhoneInputType.PASTED) // See PhoneInputType class
        .setOtpInputType(OtpInputType.AUTO_FILLED) // See OtpInputType class
        .setUserEventType(UserEventType.LOGIN) // See UserEventType class
        .setMerchantId("<merchant_id>") // Set Merchant Id 
        .build()

Sign3Intelligence.getInstance(context).updateOptions(updateOptions)
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
  "requestId": "9ca5fa19-cc8c-4d67-ac43-ae301e50478a",
  "newDevice": false,
  "deviceId": "6c805eea-cb6a-4a1f-8896-eb7b4804556d",
  "vpn": false,
  "proxy": false,
  "emulator": true,
  "remoteAppProviders": false,
  "mirroredScreen": false,
  "cloned": false,
  "geoSpoofed": false,
  "rooted": false,
  "sessionRiskScore": 12.78",
  "hooking": true,
  "factoryReset": false,
  "appTampering": false,
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


## Changelog

### 3.0.0
 - Improved existing detection logic
 - Improved stability of device hash
 - Add new device signals to genrate better fingerprint
 - Introducing new detection remote control, screen mirroring, hooking, factory reset, App tampering & current location
 - SDK performance improvements and bug fixing
