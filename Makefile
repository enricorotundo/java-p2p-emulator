default: progetto

all:
	make clean
	make progetto
	make stop
	make start

progetto:
	javac ./src/client/Client.java ./src/client/ClientInterface.java ./src/gui/AbstractBasicFrame.java ./src/gui/ClientFrame.java ./src/gui/ServerFrame.java ./src/resource/part/ResourcePart.java ./src/resource/part/ResourcePartInterface.java ./src/resource/part/TransfertStatus.java ./src/resource/Resource.java ./src/resource/ResourceInterface.java ./src/server/Server.java ./src/server/ServerInterface.java -d ./bin

clean: 
	find . -name "*.class" -type f -delete

start:
	rmiregistry &
	sleep 2
	xterm -e "java -cp bin/ server.Server Razorback1" &
	sleep 2
	# xterm -e "java -cp bin/ server.Server Razorback2" &
	# sleep 4
	xterm -e "java -cp bin/ client.Client C1 Razorback1 3 A 1 B 4 C 6" &
	# xterm -e "java -cp bin/ client.Client C2 Razorback2 3 A 1 D 2" &
	# xterm -e "java -cp bin/ client.Client C3 Razorback2 3 E 2 D 2" &

start-client:
	xterm -e "java -cp bin/ client.Client C1 Razorback1 3 A 1 B 4 C 6" &

stop:
	killall rmiregistry &
	sleep 1
	killall xterm &
