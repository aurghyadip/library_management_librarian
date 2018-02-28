# Android Based Library Management System

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/91f47602b8694214ad4b47b81e7c62c8)](https://app.codacy.com/app/adkundu/library_management_librarian?utm_source=github.com&utm_medium=referral&utm_content=aurghyadip/library_management_librarian&utm_campaign=badger)

## Librarian Version

## Overview
The initial project idea was to build a project based on a PHP backend and Android frontent that would support a small or a medium sized library with ease, however the project now has Google Firebase as backend for a more realtime database.

Also, there are some design and dataflow changes in the App, though students can view which book they have in possession from the library right now, but they can't request a book (this is done for some security reason). To consolidate the missing feature, we have introduced a real time book count on the book details page.

Introduction and removal of the above features can lower the project cost and running cost greatly as well as removing most of the maintenance costs as firebase is a cloud system and charges only for the compute time we use.
Apart fromm that, the Application still uses Google Books API to get books data(if available) when adding the book to the library database.

Due to the restriction of some features, we have made sure that the minimum Android API Level is 23 (Android 6.0). 

The Application still requires a camera to operate as intended, however we hhave also added an option to make sure that the app can operate without a camera and wish to come out with a tablet version of the app with the same feature and kind of the same UI with some optimizations specially for Tablet devices to support a more streamlined workflow and go more towards the replacement of a full desktop computer for managing a library.
## Requirements
Currently the Minimum API Level is 23
so
- Android 6.0 or above
- At least 512MB of RAM in the device
- At least 100MB of free storage space
- (Optional) Camera with Autofocus capabilities
- WiFi / Mobile Internet capabilites

## Development Requirements
- Android Studio V3.0 or above and equivalent Gradle version

For compilation, we have used Android API Level 26. So,
- Android SDK API Level 26 or above
- At least 6GB of RAM, to build and run the project smoothly with an emulator
- An Android device with Android 6.0 or above for testing
- A stable internet connection