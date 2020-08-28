# appium-tutorial

How to start:

1. Instal: nodejs, appium, Android Studio (provides emulators and adb)
2. Setup environment variables in Windows:
For variable name use “JAVA_HOME”, for value JDK installation path you noted earlier (eg. “C:\Program Files\Java\jdk1.8.0_261”), confirm 
In the same way create new variable “ANDROID_HOME”, with value pointing to Android SDK location (with default settings it should be at “C:\Users\your_user_name\AppData\Local\Android\Sdk”, confirm whether it’s there or somewhere else and use the correct path)
3. Run appium server from command line
4. Run emulator (eg. Pixel_ from Android Studio)
5. Type adb devices to make sure it is detected.
6. Drag'n'drop apk from src/main/resources/AUTs to emulator
7. Run uiautomatorviewer from command line, to make mobile screenshots and check elements locators
8. Write your Page Object classes in src/main/java/pages/pageClasses. 
9. Write your Test classes in src/main/java/tests/testCases
