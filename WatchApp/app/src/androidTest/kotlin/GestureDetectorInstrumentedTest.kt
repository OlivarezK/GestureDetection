import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.speakbyhand.app.core.GestureCode
import com.speakbyhand.app.core.GestureData
import com.speakbyhand.app.core.GestureDetector
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class GestureDetectorInstrumentedTest {
    lateinit var detector: GestureDetector

    @Before
    fun setup() {
        detector = GestureDetector(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testToiletGestureCanBeDetected(){
        val input = loadResourceAsString("/Toilet_1")
        val data = GestureData(input)
        val result = detector.detect(data)

        assertEquals(GestureCode.Toilet, result);
    }

    @Test
    fun testSample(){
        assertEquals(1, 1);
    }

    @Test
    fun testEatFoodGestureCanBeDetected(){
        val input = loadResourceAsString("/Eat_1")
        val data = GestureData(input)
        val result = detector.detect(data)

        assertEquals(GestureCode.EatFood, result);
    }

    @Test
    fun testDrinkWaterGestureCanBeDetected(){
        val input = loadResourceAsString("/Drink_1")
        val data = GestureData(input)
        val result = detector.detect(data)

        assertEquals(GestureCode.DrinkWater, result);
    }

    @Test
    fun testHelpGestureCanBeDetected(){
        val input = loadResourceAsString("/Help_1")
        val data = GestureData(input)
        val result = detector.detect(data)

        assertEquals(GestureCode.Help, result);
    }

    @Test
    fun testYesGestureCanBeDetected(){
        val input = loadResourceAsString("/Yes_1")
        val data = GestureData(input)
        val result = detector.detect(data)

        assertEquals(GestureCode.Yes, result);
    }

    @Test
    fun testNoGestureCanBeDetected(){
        val input = loadResourceAsString("/No_2")
        val data = GestureData(input)
        val result = detector.detect(data)

        assertEquals(GestureCode.No, result);
    }

     fun loadResourceAsString(fileName: String) : String{
        val scanner = Scanner(GestureDetectorInstrumentedTest::class.java.getResourceAsStream(fileName));
        val contents = scanner.useDelimiter("\\A").next();
        scanner.close();
        return contents;
    }
}

