#include <ESP8266WiFi.h>

const char *ssid = "SounderBox";
const char *pass = "SounderBox";

WiFiServer server(6200);
WiFiClient serverClients[3];
void setup() {
  Serial.begin(9600);
  WiFi.softAP(ssid,pass);
  server.begin();
  server.setNoDelay(true);
  Serial.println(WiFi.softAPIP());
}

void loop() {
  if(server.hasClient()){
    uint8_t i =0;
    for( i = 0; i<3 ; i++){
      if(!serverClients[i] || !serverClients[i].connected()){
        if(serverClients[i])serverClients[i].stop();
        serverClients[i]=server.available();
        break;
        }
      }
      if(i==3){
        WiFiClient serverClient = server.available();
        serverClient.stop();
        
        }
    }
    for (uint8_t i=0;i<3;i++){
      if(serverClients[i] && serverClients[i].connected()){
        if(serverClients[i].available()){
          while(serverClients[i].available())Serial.print(serverClients[i].read());
          }
        }
      }
}
