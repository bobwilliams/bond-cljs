#!/usr/bin/env bash

WEBKIT_VERSION=0.7.2
DIR=`pwd`
NW_GYP=$DIR/node_modules/.bin/nw-gyp

echo "Patch node-expat..."
cd $DIR/node_modules/node-xmpp/node_modules/node-expat
$NW_GYP configure --target=$WEBKIT_VERSION
$NW_GYP build

echo "Patch lame..."
cd $DIR/node_modules/lame
$NW_GYP configure --target=$WEBKIT_VERSION
$NW_GYP build

echo "Patch speaker..."
cd $DIR/node_modules/speaker
$NW_GYP configure --target=$WEBKIT_VERSION
$NW_GYP build

echo "Finished"