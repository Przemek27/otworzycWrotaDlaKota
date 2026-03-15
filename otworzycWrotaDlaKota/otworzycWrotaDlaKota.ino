#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

#include "wifi_credentials.h"

#define LED_ON digitalWrite(ledPin, LOW) // active low
#define LED_OFF digitalWrite(ledPin, HIGH)

const int ledPin = 2;
const int sensorPin = 4;

bool sensorState = false;
bool lastSensorState = false;

ESP8266WebServer server(80);

void handleRoot() {
  server.send(200, "text/plain", "ESP8266 server running");
}

void handleSensor() {
  server.send(200, "application/json", "{\"sensor\": " + String(sensorState) + "}");
}

void handleLedOn() {
  LED_ON;
  server.send(200, "text/plain", "LED ON");
}

void handleLedOff() {
  LED_OFF;
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

//continues execution
void loop() {
  server.handleClient();

  //handle sensor, leds
  sensorState = digitalRead(sensorPin);
  if(sensorState != lastSensorState){
    if(sensorState){ LED_ON; }
    else{ LED_OFF; }

    lastSensorState = sensorState;
  }

  //check wifi connection
  if (WiFi.status() != WL_CONNECTED) {
    WiFi.reconnect();
  }
}
