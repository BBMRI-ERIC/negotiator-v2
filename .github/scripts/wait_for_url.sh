#!/bin/bash -e

URL=$1

# shellcheck disable=SC1083
while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' "$URL")" != "302" ]]; do sleep 2; done
