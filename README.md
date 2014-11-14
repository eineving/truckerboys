# OTTO: Onroad Time Tracking Optimizer
<div style="width: 800px; text-align: center"><img src="https://github.com/eineving/truckerboys/blob/develop/documentation/images/OTTOLogo.jpg" alt="Logo" height="300px"/></div>


#### Trucker Boys Project for DAT255 @ Chalmers University of Technology Fall 2014

## Building and installing
Instructions and requirements on building and installning the application.

### Requirements
* Java 7
* Android SDK
* An Android Device
* Internet Connection

<i>Please also note that this application has been developed for the Nexus 5 with
an Android version of 4.4.4</i>

### Building
The project uses gradle and there is a gradle file in the ~/otto/directory that
can be used for compiling the project. Place yourself in the ~/otto/directory
and run:

<code>
$ ./gradlew clean build
</code>

This should build the project and output an .apk file in ~/otto/app/build/outputs/apk
directory

### Installing
You now install this application by moving said .apk file into your device and
clicking on it. <i>Simple.</i>

## Course Information

#### Relevant links
<a href="https://waffle.io/eineving/truckerboys" target="_blank">Issue board</a><br />
<a href="https://dashboard.genmymodel.com/projectProperties/_tNRVQDzhEeSn3ZXpVIPWYw" target="_blank">UML Diagram</a>

#### Supervision

<a href="https://drive.google.com/file/d/0B3-HBeyIrR6eQnlfX0ZpRnZVYms/edit?usp=sharing" target="_blank">Group contract</a><br />
<a href="https://groups.google.com/forum/#!forum/cth-dat255-lp1-2014" target="_blank">Google Group</a>

#### References

<a href="https://developer.lindholmen.se/redmine/projects/aga" target="_blank">AGA Home</a><br />
<a href="https://se-div-c3s-1.ce.chalmers.se:7001/index.cgi" target="_blank">HMI and SDK Guidelines</a><br />

##### Project criterias:
<a href="https://github.com/morganericsson/DAT255/wiki/SICS's-app-assessment-criteria" target="_blank">SICS</a>

## Relevant Topics
<a href="https://www.transportstyrelsen.se/Global/Publikationer/Vag/Yrkestrafik/kor_vilotider_utg10_low.pdf" target="_blank">EU Truck Regulations</a> <br />
<a href="http://antonioleiva.com/mvp-android/" target="_blank">MVP-pattern for Android</a>
