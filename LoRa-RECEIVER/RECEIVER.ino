#include <LoRa.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <WiFiClientSecureBearSSL.h>

const char* mqtt_server = "test.mosquitto.org";
const char* ssid = "singoedan";
const char* password = "jakarta12";

WiFiClient espClient;
const char* mqtt_topic = "loli/hunter/ti3c/data";
PubSubClient client1(espClient);
#define SS 15
#define RST 16
#define DIO0 2
int led = D3;


void sendData(int ppm, float humidity, float temperature) {
  char payload[100];  // Menyesuaikan ukuran buffer sesuai kebutuhan
  int writtenBytes = snprintf(payload, sizeof(payload), "{\"suhu\":%.2f, \"kelembapan\":%.2f, \"ppm\":%d}", temperature, humidity, ppm);

  // Periksa apakah ada error saat membuat payload
  if (writtenBytes < 0 || writtenBytes >= sizeof(payload)) {
    Serial.println("Error creating payload");
    return;
  }

  client1.publish(mqtt_topic, payload);
  Serial.println("Kirim data ke mqtt...");
}

void setup_wifi() {
  delay(10);
  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void reconnect() {
  while (!client1.connected()) {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);

    if (client1.connect(clientId.c_str())) {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client1.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.println("Message arrived");
}

void setup() {
  Serial.begin(9600);
  while (!Serial)
    ;
  Serial.println("Receiver Host");
  setup_wifi();
  pinMode(led, OUTPUT);
  client1.setServer(mqtt_server, 1883);
  client1.setCallback(callback);
  LoRa.setPins(SS, RST, DIO0);
  if (!LoRa.begin(433E6)) {
    Serial.println("LoRa Error");
    while (1)
      ;
  }
}
void loop() {
  if ((WiFi.status() != WL_CONNECTED)) {
    setup_wifi();
  }

  if (!client1.connected()) {
    reconnect();
  }
  client1.loop();

  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    Serial.print("Receiving Data: ");
    while (LoRa.available()) {
      String data = LoRa.readString();
      Serial.println(data);
      String values[3];  // Array untuk menyimpan ppm, temperature, dan humidity
      int index = 0;
      digitalWrite(led, HIGH);
      while (data.length() > 0 && index < 3) {
        int pos = data.indexOf(',');
        if (pos == -1) {
          values[index] = data;
          data = "";
        } else {
          values[index] = data.substring(0, pos);
          data = data.substring(pos + 1);
        }

        index++;
      }

      // Konversi nilai ke tipe data yang sesuai
      int ppm = values[0].toInt();
      float temperature = values[1].toFloat();
      float humidity = values[2].toFloat();
      sendData(ppm, humidity, temperature);
    }
    digitalWrite(led, LOW);
  } else {
    digitalWrite(led, LOW);
  }
}

