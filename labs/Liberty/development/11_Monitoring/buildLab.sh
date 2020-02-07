#!/bin/bash +x

####  This script will build you monitoring environment ####
####

WLPHOME=../../../wlp
#HOME=/home/tjmcmanus/buildpot/17001/
$WLPHOME/bin/server stop monitorServer
rm -r $WLPHOME/usr/servers/monitorServer

$WLPHOME/bin/server create monitorServer
cp server.xml $WLPHOME/usr/servers/monitorServer
cp MoneyBank.war $WLPHOME/usr/servers/monitorServer/dropins
$WLPHOME/bin/server start monitorServer
