# StarstreamDroneGEII-Upgrade2017 / ARDrone-Android-GEII-Upgrade2024

## Description of the Project

The aim of this project is to completely control and monitor the Parrot AR.Drone 2.0 quadrocopter with an Android device. You can control the drone via virtual joysticks or a (Bluetooth) gamepad. It is a "copy-fork" of [ARDrone-Android-GEII](https://github.com/AJRdev/ARDrone-Android-GEII).

## Status

The differents goals reached at this point are:

* Controlling the drones movements
* Fetching the sensor data
* Receiving the video stream
* Using a (Bluetooth) gamepad to control the drone

### How to install

* Clone the repository into an arbitrary directory or download the ZIP snd unpack it.
* Open the project in Android Studio "Iguana" 2023.2.1 or higher, eventually upgrade the Gradle plug-in.
* Compile and download the app onto an Android device which is in minimum version 5.1 (API 22).
* Set the limits to middle values (maximum values can causes instability for the drone) and select a control mode (virtual joysticks or gamepad)
* Then press the "Start" button and wait several seconds before landing.
* Enjoy flying the drone!

## Library Dependencies

Our project uses following libraries :

* Vitamio: https://github.com/yixia/VitamioBundle - Used to handle the Drone’s video stream

### Documentation

* AR Drone developer guide: https://projects.ardrone.org/wiki/ardrone-api/Developer_Guide
* Open CV documentation: http://docs.opencv.org/
* Vitamio documentation: https://www.vitamio.org/en/docs/
* Handling controller actions: http://developer.android.com/training/game-controllers/controller-input.html

## Authors

**Original students**

This project has been made by a group of 5 students of [the GEII (Electrical and Industrial Computing Engineering) department](http://www.iut-acy.univ-savoie.fr/dut/geii/) of [Université Savoie Mont Blanc](https://www.univ-smb.fr/) in France for their last year term project. The original repository was [ARDrone-Android-GEII](https://github.com/AJRdev/ARDrone-Android-GEII).

* Bouguerra Bilel ([@BilelBgr](https://github.com/BilelBgr))
* Dancre Antoine
* Genoud Quentin
* Nabhan Stephane ([@BlackStef](https://github.com/BlackStef))
* Ranarivelo Andre ([@AJRdev](https://github.com/AJRdev))
* **Mentor:** Caron Bernard

**Students (2017)**

This has been updated by another group of 6 students from the same university in 2017. They have given a name to the project: "Starstream".

* BLANC Sofian
* CAPALDI Timothée
* MARMONT Maxime ([@marmontm](https://github.com/marmontm))
* MASSON Thomas
* PARIOT Valentin
* PETTIER Loïc ([@loicpettier](https://github.com/loicpettier))
* **Mentor:** Caron Bernard

**HorstBaerbel (2024)**

I made the repository build again with Android Studio "Iguana" 2023.2.1, fixed several linter errors, library issues and made strings translatable. Most visible texts were translated to English.  
**Note that I have not done the original copy from [ARDrone-Android-GEII](https://github.com/AJRdev/ARDrone-Android-GEII) and disapprove doing "copy-forks", but I wanted to start with something moderately new that's why I chose this repository.**

## License

This project is licensed under the terms of the [MIT license](LICENSE).

### Caveats / Todos
* [Vitamio](https://github.com/yixia/VitamioBundle) is very old. It is proprietary and can not be updated. This is a problem because:
  * The Android SDK needs to be < 23, because as of 23+ text relocations in 32-bit libraries are not allowed anymore. This means the app will crash due to the ffmpeg libraries packed into libarm.so (actually a ZIP file) having text relocations and not being loaded. This can only be fixed by compiling or using newer ffmpeg libraries with the correct settings.
  * Android complains about "Unable to match the desired swap behavior." which seems to come from the OpenGL functions in the native [Vitamio](https://github.com/yixia/VitamioBundle) libvinit.so libraries, which we don't have the source to.
  * There are no libraries for 64-bit devices.