# Sign3SDK-Integration-Guide


Integration Documentation of Sign3 SDK

Integrate the Sign3 SDK

Step 1: Add Sign3SDK to the Dependency block of your app/build.gradle file.
dependencies {
    implementation ‘com.sign3.intelligence:intelligence:<latest_version>’
}
Step 2: Sync project with gradle files

Manual Implementation
Step 1: Add aar file in your libs folder
Step 2: In your app build.gradle file simply add the SDK
implementation(files("libs/sign3intelligence-release.aar"))
Step 3: Sync project with gradle files


Initialize the SDK

The SDK initialization should be configured at onCreate() in your Application class to ensure successful generation and processing of device fingerprints. You need to set the SIGN3CLIENT_ID, SIGN3CLIENT_SECRET & ENVIRONMENT(PROD, DEV, STAGE) to initialize the SDK. 

For Java 
Options options = new Options.Builder()
           .setSign3ClientId("SIGN3_CLIENT_ID")
           .setSign3ClientSecret("SIGN3_CLIENT_SECRET")
           .setEnvironment(Options.ENV_PROD)
           .build();


Sign3Intelligence.getInstance(this).initAsync(options, new Callback<Boolean>() {
       @Override
       public void onResult(Boolean result) {
           // to check if the SDK is initialized correctly or not
           Log.i("AppInstance", "Sign3Intelligence init : " + result);
       }
});

For Kotlin
val options = Options.Builder()
   .setSign3ClientId("SIGN3_CLIENT_ID")
   .setSign3ClientSecret("SIGN3_CLIENT_SECRET")
   .setEnvironment(Options.ENV_PROD)
   .build()


Sign3Intelligence.getInstance(this).initAsync(options) {
   // to check if the SDK is initialized correctly or not
   Log.i("AppInstance", "Sign3Intelligence init : $it")
}

Get Device Result

Our SDK will capture an initial device fingerprint upon SDK initialization and return an additional set of device intelligence ONLY if the device fingerprint changes along one session. This ensures a truly optimised end to end protection of your ecosystem.

For Java
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


For Kotlin
Sign3Intelligence.getInstance(this).getIntelligence(object : IntelligenceListener {
   override fun onSuccess(response: IntelligenceResponse) {
        // Do something with the response
   }


   override fun onError(error: IntelligenceError) {
        // Something went wrong, handle the error message
   }
})


The SDK provides a method getIntelligence that asynchronously fetches this data. Upon successful data retrieval, the method triggers the onSuccess callback, where the response containing the device intelligence information is processed. In the event of an error during this process, the onError callback is invoked.














Sample Device Result Response

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

The device result JSONObject has following keys:

requestId: A unique identifier for the request, used to track the intelligence request and its response.
newDevice: Indicates whether the device is being seen for the first time (true) or not (false).
deviceId: A unique identifier for the device, typically used to track it across sessions or requests.
vpn: A boolean flag indicating whether the device is connected via a Virtual Private Network (true) or not (false).
proxy: Specifies if the device's internet connection is routed through a proxy server (true) or not (false).
emulator: Determines if the device is an emulator (true) or a physical device (false).
remoteAccess: Indicates whether the device is being accessed remotely (true) or not (false).
cloned: A boolean flag showing if the device is a clone (true) or original (false).
geoSpoofed: Specifies if the device's geographic location is being spoofed (true) or is genuine (false).
rooted: Indicates whether the device has root access (true) or not (false).
riskScore: A qualitative assessment of the device's security risk ("high", "medium", "low"), reflecting its overall trustworthiness.
ip: The IP address currently assigned to the device.
ipLocationCity: The city derived from the device's IP address location.
ipLocationRegion: The region or state derived from the device's IP address location.
ipLocationCountry: The country code derived from the device's IP address location.
ipLocationLatitude: The latitude location based on its IP address.
ipLocationLongitude: The longitude location based on its IP address.
ipRiskScore: A numeric score representing the risk associated with the device's IP address, with higher scores indicating greater risk.
ipNetworkType: The type of network the device is connected to (e.g., "Mobile"), providing insight into the nature of the internet connection.
