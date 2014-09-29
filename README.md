# OTTO: Onroad Time Tracking Optimizer

#### Trucker Boys Project for DAT255 @ Chalmers University of Technology Fall 2014
### Relevant links
<a href="https://waffle.io/eineving/truckerboys">Issue board</a><br />
<a href="https://dashboard.genmymodel.com/projectProperties/_tNRVQDzhEeSn3ZXpVIPWYw">UML Diagram</a>

## Development Instructions


### Installing the SDK

Feel free to use the standard Android SDK. There is one that comes with
Android Studio, or you can download a seperate one from
<a href="http://developer.android.com/sdk/installing/index.html"> here</a>

1. Select the folder where you have stored your SDK in Android Studio <i>(This is done automatically if you've choosen to go with android studios built in SDK)</i>
2. Enter the SDK Manager and install the following: <br />
    Android SDK tools <br />
    Android SDK platform tools <br />
    Android SDK build tools 19 -> 20 (all versions)<br />
    Android SDK 19 and 20 platform
    
3. After the update is finished, restart the SDK manager.
4. Install the following: <br />
    Android support Repository <br />
    Android Suppoirt Library <br />
    Google Play Services <br />
    Google Repository <br />

### Setting up the jar-files

Start by downloading the following jar-files from <a href="http://developer.lindholmen.se/repo/artifacts/">here</a>

### Writing JUnit tests
<i>Check the <a href="https://github.com/eineving/truckerboys/blob/develop/otto/app/src/test/java/truckerboys/otto/TestTemplate.java">"src/test/java/truckerboys/otto/TestTemplate.java"</a> file when reading theese instructions to properly understand them!</i><br /><br />
The important parts when writing a test for Android is the fact that you append the<br />
<i>@Config(emulateSdk = 18)</i> and <i>@RunWith(RobolectricTestRunner.class)</i> before the class definiton, this is <strong>NEEDED</strong> in order for Gradle to be able to
compile and run the tests. Other than this, you write JUnit tests just like you normally would.

<strong>IMPORTANT:</strong> If you want to test android-specific stuff, read up on Robolectric. It adds
functionality in order to test android-elements.

## Course Information

#### Supervision

<a href="https://drive.google.com/file/d/0B3-HBeyIrR6eQnlfX0ZpRnZVYms/edit?usp=sharing">Group contract</a>

<a href="https://groups.google.com/forum/#!forum/cth-dat255-lp1-2014">Google Group</a>

#### References

<a href="https://developer.lindholmen.se/redmine/projects/aga">AGA Home</a>

<a href="https://se-div-c3s-1.ce.chalmers.se:7001/index.cgi">HMI and SDK Guidelines</a><br />
<strong>Username:</strong> student_dat255ht14<br />
<strong>Password:</strong> jaglovarattvaraduktig

##### Project criterias:
<a href="https://github.com/morganericsson/DAT255/wiki/SICS's-app-assessment-criteria">SICS</a>

## Relevant Topics

<a href="https://www.transportstyrelsen.se/Global/Publikationer/Vag/Yrkestrafik/kor_vilotider_utg10_low.pdf">EU Truck Regulations</a> <br />
<a href="http://antonioleiva.com/mvp-android/">MVP-pattern for Android</a>
