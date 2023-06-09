# Dicoding: User Story App 📱

<p align="center">
  <img src="https://images.glints.com/unsafe/glints-dashboard.s3.amazonaws.com/company-logo/0ecccc80caed7d3013433880e099e4fb.png" alt="Dicoding Logo"/>
</p>

An application containing a list of stories (like Instagram posts) uploaded by users from [Dicoding Story API](https://story-api.dicoding.dev/v1/). In this app, you can upload story with image (from camera and gallery), description, and location (_optional_), you can also see other user story in list (and in Google Map view also!) in realtime, and if you click one of the story you can see the detail of the story. This app is made for final submission on the "Android Intermediate" ([Belajar Pengembangan Aplikasi Android Intermediate](https://www.dicoding.com/academies/352)) Dicoding course. This application implements some of the material from the course, i.e:
- Advanced UI (Custom View)
- Animation (Shared Animation)
- Localization and Accessibility (Languages and Text-to-speech)
- Media (CameraX, Gallery Intent, Upload File to Server)
- Geo Location (using Google Maps API)
- Include Advanced Testing (Unit and UI Test using Espresso)
- Advanceed Database (RawQuery, Room, Database Migration, Paging3 with RemoteMediator)

This application is based on endpoint API base URL ```https://story-api.dicoding.dev/v1/``` with several routes in use, i.e.:
- ```.../login``` for login
- ```.../register``` for register or create account
- ```.../stories``` for get all list stories and upload story with or without location (```lat``` and ```lon``` params)
- ```.../stories?location=1``` for get story list based on location in Google Map view

This application uses Kotlin as a programing language and Android Studio version Electric Eel as a tool for developing the app.

## Get Google Map API Key 🗺

To display the Google Map view, you need the API Key from Google Cloud Console. Here is the step to get your own Google Map API key:
1. First, enter the [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/credentials) and open the **Credentials** tab.
2. Check and click **AGREE AND CONTINUE** to agree to the Terms of Service.
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:3b942d7fcaa3f20cafa2961a323d978420220413095139.png)<br>
   **note:** _If you are asked to enter a billing account, you can follow the other alternatives in step number 10. The important thing is to understand the basic steps first._
3. Then go to the side menu and select **API & Services → Credentials**.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:6c890ace50ae609ec47e5136844e343a20220413095415.png)
4. Create a new project by clicking **CREATE PROJECT** and change the project name to your liking. Click **CREATE** to continue.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:7bfefd3b8d4f360a71bd09ea16087d5020220413100118.png)<br>
   **note:** _Remember that the limit for the number of projects that can be created is 12. If you already have 12 projects, delete one of the projects first or you can use an existing project._
5. Next, activate the Google Maps feature by selecting **Enabled APIs & Services** on the side menu and clicking the **+ ENABLED APIS AND SERVICES** button.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:896983f587e0e02e5e7aef15a5cc3f7920220413101041.png)
6. A variety of features will appear that you can use in the Google Cloud Console. Select **Maps SDK for Android** and click Enable.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:6f8f0528f41072eb663ed0a62fc17cff20221214164816.png)
7. Next, select the Credentials menu on the side menu and click the **CREATE CREDENTIALS → API key** button to create new credentials.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:c8cfd99162171f49f74126501511394420220413101710.png)
8. At this point, you have got the key which usually starts with the word **"AIza…"**. Actually you can use this API key, but this key is still not secure because any project can use it. To be more secure, click the **Edit API key** link.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:fdba2376cbeb399b943d691488eee9ff20220413101934.png)
9. Select the **Android apps** checkbox in Application restrictions and add new data by clicking **ADD AN ITEM**. Then fill in the **package name** of your application and the **SHA-1** of the device used.<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:17fceddb79479d6060672d327d82364120220413102948.png)<br>
   **note:** _Each device has a different SHA certificate. To get the SHA-1, you can run the_ **```gradlew signingreport```** _or_ **```./gradlew signingReport```** _command in the **Android Studio Terminal**._<br>
   ![](https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/academy/dos:87cdcacfb8f1c5d08e74e05a1daa39fe20220413103447.jpeg)
10. Apart from using the method above, you can also create an APIKey via a _direct link_ like this to make it even faster.<br>
  [https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=**11:22:33:44:55:66:77:88:99:00:AA:BB:CC:DD:EE:FF:01:23:45:67**%3B**com.packagename.appname**](https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=11:22:33:44:55:66:77:88:99:00:AA:BB:CC:DD:EE:FF:01:23:45:67%3Bcom.packagename.appname)<br>
   You only need to change the SHA-1 data and package name marked in bold.<br>

**Note:** _The above steps are taken based on the "[Geo Location](https://www.dicoding.com/academies/352/tutorials/21827)" module of that Dicoding course._

## ⚠ Disclaimer ⚠
This repository is created for sharing and educational purposes only. Plagiarism is unacceptable and is not my responsibility as the author.
