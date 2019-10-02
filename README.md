# PointSDK-RealTimeSync-SampleApp-Android

This project demonstrates Realtime Sync functionality of Bluedot SDK via push notifications. 
Realtime Sync allows for updating Zones and Fences information to the mobile fleet in real-time. 
Firebase push notifications library is picked for this example, but any push notification solution 
could potentially be utilised to implement Realtime Sync.

The following steps must be performed to compile and run the app:

1. Register an account within Firebase ecosystem http://firebase.google.com/‎ and put credentials into 
app source code.
2. Request a free demo for the Bluedot ecosystem http://bluedotinnovation.com/demo.
3. Once your account is provisioned, retrieve and enter credentials into app at `LogFragment.BLUEDOT_API_KEY`.
4. Generate Google Maps API Key and paste it inside AndroidManifest.xml as per instructions.

Full documentation is available at http://docs.bluedotinnovation.com/display/DEVDOC10/Android+Features+-+Real-time+Data+Sync
