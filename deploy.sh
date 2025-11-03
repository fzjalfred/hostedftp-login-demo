#!/bin/bash
sudo cp -f setenv.sh /opt/tomcat/bin/setenv.sh
sudo chmod +x /opt/tomcat/bin/setenv.sh
sudo cp -f hostedftp-login-demo.war /opt/tomcat/webapps/

sudo systemctl restart tomcat
sudo systemctl status tomcat --no-pager