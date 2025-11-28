#!/bin/sh
echo "******************************************************************"
echo "* We can add certs in this point to the Java keystore if needed  *"
echo "* Starting app...                                                *"

java $JAVA_OPTS -jar /app/app.jar
echo "* Loading app...                                                 *"