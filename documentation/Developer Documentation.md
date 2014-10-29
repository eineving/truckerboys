#Developer Documentation
Documentation and motivation regarding decisions that's been made during
the development process, aswell as information regarding all external
dependencies and what they have contributed with during our development procedure.

##Project Structure
###Design Decisions
During this project we've encountered a few problems with how to structure the
application. Below is an overview (and a few links for further information)
explaining the general idea and thought process behind these decisions.

#####MVP - <a href="http://antonioleiva.com/mvp-android/">Explanation</a>
The overall design of our application is based on the MVP (Model-View-Presenter)
designpattern, this is the Android version of MVC (Model-View-Controller)
designpattern. It is very usefull in the aspect that it makes for a very
well structured application which is easy to modify and write tests to,this is
also a pattern that everyone in the project is very comfortable with and have
used before (MVC), therefore we felt it was an appropriate choice in overall design.

However there exists a problem with the MVP pattern, which is the fact that
you have double couplings between the presenter and the view, which we
solved by the introduction of an EventBus.

#####EventBus
The EventBus was introduced into our project when we needed to solve the double
couplings between the views and presenters. What the EventBus is used for
in our application is sending events to it's subscribers whenever something is
done.

<i>For example in our application this is the use case in most situations:</i><br />
The user presses a button, which the view has to have a listener to since thats
where we can get the component from <i>(This is a restriction from the Android Environment)</i>
The view then sends a "Button Pressed" event, which the presenter listens to,
the presenter then tells the model to do a specific action that we want performed
with the data at this point.

This solves our double coupling problem, since the view is 'stupid' and simply
forwards its problems to whoever wants to listen.

In addition to solving the above mentioned problem, the EventBus also allows us to
take actions in other parts of the application whenever we want to. For example
we can update the application in real-time when we change something in our settings
tab. Which makes for a better user experience.

##User Inteface
The app was designed in a gray colour scheme for a clean and non-distracting user experience. 
The driver/user should not focus his/hers attention on the app while driving, but on the road. 
This, along the SICS guidelines was a main focus of ours when designing the user interface and by using a gray colour scheme within the app the driver wouldn’t glance at any colourful icons/graphics in his peripheral vision.
As mentioned we’ve also followed the SICS design guidelines which have given us clear do’s and don’ts regarding the user interface design (e.g. max 30 chars when driving). 

##Safety Features
In the Settings tab we’ve chosen to implement support for muting the system sound, as well as keeping the screen awake at all time. 
This impoves safety a great deal in a manner that the driver wont have to close the app, enter Androids system settings and navigating to the desired setting. Instead it’s as easy as swiping to OTTO’s settings tab and switching on or off. 
To make this as intuitive a task a possible we’ve chosen to use intuitive switch components, natively found in the Android system.

##External Dependencies
Since we're developing for Android, we've choosen Google as our main provider for
external APIs. This is mainly because users of Android are already very
comfortable with Googles applications and services.

Google is also the leading provider of GPS/Location based APIs in the world for
all developers to use. This makes for an easy decision since all their APIs
work very well both together and integrated with Android. Also there is alot of
documentation and help to receive from others since they are so widely used by
developers.

<i>Google separates all of their APIs, making us use alot of them instead
of one big API;</i>

#####Google Maps
Google Maps is an API that is developed by Google and its main focus is providing
a world wide map for developers to use and let their users navigate within. We've
choosen this API mainly because it's generally the most used Maps API in the world
and most users expect to see a Google Map when navigating a world map using an
Android device. Therefore they're familiar with the navigation methods and
such is important to make the user comfortable with the application.

We use the Google Maps API mostly for letting the driver follow a calculated
route. Making it easy for the driver to know where he or she is going to make the
next turn etc.

#####Google Directions
Google Directions is the API used for calculating routes between a set of points,
in other words this is the API we use to optimize the drivers route depending
on the time left they have to drive. We've chosen this API, again because it's
a part of Googles location APIs and they integrate very well both with eachother
and with Android.

We also have experience with this API from our private phones (Since this is the
API that Google Maps use on a standard Android phone.) and our overall impression
is that it generally estimates very good routes to get from one point to another.

#####Google Places
Motivate why.

#####Joda Time
The base of our application is keeping track of time, and therefore we need to
have a solid and well tested library that we can trust when dealing with time,
and since we're developing with Java 7 (Android does not support Java 8 at this time)
we do not have a standard time library in Java that feel comfortable with using.

This is were we decided to use Joda Time, it's the newly implemented time library
in Java 8, but separated so it can be used with older versions of Java. It
replaces the 'Date' and 'Time' classes in Java 7.

It has good test coverage and adding to that it is Open Source, which is always
a nice addition to make sure you have full control.

#####JARs instead of SDK
We decided to use the supported JARs supplied for us instead of the SDK, which would have made us flash the ROM onto all the devices on which we wanted to test the app.

#####Android Studio
We furthermore used Android Studio as our main IDE instead of the Eclipse. This choice came natural for us because of the superior support for the Android platform which Android Studio offers (e.g. the gui-editor).
