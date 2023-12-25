#include <SPI.h>
#include <LoRa.h>
#include <dht.h>

dht DHT;

#define DHT11_PIN 6
const int SENSORUDARA = A0;

int sensorValue;
int digitalValue;

void setup() {
  Serial.begin(9600);  // sets the serial port to 9600
  pinMode(SENSORUDARA, INPUT);

  while (!Serial)
    ;
  Serial.println("LoRa Sender");
  if (!LoRa.begin(433E6)) {  // or 915E6, the MHz speed of yout module
    Serial.println("Starting LoRa failed!");
    while (1)
      ;
  }
}

void loop() {
  sensorValue = analogRead(SENSORUDARA);  // read analog input pin 0
  int chk = DHT.read11(DHT11_PIN);

  Serial.print("PPM =");  //kirim serial "Suhu"
  Serial.println(sensorValue);
  Serial.print("Temperature = ");
  Serial.println(DHT.temperature);
  Serial.print("Humidity = ");
  Serial.println(DHT.humidity);
  LoRa.beginPacket();
  LoRa.print(sensorValue);
  LoRa.print(",");
  LoRa.print(DHT.temperature);
  LoRa.print(",");
  LoRa.print(DHT.humidity);
  LoRa.endPacket();
  delay(10000);  // wait 10 seconds for next reading
}