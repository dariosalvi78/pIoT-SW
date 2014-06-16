pIoT-SW
=======


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


