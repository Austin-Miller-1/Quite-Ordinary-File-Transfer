"# P2P-Networking" 

To compile:
	javac -d bin -cp ".:/lib/h2-1.4.197.jar" src/p2p/\*.java src/p2p/db/\*.java	src/p2p/udp/\*.java src/p2p/tcp/\*.java (If you're copying the command directly from github)
	javac -d bin -cp ".:/lib/h2-1.4.197.jar" src/p2p/*.java src/p2p/db/*.java src/p2p/udp/*.java src/p2p/tcp/*.java	(If you're copying the command from this file in text document)
	
To run:
	java -cp ./bin;./lib/\* p2p.Main (If you're copying the command directly from github)
	java -cp ./bin;./lib/* p2p.Main	(If you're copying the command from this file in text document)
	java -cp ./bin;./lib/* p2p.CentralServer
	java -cp ./bin;./lib/* p2p.ClientMain
	
	Goals for this project:
	
	
	- Database Integration (Completed!)
	
	- GUI
	- Successfully Multithreaded 
	- Test Driven using JUnit 5
	- Original Code is Refactored
	- Pipelined Data Transfer impllemented instead of Stop-And-Wait
	- Full JavaDoc created 