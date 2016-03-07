#!/bin/bash

echo "update semantic_forms Play! server when code has changed"
SRC=$HOME/src/semantic_forms-sharecop/scala/forms_play/
APP=sharecoop

APPVERS=${APP}-1.0-SNAPSHOT
SBT=sbt

cd $SRC
git pull --verbose
$SBT dist
echo "sofware recompiled!"

cd ~/deploy
kill `cat ${APPVERS}/RUNNING_PID`

# pour garder les logs
rm -r ${APPVERS}_OLD
mv ${APPVERS} ${APPVERS}_OLD

unzip $SRC/target/universal/${APPVERS}.zip

cd ${APPVERS}
ln -s ../TDBsc TDB
ln -s ../TDB2sc TDB2

PORT=9333
echo starting the server on port $PORT
nohup bin/${APP} -J-Xmx100M -J-server -Dhttp.port=$PORT &
