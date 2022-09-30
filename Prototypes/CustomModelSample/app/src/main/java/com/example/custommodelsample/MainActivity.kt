package com.example.custommodelsample

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import com.example.custommodelsample.databinding.ActivityMainBinding
import java.nio.ByteBuffer

import android.support.v4.app.RemoteActionCompatParcelizer
import android.widget.Button
import android.widget.Toast
import com.example.custommodelsample.ml.GestureModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : Activity() {
    //val initializeTask: Task<Void> by lazy { TfLite.initialize(this) }

    private lateinit var binding: ActivityMainBinding
    //private lateinit var interpreter: InterpreterApi
    private lateinit var byteBuffer: ByteBuffer;
    private val input = FloatArray(127*3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        /*
        val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(GyroscopeListener(fun(x: Float,y:Float,z:Float){
            binding.gyroscopeText.text = "Gyroscope Readings $x $y $x"
        }), gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
         */


        binding.btnStart.setOnClickListener{
            /*
            //Sample data input
            val input = floatArrayOf(
                0.093848F, 0.017482F, 0.040462F
                , 0.051772F, 0.023126F, 0.091997F
                , (-0.010960).toFloat(), 7.14E-4F, 0.110314F
                , (-0.026325).toFloat(), 2.31E-4F, 0.098525F
                , 0.021479F, 0.037321F, 0.060882F
                , 0.108017F, 0.05672F, 0.009292F
                , 0.190206F, 0.044278F, (-0.010788).toFloat()
                , 0.242959F, 0.030573F, (-0.010135).toFloat()
                , 0.162297F, 0.040445F, 0.002206F
                , (-0.026234).toFloat(), 0.039325F, 0.057314F
                , (-0.096677).toFloat(), 0.014513F, 0.162804F
                , 0.024819F, 0.001043F, 0.236763F
                , 0.102757F, (-0.024144).toFloat(), 0.232668F
                , (-0.054112).toFloat(), (-0.076696).toFloat(), 0.221609F
                , (-0.327729).toFloat(), (-0.148086).toFloat(), 0.209257F
                , (-0.676103).toFloat(), (-0.282418).toFloat(), 0.218079F
                , (-1.132153).toFloat(), (-0.464217).toFloat(), 0.234448F
                , (-1.730219).toFloat(), (-0.745399).toFloat(), 0.119206F
                , (-2.351783).toFloat(), (-1.020945).toFloat(), (-0.138667).toFloat()
                , (-2.865709).toFloat(), (-1.167982).toFloat(), (-0.324412).toFloat()
                , (-3.419902).toFloat(), (-1.448268).toFloat(), (-0.329777).toFloat()
                , (-3.926553).toFloat(), (-1.696709).toFloat(), (-0.187715).toFloat()
                , (-4.067696).toFloat(), (-1.847996).toFloat(), 0.041928F
                , (-4.026989).toFloat(), (-2.130944).toFloat(), 0.111313F
                , (-4.111197).toFloat(), (-2.290512).toFloat(), (-0.162056).toFloat()
                , (-4.017880).toFloat(), (-2.445096).toFloat(), (-0.645094).toFloat()
                , (-3.590386).toFloat(), (-2.675359).toFloat(), (-1.091601).toFloat()
                , (-3.252330).toFloat(), (-3.097090).toFloat(), (-1.599459).toFloat()
                , (-3.258639).toFloat(), (-3.453562).toFloat(), (-2.039614).toFloat()
                , (-2.863009).toFloat(), (-3.469863).toFloat(), (-2.128949).toFloat()
                , (-1.564414).toFloat(), (-3.296484).toFloat(), (-1.722463).toFloat()
                , (-0.274149).toFloat(), (-2.858466).toFloat(), (-0.929162).toFloat()
                , 0.958132F, (-2.322059).toFloat(), (-0.047264).toFloat()
                , 2.954368F, (-1.464193).toFloat(), 0.524801F
                , 4.90352F, (-0.322643).toFloat(), 0.338123F
                , 5.313217F, 0.364579F, (-0.215063).toFloat()
                , 4.848571F, 0.736637F, (-0.510305).toFloat()
                , 4.65074F, 0.773671F, (-0.346114).toFloat()
                , 4.615873F, 0.643593F, (-0.091405).toFloat()
                , 4.590767F, 0.819427F, (-0.053217).toFloat()
                , 4.722916F, 1.108728F, (-0.215318).toFloat()
                , 5.003703F, 1.436458F, (-0.187559).toFloat()
                , 5.529147F, 1.818467F, 0.159913F
                , 5.848415F, 2.068639F, 0.606409F
                , 5.588629F, 2.07167F, 0.985915F
                , 5.050917F, 1.77769F, 1.018467F
                , 4.557933F, 1.491019F, 0.783937F
                , 4.099533F, 1.293967F, 0.495741F
                , 3.694309F, 0.975075F, 0.100993F
                , 3.306401F, 0.800736F, (-0.416050).toFloat()
                , 3.026968F, 0.800819F, (-0.783948).toFloat()
                , 2.858743F, 0.74245F, (-0.943743).toFloat()
                , 2.655356F, 0.625244F, (-0.913938).toFloat()
                , 2.481023F, 0.544995F, (-0.729168).toFloat()
                , 2.281924F, 0.383133F, (-0.515379).toFloat()
                , 2.011961F, 0.217008F, (-0.416403).toFloat()
                , 1.556964F, (-0.076772).toFloat(), (-0.594769).toFloat()
                , 0.891936F, (-0.417881).toFloat(), (-0.951284).toFloat()
                , 0.331026F, (-0.623849).toFloat(), (-1.108480).toFloat()
                , 0.180668F, (-0.696091).toFloat(), (-0.812391).toFloat()
                , 0.113681F, (-0.727957).toFloat(), (-0.335290).toFloat()
                , (-0.111991).toFloat(), (-0.691330).toFloat(), 0.036718F
                , (-0.350985).toFloat(), (-0.733444).toFloat(), 0.199726F
                , (-0.597571).toFloat(), (-0.866402).toFloat(), 0.122135F
                , (-0.830844).toFloat(), (-0.929974).toFloat(), (-0.151834).toFloat()
                , (-0.797487).toFloat(), (-0.922447).toFloat(), (-0.412888).toFloat()
                , (-0.606629).toFloat(), (-0.856938).toFloat(), (-0.449107).toFloat()
                , (-0.507409).toFloat(), (-0.804587).toFloat(), (-0.332429).toFloat()
                , (-0.458899).toFloat(), (-0.798045).toFloat(), (-0.211110).toFloat()
                , (-0.591957).toFloat(), (-0.889345).toFloat(), (-0.113720).toFloat()
                , (-1.004690).toFloat(), (-1.032736).toFloat(), (-0.044497).toFloat()
                , (-1.494194).toFloat(), (-1.114727).toFloat(), (-0.027086).toFloat()
                , (-1.882500).toFloat(), (-1.152259).toFloat(), (-0.035705).toFloat()
                , (-2.012199).toFloat(), (-1.148735).toFloat(), (-0.087004).toFloat()
                , (-1.821257).toFloat(), (-1.057551).toFloat(), (-0.151922).toFloat()
                , (-1.387185).toFloat(), (-0.904017).toFloat(), (-0.167926).toFloat()
                , (-0.921776).toFloat(), (-0.789992).toFloat(), (-0.173218).toFloat()
                , (-0.747127).toFloat(), (-0.765858).toFloat(), (-0.231434).toFloat()
                , (-0.832759).toFloat(), (-0.768410).toFloat(), (-0.306448).toFloat()
                , (-0.873360).toFloat(), (-0.759531).toFloat(), (-0.287294).toFloat()
                , (-0.838637).toFloat(), (-0.715058).toFloat(), (-0.164966).toFloat()
                , (-0.834070).toFloat(), (-0.613843).toFloat(), 0.036022F
                , (-0.812737).toFloat(), (-0.509135).toFloat(), 0.275611F
                , (-0.800611).toFloat(), (-0.421173).toFloat(), 0.471207F
                , (-0.864107).toFloat(), (-0.337374).toFloat(), 0.567374F
                , (-0.923049).toFloat(), (-0.294496).toFloat(), 0.529166F
                , (-0.999866).toFloat(), (-0.323393).toFloat(), 0.342395F
                , (-1.047710).toFloat(), (-0.308577).toFloat(), 0.174867F
                , (-0.876164).toFloat(), (-0.253621).toFloat(), 0.157574F
                , (-0.607240).toFloat(), (-0.183201).toFloat(), 0.205661F
                , (-0.416742).toFloat(), (-0.134297).toFloat(), 0.26569F
                , (-0.407595).toFloat(), (-0.105464).toFloat(), 0.300175F
                , (-0.504252).toFloat(), (-0.053052).toFloat(), 0.342633F
                , (-0.419149).toFloat(), 0.004579F, 0.401257F
                , (-0.124698).toFloat(), 0.076294F, 0.388063F
                , 0.168874F, 0.173632F, 0.282442F
                , 0.310214F, 0.216029F, 0.108081F
                , 0.12816F, 0.17919F, (-0.048839).toFloat()
                , (-0.261196).toFloat(), 0.095326F, (-0.176356).toFloat()
                , (-0.539352).toFloat(), 0.014427F, (-0.193322).toFloat()
                , (-0.617910).toFloat(), (-0.031609).toFloat(), (-0.060558).toFloat()
                , (-0.530926).toFloat(), (-0.040267).toFloat(), 0.081178F
                , (-0.101363).toFloat(), (-0.020849).toFloat(), 0.21008F
                , 0.425247F, 0.093634F, 0.309338F
                , 0.757749F, 0.180391F, 0.286575F
                , 0.754893F, 0.232296F, 0.150852F
                , 0.585863F, 0.222451F, 0.053181F
                , 0.505645F, 0.191295F, 0.023963F
                , 0.30676F, 0.163025F, (-0.012268).toFloat()
                , (-0.101250).toFloat(), 0.047078F, (-0.095538).toFloat()
                , (-0.506617).toFloat(), (-0.051181).toFloat(), (-0.202063).toFloat()
                , (-0.779778).toFloat(), (-0.114732).toFloat(), (-0.223805).toFloat()
                , (-0.589824).toFloat(), (-0.081670).toFloat(), (-0.157879).toFloat()
                , 0.099803F, (-0.028821).toFloat(), (-0.115486).toFloat()
                , 0.804784F, 0.111678F, (-0.066391).toFloat()
                , 1.13056F, 0.209522F, (-0.026589).toFloat()
                , 1.008704F, 0.191347F, 0.08001F
                , 0.592566F, 0.118485F, 0.217626F
                , 0.132606F, 0.051349F, 0.300304F
                , (-0.301291).toFloat(), (-0.017030).toFloat(), 0.281468F
                , (-0.651707).toFloat(), (-0.082136).toFloat(), 0.186712F
                , (-0.782802).toFloat(), (-0.158450).toFloat(), (-0.011247).toFloat()
                , (-0.618743).toFloat(), (-0.179945).toFloat(), (-0.245694).toFloat()
                , (-0.270865).toFloat(), (-0.142270).toFloat(), (-0.416262).toFloat()
                , 0.080792F, (-0.086083).toFloat(), (-0.452006).toFloat()
                , 0.398339F, (-0.039003).toFloat(), (-0.366571).toFloat()
                , 0.592917F, (-0.000958).toFloat(), (-0.212623).toFloat()
            )
            val prediction = Predict(input)
             */

            sensorManager.registerListener(AccelerometerListener(fun(x: Float,y:Float,z:Float){
                for (i in 0..380 step 3){
                    input[i] = x
                    input[i+1] = y
                    input[i+2] = z
                }

                //binding.accelerometerText.text = "Accelerometer Readings $x $y $x"
            }), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            sensorManager.unregisterListener(AccelerometerListener(fun(x: Float,y:Float,z:Float){}))

            val model = GestureModel.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 127, 3), DataType.FLOAT32)
            //inputArray = value
            inputFeature0.loadArray(input)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val predictedValue = outputFeature0.getFloatValue(0)
            val predictedVal = outputFeature0.getFloatValue(1)

            // Releases model resources if no longer used.
            model.close()

            binding.accelerometerText.text = predictedValue.toString()
            binding.gyroscopeText.text = predictedVal.toString()
        }
    }

    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(event: SensorEvent) {

        }
    }
}

