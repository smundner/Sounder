#include <ESP8266WiFi.h>

const char *ssid = "SounderBox";
const char *pass = "SounderBox";

int old = 0;
IPAddress alarmServer;

WiFiServer server(6200);
WiFiClient serverClients;
void setup() {
  Serial.begin(9600);
  Serial.println(WiFi.softAP(ssid, pass, 3));
  server.begin();
  server.setNoDelay(true);
  Serial.println(WiFi.softAPIP());
  pinMode(D0,INPUT_PULLUP);
  pinMode(D1,INPUT_PULLUP);
  pinMode(D2,INPUT_PULLUP);
  pinMode(D3,INPUT_PULLUP);
}

void loop() {

  int  refreshed = WiFi.softAPgetStationNum();
  if (refreshed != old) {
    Serial.print("Station are connected: ");
    Serial.println(refreshed);
    old = refreshed;
  }

  if (server.hasClient()) {
    if (!serverClients || !serverClients.connected()) {
      if (serverClients)serverClients.stop();
      serverClients = server.available();
      Serial.println("Client verbunden");
    }
  }

  if (serverClients && serverClients.connected()) {
    if (serverClients.available()) {
      alarmServer = serverClients.remoteIP();
      Serial.print("Message from ");
      Serial.println(alarmServer);
      while (serverClients.available())Serial.print(char(serverClients.read()));
    }
  }

  if (Serial.available()) {
    Serial.println("Manuele eingabe erfolgt");
    serverClients.connect(alarmServer,6201);
      Serial.println("Schreibe daten");
      while (Serial.available()) {
        serverClients.write(Serial.read());
      }
      serverClients.write("\n");
      serverClients.flush();
      serverClients.stop();
      Serial.println("Alarm gesendet");
    
  }
}
