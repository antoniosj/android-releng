#!/bin/sh

if [ "$GCLOUD_SERVICE_KEY" = "" ]; then
  echo "GCLOUD_SERVICE_KEY env variable is empty. Exiting."
  exit 1
fi

# Export to secrets file
echo $GCLOUD_SERVICE_KEY | base64 -di > client-secret.json

# Auth account gcloud api
gcloud auth activate-service-account firebase-adminsdk-o1bzs@android-releng.iam.gserviceaccount.com --key-file client-secret.json

# Set project ID
gcloud --quiet config set project android-releng

gcloud firebase test android run \
    --type instrumentation \
    --app app/build/outputs/apk/debug/app-debug.apk \
    --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
    --device model=Nexus6P,version=27,locale=en_US,orientation=portrait \
    --timeout 30m

# Delete secret
rm client-secret.json