/*
fun Predict(value: FloatArray) : FloatArray{
    val model = GestureModel.newInstance(this)

    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 127, 3), DataType.FLOAT32)
    //inputArray = value
    inputFeature0.loadArray(value)

    // Runs model inference and gets result.
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    val predictedValue = outputFeature0.floatArray

    // Releases model resources if no longer used.
    model.close()

    return predictedValue
}
 */

enum class DetectorState {
    Idle,
    Recording,
    Detecting,
}

private class Detector{
    var currentState: DetectorState = DetectorState.Idle;

    fun Idle(){

    }

    fun Recording(){
        currentState = DetectorState.Recording;
        var recordingState = RecordingState()

    }

    fun Detecting(){
        currentState = DetectorState.Detecting;
    }
}

private class RecordingState{
    fun StartRecording(){

    }

    fun StopRecording(){

    }
}

private class DetectingState(){
    fun Detect(){

    }
}


private class AccelerometerListener(val callback: (Float, Float, Float) -> Unit)  : SensorEventListener{

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0];
        val y = event.values[1];
        val z = event.values[2];

        callback(x,y,z)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}

private class GyroscopeListener(val callback: (Float, Float, Float) -> Unit) : SensorEventListener{
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0];
        val y = event.values[1];
        val z = event.values[2];

        callback(x,y,z)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}