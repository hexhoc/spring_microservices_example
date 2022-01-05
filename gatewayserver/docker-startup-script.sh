#!/usr/bin/env bash
# Use this script to test if a given TCP host/port are available

usage()
{
    cat << USAGE >&2
Usage:
    $WAITFORIT_cmdname host:port timeout execute_after
USAGE
    exit 1
}

response=$(curl --write-out '%{http_code}' --silent --output /dev/null "$1")

echo "$response"
while [ "$response" != 200 ] ; do
  echo sleeping
  sleep "$2"
  response=$(curl --write-out '%{http_code}' --silent --output /dev/null "$1");
done

echo Connected!

eval "$3"
