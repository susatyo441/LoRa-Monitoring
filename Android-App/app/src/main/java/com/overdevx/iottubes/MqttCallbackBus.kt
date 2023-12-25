package com.overdevx.iottubes

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.greenrobot.eventbus.EventBus

//import com.lichfaker.log.Logger;
/**
 * 使用EventBus分发事件
 *
 * @author LichFaker on 16/3/25.
 * @Email lichfaker@gmail.com
 */
class MqttCallbackBus : MqttCallback {
    override fun connectionLost(cause: Throwable) {
        Log.d("Error cak", cause.message!!)
    }

    override fun messageArrived(topic: String, message: MqttMessage) {
        Log.d("Error cak", "$topic====$message")
        EventBus.getDefault().post(message)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {}
}