sCLI=./src/client

sGUI=./src/gui

sRES=./src/resource

sSRV=./src/server

bCLI=./bin/client

bGUI=./bin/gui

bRES=./bin/resource

bSRV=./bin/server

progetto : $(bCLI)/AbstractBasicFrame.class 

# ClientFrame.class ServerFrame.class Client.class ClientInterface.class ResourcePart.class ResourcePartInterface.class TransfertStatus.class Resource.class ResourceInterface.class Server.class ServerInterface.class   

$(bCLI)/AbstractBasicFrame.class : $(sSRC)/AbstractBasicFrame.java
	javac $(sSRC)/AbstractBasicFrame.java -d ./
	
# ClientFrame.class : ClientFrame.java
# 	javac ClientFrame.java -d ./

# ServerFrame.class : ServerFrame.java
# 	javac ServerFrame.java -d ./

# Client.class : Client.java
# 	javac Client.java -d ./
	
# ClientInterface.class : ClientInterface.java
# 	javac ClientInterface.java -d ./
	
# ResourcePart.class : ResourcePart.java
# 	javac ResourcePart.java -d ./

# ResourcePartInterface.class : ResourcePartInterface.java
# 	javac ResourcePartInterface.java -d ./
	
# TransfertStatus.class : TransfertStatus.java
# 	javac TransfertStatus.java -d ./
	
# Resource.class : Resource.java
# 	javac Resource.java -d ./
	
# ResourceInterface.class : ResourceInterface.java
# 	javac ResourceInterface.java -d ./
	
# Server.class : Server.java
# 	javac Server.java -d ./
	
# ServerInterface.class : ServerInterface.java
# 	javac ServerInterface.java -d ./

clean: 
	rm -Rf *.class

# start: 
# 	rmiregistry &
# 	sleep 2
# 	java Server.Server server1 &
# 	java Server.Server server2 &
# 	sleep 2
# 	java Client.Client client1 server1 3 r1 1 &
# 	java Client.Client client2 server2 5 r4 2 r2 7 g 8 &
# 	java Client.Client client3 server1 5 r1 5 r4 2 g 8 & 
# 	java Client.Client client3 server1 5 r1 5 r4 2 g 8 &

# stop:
# 	killall -q rmiregistry &