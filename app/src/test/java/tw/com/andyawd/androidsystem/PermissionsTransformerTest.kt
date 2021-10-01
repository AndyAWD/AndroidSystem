package tw.com.andyawd.androidsystem

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionsTransformerTest {
    @Test
    fun permissions_text_return_storage() {
        val text = PermissionsTransformer().getText(PermissionsTransformer.PERMISSION_CAMERA)
        assertEquals(PermissionsTransformer.CAMERA, text)
    }
}