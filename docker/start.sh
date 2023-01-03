#!/usr/bin/env bash

set -e
file="${CATALINA_HOME}"/conf/Catalina/localhost/ROOT.xml
conf_file=/etc/bbmri.negotiator/bbmri.negotiator.xml
sed -i "s/localhost/${POSTGRES_HOST}/"                  "$file"
sed -i "s/{AUTH-STATUS}/${AUTH}/" "$conf_file"
sed -i "s%{negotiator-url}%${NEGOTIATOR_URL}%" "$conf_file"
sed -i "s%{AUTH_HOST}%${AUTH_HOST}%" "$conf_file"
sed -i "s%{AUTH_PUBLIC_KEY}%${AUTH_PUBLIC_KEY}%" "$conf_file"
sed -i "s%{AUTH_CLIENT_ID}%${AUTH_CLIENT_ID}%" "$conf_file"
sed -i "s%{AUTH_CLIENT_SECRET}%${AUTH_CLIENT_SECRET}%" "$conf_file"
/usr/local/tomcat/bin/catalina.sh run
