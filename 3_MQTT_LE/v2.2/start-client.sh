#!/bin/bash
sleep 15
exec java -cp "src:lib/*" ds.robot.http.ControllerClient controller-server 1243
