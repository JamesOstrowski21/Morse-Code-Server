# Morse-Code-Server

## Problem Statement
Write a networked application that converts between Morse code and its English-language equivalent. The client should send either 
Morse code or an English sentence. The server should then convert the message to its counterpart and send it back to the client. 
Use one space between each morse code letter and three spaces between each word.

## Developer Documentation
This program consists of 4 classes; MorseCodeServer.java, MorseCodeClient.java, ServerTest.java, and ClientTest.java. The MorseCodeServer sets up the GUI and all the foundation parts required to set up a connection to clients, send and receive data, as well as the method that converts the string given received by the server from the client to either morse code or English. The MorseCodeClient class sets up the client GUI as well as the necessary information like the port, IP, and host in order to connect to the server. The Client class also sets up the ability to text a message to the server as well as be able to revive messages from the server. The ServerTest consists of the main method that creates a server and runs it. The ClientTest Class consists of the main method that creates a client and runs it.

## User Documentation
When using this program you must first run the ServerTest class and generate the server that will be used. You will see a "waiting for connection" message, next you are going to run the ClientTest class and wait to see the "Connection Successful" message. Once the client is up and running you can type in the text box in either morse code or English using capital letters. The server will immediately respond with the conversion to either English or Morse Code depending on what you send to the server. You can also still type a message from the server and send it to the Client.

