package tw.com.andyawd.androidsystem

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun permissions_text_return_save() {
        val text =
            EasyPermissionsTextTransformer().getPermissionsText(EasyPermissionsTextTransformer.WRITE_EXTERNAL_STORAGE)
        assertEquals(EasyPermissionsTextTransformer.STORAGE, text)
    }
}