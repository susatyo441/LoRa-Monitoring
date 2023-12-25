package com.overdevx.iottubes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.overdevx.iottubes.API.ApiClient
import com.overdevx.iottubes.databinding.ActivityMainBinding
import com.overdevx.iottubes.getData.dataResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var status_koneksi:Boolean=false
    private  var suhu : Double =0.0
    private  var kelembapan : Double =0.0
    private  var ppm : Double =0.0

    companion object {
        private var lineSet = listOf(
            "label1" to 0f
        )
        private var donutSet = listOf(
            0f,
            100f
        )
        private var donutSet2 = listOf(
            0f,
            500f
        )
        private const val animationDuration = 1000L
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLihat.setOnClickListener {
            val intent =Intent(this@MainActivity,HistoryActivity::class.java)
            startActivity(intent)
        }
        binding.apply {
            lineChart.gradientFillColors =
                intArrayOf(
                    Color.parseColor("#00897B"),
                    Color.TRANSPARENT
                )
            lineChart.animation.duration = animationDuration
            lineChart.onDataPointTouchListener = { index, _, _ ->
                binding.tvDatasuhu.text =
                    lineSet.toList()[index]
                        .second
                        .toString()
            }
            lineChart.animate(lineSet)
        }
        binding.apply {
            donutChart.donutColors = intArrayOf(
                Color.parseColor("#00ACC1"),
                Color.parseColor("#8DFFFFFF")
            )
            donutChart.animation.duration = animationDuration
            donutChart.animate(donutSet)
        }

        binding.apply {
            donutChartppm.donutColors = intArrayOf(
                Color.parseColor("#43A047"),
                Color.parseColor("#8DFFFFFF")
            )
            donutChartppm.animation.duration = animationDuration
            donutChartppm.animate(donutSet2)
        }
        binding.switchConnect.isChecked = false
        binding.switchConnect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this@MainActivity, "Start connection...", Toast.LENGTH_SHORT).show()
                Thread {
                    val b = MqttManager.instance?.creatConnect(
                        "tcp://test.mosquitto.org",
                        "",
                        "",
                        "LOLIHUNTER"
                    )
                    if (b!!) {
                        status_koneksi=true
                        subscribe()
                    }else{
                        status_koneksi=false
                    }
                }.start()
            } else {
                status_koneksi=false
                binding.tvConnect.text="Connect To Server"
                binding.tvDatasuhu.text="0 °C"
                binding.tvDataHumi.text="0 %"
                binding.tvDataPpm.text="0"
                MqttManager.instance?.disConnect()
                Toast.makeText(this@MainActivity, "Disconnected from server", Toast.LENGTH_SHORT).show()
            }
            if(status_koneksi){
                Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                binding.tvConnect.text="Disconnect"
            }
        }
        EventBus.getDefault().register(this@MainActivity)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainActivity)
        super.onDestroy()
    }

    @Subscribe
    fun onEvent(message: MqttMessage) {
        try {
            val jsonObject = JSONObject(message.toString())

            // Mengambil nilai suhu dari topik SUHU
            if (jsonObject.has("suhu")) {
                 suhu = jsonObject.getDouble("suhu")
                binding.tvDatasuhu.text = "$suhu°C"
                // Panggil fungsi untuk mengupdate LineChart
                updateLineChartWithData("labelX", suhu.toFloat())
            }

            // Mengambil nilai kelembapan dari topik KELEMBAPAN
            if (jsonObject.has("kelembapan")) {
                 kelembapan = jsonObject.getDouble("kelembapan")
                binding.tvDataHumi.text = "$kelembapan%"
                updateDonutChartWithHumidity(kelembapan.toFloat())
            }

            if (jsonObject.has("ppm")) {
                 ppm = jsonObject.getDouble("ppm")
                binding.tvDataPpm.text = "$ppm"
                updateDonutChartWithPpm(ppm.toFloat())
                // Buat objek JSON sesuai dengan kebutuhan Anda
                val json = """
            {
                "temp": $suhu,
                "hum": $kelembapan,
                "ppm": $ppm
            }
            """.trimIndent()

                // Buat RequestBody dari JSON
                val requestBody = RequestBody.create(MediaType.parse("application/json"), json)

                // Panggil API menggunakan coroutine
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        // Menunjukkan ProgressBar saat pemanggilan API dimulai
                        runOnUiThread {
                            binding.loading.visibility = View.VISIBLE
                        }
                        val response = ApiClient.retrofit.getPredict(requestBody)
                        // Periksa apakah permintaan berhasil (kode status 2xx)
                        if (response.isSuccessful) {
                            // Ambil nilai prediksi
                            val responseData: dataResponse? = response.body()
                            // Update UI di thread utama
                            responseData?.let {
                                runOnUiThread {
                                    // Set warna teks berdasarkan nilai prediksi
                                    when (it.prediction) {
                                        "Bad" -> binding.tvKlasi.setTextColor(Color.RED)
                                        "Normal" -> binding.tvKlasi.setTextColor(Color.BLUE)
                                        "Great" -> binding.tvKlasi.setTextColor(Color.GREEN)
                                        "Terrible" -> binding.tvKlasi.setTextColor(Color.BLACK)
                                        else -> binding.tvKlasi.setTextColor(Color.BLACK)
                                    }
                                    // Set teks pada TextView
                                    binding.tvKlasi.text ="${it.prediction}"

                                    // Sembunyikan ProgressBar setelah mendapatkan respons
                                    binding.loading.visibility = View.GONE

                                }

                            }



                        } else {
                            // Handle respons gagal (kode status tidak 2xx)
                            // Sembunyikan ProgressBar dalam kasus respons gagal
                            binding.loading.visibility = View.GONE
                            println("Respons gagal dengan kode: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.d("RESPONSE", "HASIL PREDIKSI : kosong")
                    }
                }
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    fun updateDonutChartWithHumidity(humidity: Float) {
        val updatedData = listOf(humidity, 100f) // Sesuaikan dengan format data donut chart Anda

        // Update donutSet
        donutSet = updatedData

        // Update DonutChart
        binding.donutChart.animate(donutSet)
    }

    fun updateDonutChartWithPpm(ppm: Float) {
        // Mengatur nilai maksimum DonutChart menjadi 500
        val maxDonutValue = 500f
        val updatedData = listOf(ppm, maxDonutValue)

        // Memastikan bahwa nilai DonutChart tidak melebihi maksimum yang diinginkan
        val clampedData = updatedData.map { it.coerceIn(0f, maxDonutValue) }

        // Update donutSet
        donutSet2 = clampedData

        // Update DonutChart
        binding.donutChartppm.animate(donutSet2)
    }
    // Tambahkan fungsi berikut ke dalam kelas atau tempat yang sesuai
    fun updateLineChartWithData(label: String, value: Float) {
        val updatedData = lineSet.toMutableList()
        updatedData.add(label to value)

        // Batasi jumlah data yang ditampilkan (sesuaikan sesuai kebutuhan)
        if (updatedData.size > 12) {
            updatedData.removeAt(0)
        }

        // Update lineSet
        lineSet = updatedData.toList()

        // Update LineChart
        binding.lineChart.animate(lineSet)
    }

    // Fungsi untuk melakukan subscribe ke broker MQTT
    private fun subscribe() {
        Thread {
            val smqttsubtopic = "loli/hunter/ti3c/data"
            MqttManager.instance?.subscribe(smqttsubtopic, 2)
        }.start()
    }
}