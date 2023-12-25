package com.overdevx.iottubes

import android.util.Log
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence

/**
 * 管理mqtt的连接,发布,订阅,断开连接, 断开重连等操作
 *
 * @author LichFaker on 16/3/24.
 * @Email lichfaker@gmail.com
 */
class MqttManager private constructor() {
    // Private instance variables
    private var client: MqttClient? = null
    private var conOpt: MqttConnectOptions? = null
    private val clean = true

    // callback
    private val mCallback: MqttCallbackBus

    init {
        mCallback = MqttCallbackBus()
    }

    /**
     * Create Mqtt connection
     *
     * @param brokerUrl Mqtt Broker(tcp://xxxx:1883)
     * @param userName
     * @param password
     * @param clientId  clientId
     * @return
     */
    fun creatConnect(
        brokerUrl: String?,
        userName: String?,
        password: String?,
        clientId: String?
    ): Boolean {
        var flag = false
        val tmpDir = System.getProperty("java.io.tmpdir")
        val dataStore = MqttDefaultFilePersistence(tmpDir)
        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = MqttConnectOptions()
            conOpt!!.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
            conOpt!!.isCleanSession = clean
            if (password != null) {
                conOpt!!.password = password.toCharArray()
            }
            if (userName != null) {
                conOpt!!.userName = userName
            }

            // Construct an MQTT blocking mode client
            client = MqttClient(brokerUrl, clientId, dataStore)

            // Set this wrapper as the callback handler
            client!!.setCallback(mCallback)
            flag = doConnect()
        } catch (e: MqttException) {
            Log.d("Error cak", e.message!!)
        }
        return flag
    }

    /**
     * establish connection
     *
     * @return
     */
    fun doConnect(): Boolean {
        var flag = false
        if (client != null) {
            try {
                client!!.connect(conOpt)
                Log.d(
                    "Error cak konek e",
                    "Connected to " + client!!.serverURI + " with client ID " + client!!.clientId
                )
                flag = true
            } catch (e: Exception) {
            }
        }
        return flag
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param topicName the name of the topic to publish to
     * @param qos       the quality of service to delivery the message at (0,1,2)
     * @param payload   the set of bytes to send to the MQTT server
     * @return boolean
     */
    fun publish(topicName: String, qos: Int, payload: ByteArray?): Boolean {
        var flag = false
        if (client != null && client!!.isConnected) {
            Log.d("Error cak : Publish --> ", "Publishing to topic \"$topicName\" qos $qos")

            // Create and configure a message
            val message = MqttMessage(payload)
            message.qos = qos

            // Send the message to the server, control is not returned until
            // it has been delivered to the server meeting the specified
            // quality of service.
            try {
                client!!.publish(topicName, message)
                flag = true
            } catch (e: MqttException) {
            }
        }
        return flag
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     *
     * @param topicName to subscribe to (can be wild carded)
     * @param qos       the maximum quality of service to receive messages at for this subscription
     * @return boolean
     */
    fun subscribe(topicName: String, qos: Int): Boolean {
        var flag = false
        if (client != null && client!!.isConnected) {
            // Subscribe to the requested topic
            // The QoS specified is the maximum level that messages will be sent to the client at.
            // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
            // be downgraded to 1 when delivering to the client but messages published at 1 and 0
            // will be received at the same level they were published at.
            Log.d("Error cak : Subscib e", "Subscribing to topic \"$topicName\" qos $qos")
            try {
                client!!.subscribe(topicName, qos)
                flag = true
            } catch (e: MqttException) {
            }
        }
        return flag
    }

    /**
     * 取消连接
     *
     * @throws MqttException
     */
    @Throws(MqttException::class)
    fun disConnect() {
        if (client != null && client!!.isConnected) {
            client!!.disconnect()
        }
    }

    companion object {
        // 单例
        private var mInstance: MqttManager? = null
        val instance: MqttManager?
            get() {
                if (null == mInstance) {
                    mInstance = MqttManager()
                }
                return mInstance
            }

        /**
         * Release the instance, and the resource it references
         */
        fun release() {
            try {
                if (mInstance != null) {
                    mInstance!!.disConnect()
                    mInstance = null
                }
            } catch (e: Exception) {
            }
        }
    }
}