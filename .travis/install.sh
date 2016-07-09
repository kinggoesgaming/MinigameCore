#!/usr/bin/env bash

###############################
# Get JQ                      #
###############################
echo "Downloading JQ"
wget http://stedolan.github.io/jq/download/linux64/jq
echo "Downloaded JQ"

echo "Installing JQ"
chmod +x ./jq
sudo cp /usr/bin
echo "Installed JQ"
