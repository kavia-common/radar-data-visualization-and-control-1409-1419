#!/bin/bash
cd /home/kavia/workspace/code-generation/radar-data-visualization-and-control-1409-1419/radar_android_app
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

