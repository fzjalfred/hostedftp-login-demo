#!/bin/bash

sudo apt update
sudo apt install -y mysql-client openjdk-17-jdk

curl -LO https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.95/bin/apache-tomcat-9.0.95.tar.gz
sudo mkdir -p /opt/tomcat
sudo tar -xzf apache-tomcat-9.0.95.tar.gz -C /opt/tomcat --strip-components=1

sudo chown -R root:root /opt/tomcat
sudo chmod -R 775 /opt/tomcat

sudo tee /etc/systemd/system/tomcat.service >/dev/null <<EOF
[Unit]
Description=Apache Tomcat 9
After=network.target

[Service]
Type=simple
User=root
Group=root
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
WorkingDirectory=/opt/tomcat
ExecStart=/opt/tomcat/bin/catalina.sh run
ExecStop=/opt/tomcat/bin/catalina.sh stop
SuccessExitStatus=143
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable --now tomcat
sudo systemctl status tomcat --no-pager


# enable https on port 8443： /opt/tomcat/conf/server.xml 
# mysql connection configuration: /opt/tomcat/webapps/hostedftp-login-demo/WEB-INF/classes/db.properties