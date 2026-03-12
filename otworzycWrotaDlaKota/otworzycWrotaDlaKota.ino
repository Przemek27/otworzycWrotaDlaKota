#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

//fixme: hide password
const char* ssid = "SSID";
const char* password = "PASSWORD";

const int ledPin = 2;
const int sensorPin = 4;

ESP8266WebServer server(80);

void handleRoot() {
  server.send(200, "text/plain", "ESP8266 server running");
}

void handleSensor() {
  int sensorState = digitalRead(sensorPin);

  String response = "{ \"sensor\": ";
  response += sensorState;
  response += " }";

  server.send(200, "application/json", response);
}

void handleLedOn() {
  digitalWrite(ledPin, LOW); // active low
  server.send(200, "text/plain", "LED ON");
}

void handleLedOff() {
  digitalWrite(ledPin, HIGH);
  server.send(200, "text/plain", "LED OFF");
}

void setup() {

  Serial.begin(115200);

  pinMode(ledPin, OUTPUT);
  pinMode(sensorPin, INPUT);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  Serial.print("Connecting");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("Connected!");
  Serial.println(WiFi.localIP());

  server.on("/", handleRoot);
  server.on("/sensor", handleSensor);
  server.on("/led/on", handleLedOn);
  server.on("/led/off", handleLedOff);

  server.begin();

  Serial.println("HTTP server started");
}

void loop() {

  server.handleClient();

}
