# LoRa - Air Quality, Temperature and Humidity Monitoring Outdoor

## How it Works?
This system is divided into two parts which have their respective functions, namely receiver and sender. The sender section functions to collect data that will be sent to the receiver, after the data is received by the receiver the data will be sent to MQTT, then Android that has subscribed will in real-time get data from MQTT to be displayed and sent back to the flask server to get a classification response air quality, Before the response is sent, the flask server will first send the data to a spreadsheet containing 5 data, namely current time data, temperature data, humidity, PPM and data categories. Data categories are obtained from the classification results, after all the processes are complete the data will be displayed on Android.

## System
![alt text](https://github.com/susatyo441/LoRa-Monitoring/blob/main/images/Screenshot_141.jpg?raw=true)

## Components
- Arduino R3
- NodeMCU ESP8266
- MQ-135 Sensor
- DHT-11
- LoRa Ra-02 Ra02 SX1278 (x2)
- led

## Pin Connections
- LoRa Node Sender
<br>
<img src="https://github.com/susatyo441/LoRa-Monitoring/blob/main/images/Screenshot_143.jpg" alt="drawing" width="300"/>
<br>
- LoRa Node Receiver
<br>
<img src="https://github.com/susatyo441/LoRa-Monitoring/blob/main/images/Screenshot_142.jpg" alt="drawing" width="300"/>
<br>

## Android
<img src="https://github.com/susatyo441/LoRa-Monitoring/blob/main/images/android.jpg" alt="drawing" width="200"/>

## Final Product
- LoRa Node Sender
  <br>
  <img src="https://github.com/susatyo441/LoRa-Monitoring/blob/main/images/1.jpg" alt="drawing" width="200"/>
  <br>
- LoRa Node Receiver
  <br>
  <img src="https://github.com/susatyo441/LoRa-Monitoring/blob/main/images/2.jpg" alt="drawing" width="200"/>
