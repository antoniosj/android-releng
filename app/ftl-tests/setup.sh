#!/bin/sh

if [ "$GCLOUD" = "" ]; then
  echo "GCLOUD env variable is empty. Exiting."
  exit 1
fi

# Export to secrets file
echo $GCLOUD | base64 -di > client-secret.json

# Set project ID
gcloud config set project android-releng

# Auth account gcloud api
gcloud auth activate-service-account firebase-adminsdk-o1bzs@android-releng.iam.gserviceaccount.com --key-file client-secret.json

# Delete secret
rm client-secret.json
