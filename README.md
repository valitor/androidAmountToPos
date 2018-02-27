Valitor POSI Tengdur Android Library
=

This is the GitHub repository of the Valitor POSI Tengdur Android Library. The repository contains the source code for the library and a basic example of usage. The library itself is the Library module and the example is the Application module.

Prerequisites
-

* The library was created using Android Studio 3.0.1 and gradle 2.3.3
* The library has set the minimum Android SDK level to be 21, there is nothing that prevents you from using it in lower versions, the library depends on another library that has a minimum level of 10, but there are no guarantees that will work correctly before 21 since it has developed using 21 (I'm pretty sure it will work fine since the library does not use Android features that are impacted by the Android versioning).
* The library only supports the architectures arm64-v8a, armeabi, armeabi-v7a and x86.

Installation
-

* Copy the folder 'Library' into your project next to your application module (if you already have a module called Library, you can rename the folder before copying it, just make sure to supply the new name instead of Library in the next steps).
* Include it in your settings.gradle file in the root of your project.

```gradle
include ..., ':Library'
```

* Next, in your module that will consume the library, include the dependency in your build.gradle file.

```gradle
dependencies {
    ...
    compile project(':Library') // You can place this dependency where ever you want inside this block 
    ...
}
```

How do I use the library?
-

Before anything you do it's really important that you make this small configuration in your Application:

```java
public class MainApplication
        extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ValitorConfig.PACKAGE_NAME = getPackageName();
        ValitorConfig.ENABLE_LOG = true;
    }
}
```

Define a field in your activity.

```java
    private Valitor mValitor = null;
```

In the onCreate of the activity initialize your field, the first parameter to the constructor is your activity and the second parameter is an implementation of a ValitorListener, you can provide an instance of ValitorListener.NOP that implements this interface and does nothing in all it's method but to log the event.

```java
    mValitor = new Valitor(this, new ValitorListener.NOP());
```

Even tho you can use ValitorListener.NOP, you should at least override the following methods:

* ```void onReady()``` - this methods notifies you that the library is ready and you can start making requests.
* ```void onServiceStopped()``` - this method will notify you that the library is no longer in a valid state and can no longer make requests.
* ```void onError(String error, Exception ex)``` - this method notifies you when an error occurs in the library with an error code and possibly the exception that caused it (some error codes are defined inside ValitorListener).

After creating the instance you have to active a device for the library to use, to select a device you simply have to call ```mValitor.getDevices()``` and with any of the elements do the following:

```java
    if (mValitor.activateDevice(device)) {
        // Device successfully activated, you can proceed to call mValitor.start();
    } else {
        // Failed to activate device
    }
``` 

When you have successfully setup the library, you will receive a call in your ValitorListener implementation to the function ```void onReady()``` so you can start making requests.

To send a request, simply create an instance of any of the classes found in the package ```is.valitor.library.android.request``` with the correct parameters depending on the class and make the following call ```mValitor.execute(request);```, you can see some example of requests in the example project.

It is recommended that before you do any request you make first a ping request, you can see an example of this in the example project.

As almost everything in Android it's important to not hug to things when your activities or fragment are not in use, so make sure to call ```Valitor.start()``` and ```Valitor.stop()``` when your component is becoming active and inactive respectively.

How do I use the example project?
-

As mentioned earlier, you need Android Studio 3, after cloning this repository, open Android Studio and select the option "Open an existing Android Studio project" or go to File -> Open..., navigate to the directory where this repository was cloned and select the file settings.gradle.

After Android Studio finishes opening the project you'll find 2 modules, Application and Library, open up the Application module and navigate to the java source, you'll find in there MainApplication.java and MainActivity.java (there are other files in there also but those 2 are the most important) which are the files that showcase how to use the library.

When you run the example project, the app is going to prompt you to select a device to communicate with, after selecting a device you'll see a list of operations that you can execute with the library, you'll see that some are greyed out which means that the option is not available because a prerequisite for it has not been met yet.

The first thing to do is click in the button that says Start BT+TCP, this will make the library to setup all that it needs for it to work, when it successfully finishes the buttons states will change, the start button will now be greyed out and some others will be available, whenever you want to stop using the library just click in the Stop BT+TCP button. The other buttons will perform a request to the device, the name of the buttons tell you what request will be performed when you click it, the parameters for the request can be modified in code.