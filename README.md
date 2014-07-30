<img src="http://openclipart.org/people/Scout/Chick.svg" width="10%" height="10%"/> pIoT-SW
===========================================================================================


pIoT is an open source pico/personal framework for the Internet of Things (IoT).
It includes a hardware design for low-cost, low-power, Arduino compatible boards, a C++ library for programming the board and a simple server application that stores data and offers web visualization.


This repo contains the code of the server application.

Requirements:
-------------

1.  the program must communicate with one central pIoT board (called the base) on a serial link
2.  it must offer a web interface to visualize data and configure logic
3.  it must allow sending commands to the pIoT boards
4.  the server shall allow programming rules for triggering actions when certains situations happen


Design decisions:
-----------------

1.  the server is written in Java
2.  the web interface is programmed with GWT so that no HTML or javascript is required in principle
3.  visualizing received data shall be possible without having an ad-hoc visualizer for all kinds of classes. This is possible thanks to reflection
4.  communication between the base and the server happens through JSON messages
5.  a Java POJO has to be created for every kind of message that can be communicated with the base. Conversion between POJOs and JSON text is automatic
6.  the database is object oriented (no SQL)


Content of the packages:
------------------------

*  pIoT: contains the xml description of the server
*  pIoT.client: contains the client side of the server, that is, the web UI
*  pIoT.client.services: the definition of the interfaces of the client-server functionalities, and the asynchronous methods, as dictated by GWT
*  pIoT.client.events: client side custom events, used to make parts of the application communicate to each other
*  pIoT.server: server side implementation of the services
*  pIoT.shared: shared classes between server and client side, all classes here have to be serializable, and is some cases annotated with @Reflectable
*  pIoT.shared.messages: messages sent by nodes
*  pIoT.shared.actions: actions triggered at the server


extra folders:
*  libs: contains external libraries used
*  war: contains the content of the compiled servlet packages

Implementation of messages:
---------------------------

For using the server with your pIoT sketches, you need to model all the JSON messages that you send/receive with your Base node as java classes.
This requires you to open the source code of the server and add your classes.
For setting up Eclipse:
*  Install a [Java Development Kit](http://java.sun.com/javase/downloads/).
*  Download a [stable version of Eclipse](http://www.eclipse.org/downloads/).
*  Install the [GWT development kit](http://www.gwtproject.org/gettingstarted.html) or set up the [GWT plugin in Eclipse](http://www.gwtproject.org/usingeclipse.html) (recommended choice).

If you use Google Chrome and you want to install the GWT development mode plugin in Chrome and Windows 8 [you may find some problems](http://stackoverflow.com/questions/19059544/how-to-install-gwt-browser-plugin).

Download the project from the GIT repository and import it in Eclipse.

Now, for messages received from pIoT nodes you need to model them in the following way:
* Put them into pIoT.shared.messages package, or a subpackage.
* Extend class DataMessage.
* Make sure is has a public constructor with no arguments.
* Create getters and setters for all properties, Java bean style.
* Do not created nested types, always declare classes separately.
* If you include extra classes in your message, make sure these implement Serializable and Reflectable.
* Do not use arrays, use some implementation of List instead (e.g. ArrayList).
* When importing other types, make sure they are serializable.
* add your data message classes into SerialServiceImpl, in the constructor:
    public SerialServiceImpl(){
        //ADD HERE DATA MESSAGES CLASSES
        ObjectParser.addClassType(DataMessage.class);
        ObjectParser.addClassType(MyDataMessage.class); <--- ADD YOUR CLASS HERE

For messages sent from the server to the nodes, you need to follow these rules:
* Extend class ActionMessage instead of DataMessage.
* Follow the same rules about implemented interfaces and nested classes as for data messages.
* Add your action message examples into ActionsServiceImpl, in the constructor
    public ActionsServiceImpl() {
        //ADD HERE ACTION MESSAGES CLASSES
        actionMessageExamples.add(new MyActionMessage(10, 5.5, true));  <--- ADD YOUR CLASS HERE
    }

Now the server should be able to parse your JSON messages and visualize them correctly.
Be sure to test it well, as it's easy to make mistakes at this point.

Deployment:
-----------

for a light standalone server you can use [Jetty](http://www.eclipse.org/jetty/).

*  Download a recent stable distribution of jetty, then unzip its folder.
*  In Eclipse, right button on the project's icon -> Google -> GWT compile
*  Copy the war folder of the Eclise project into webapps folder of jetty.
*  Rename the folder from war to pIoT.
*  Execute java -jar start.jar in a console at the root folder of jetty. If you want logging support on file execute java -DVERBOSE -jar start.jar etc/jetty-logging.xml
*  On your browser, go to http://localhost:8080/pIoT/

have fun!

