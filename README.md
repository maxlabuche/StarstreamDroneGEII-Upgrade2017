#StarstreamDroneGEII-Upgrade2017

##Description of the Project:


The aim of this project is to completely control and monitor the Parrot AR.Drone 2.0 quadricopter with an Android device.
You have 2 ways to control the drone in this app : via virtual sticks or via a Bluetooth gamepad.

##Status:

The differents goals reached at this point are :

* Controlling the Drone’s movements
* Fetching the different Drone’s sensor data
* Receiving the Drone’s video stream
* Controlling the Drone’s movement using a Bluetooth gamepad (MYON mobile gamepad)

###How to Install:
* Download and unzip “StarstreamDroneGEII-Upgrade2017.zip” into an arbitory directory.
* Open the project in Android Studio, eventually upgrade the Gradle plug-in.
* Compile and download the app on an Android device which is in minimum version 5.0 (API 21).
* Set the limits to middle values (maximum values can causes instability for the drone) and select a pilotage mode (Virtual sticks or BT Gamepad)
* Then press the "Start" button and wait several seconds before landing.
* Enjoy flying the drone!

##Library Dependencies:

Our Project uses following libraries :

* Vitamio : https://github.com/yixia/VitamioBundle
Used to handle the Drone’s video stream

###Documentation:
* AR Drone Developer Guide : https://projects.ardrone.org/wiki/ardrone-api/Developer_Guide
* Open CV Documentation : http://docs.opencv.org/
* Vitamio Documentation : https://www.vitamio.org/en/docs/
* Handling Controler Actions : http://developer.android.com/training/game-controllers/controller-input.html

##Authors :

This project has been made by a group of 5 students of [the GEII (Electrical and Industrial Computing Engineering) department](http://www.iut-acy.univ-savoie.fr/dut/geii/) of [Université Savoie Mont Blanc](https://www.univ-smb.fr/) in France for their last year term project.
This has been updated by another group of 6 students from the same university in 2017. They have given a name to the project : "Starstream".

**Students (2017) :**

* BLANC Sofian
* CAPALDI Timothée
* MARMONT Maxime ([@marmontm](https://github.com/marmontm))
* MASSON Thomas
* PARIOT Valentin
* PETTIER Loïc ([@loicpettier](https://github.com/loicpettier))

**Original students :**

* Bouguerra Bilel ([@BilelBgr](https://github.com/BilelBgr))
* Dancre Antoine
* Genoud Quentin
* Nabhan Stephane ([@BlackStef](https://github.com/BlackStef))
* Ranarivelo Andre ([@AJRdev](https://github.com/AJRdev))

**Mentor :**
* Caron Bernard

##License:

This project is licensed under the terms of the MIT license.

(January 2017)