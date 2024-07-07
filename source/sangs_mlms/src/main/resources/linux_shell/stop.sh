ps -ef | grep sangs_mlms-1.0.0-SNAPSHOT.jar | grep -v grep | awk '{print "kill -9",$2}' | sh -v
