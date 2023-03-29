#!/bin/sh

set -e
cd `dirname ${0}`
cd ..

# maven package build
cd ../airgraph/
mvn package

# copy binary files
cp -r target/airgraph-2.0.0.jar ../release_app/airgraph.jar

exit 0
