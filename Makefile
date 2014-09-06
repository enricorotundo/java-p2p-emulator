XTERM = -geometry 120x20

default: progetto

all:
	make clean 
	make progetto
	make stop
	make start

progetto:
	javac -Xlint:unchecked ./src/controller/client/Client.java ./src/controller/client/DownloadScheduler.java ./src/controller/server/ClientChecker.java ./src/controller/client/ConnectionChecker.java ./src/controller/client/ClientInterface.java ./src/controller/server/ServerChecker.java ./src/controller/server/Server.java ./src/controller/server/ServerInterface.java ./src/model/client/ClientResources.java ./src/model/server/ConnectedClients.java ./src/model/server/ConnectedServers.java ./src/model/share/Resource.java ./src/starter/ClientStarter.java ./src/starter/ServerStarter.java ./src/view/AbstractBasicFrame.java ./src/view/ClientFrame.java ./src/view/ServerFrame.java -d ./bin

clean: 
	find . -name "*.class" -type f -delete

start:
	cd bin/ && rmiregistry &
	sleep 2
	xterm $(XTERM) -e "cd bin/ && java starter.ServerStarter Razorback1; read" &
	xterm $(XTERM) -e "cd bin/ && java starter.ServerStarter Razorback2; read" &
	sleep 4
	xterm $(XTERM) -e "cd bin/ && java starter.ClientStarter C1 Razorback1 3 A 9 B 9 C 9; read"  &
	xterm $(XTERM) -e "cd bin/ && java starter.ClientStarter C2 Razorback2 3 A 9 D 9; read"  &
	xterm $(XTERM) -e "cd bin/ && java starter.ClientStarter C3 Razorback2 3 E 2 D 9; read"  &

start-client:
	xterm -e "java -cp bin/ starter.ClientStarter C1 Razorback1 3 A 1 B 4 C 6" &

stop:
	killall rmiregistry &
	sleep 1
	killall xterm &
