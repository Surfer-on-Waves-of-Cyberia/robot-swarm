#!/bin/bash
# Delay start for 10 seconds
sleep 10
exec java -cp "src:lib/*" ds.robot.http.ControllerClient controller-server 1243
