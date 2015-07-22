# FTC-Android-Robotcore

The officially-released FTC Java SDK does not contain most of the code that contains the core functionality for the app, and for the robot. By searching GitHub for the package statement `package com.qualcomm.robotcore`, it is possible to see a mostly-accurate version of the robotcore code. The purpose of this repository is not only to rehost that source code, but also to host a version that is accurate to the version. We've looked through the souce code and a decompiled version of the released code, and ensured that the files are functionally identical. The code here is not *garunteed* to be the same, but at the very least, the differences are few, and very small.

This code requires the Android SDK to compile. This is available easily by loading the code into Android Studio. To use this code in any other IDE, download "Robotcore.jar" from this repository, and add that to your project as a dependency.

We are also trying to clean up the code, so that it can run even better! If you'd like to help, take a look at [the to-do list](TODO.md) and submit a pull request.
