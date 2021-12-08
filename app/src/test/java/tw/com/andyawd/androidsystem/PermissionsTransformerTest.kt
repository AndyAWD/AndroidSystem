package tw.com.andyawd.androidsystem

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionsTransformerTest {
    @Test
    fun permissions_text_return_storage() {
        val text =
            EasyPermissionsTextTransformer().getPermissionsText(EasyPermissionsTextTransformer.PERMISSION_CAMERA)
        assertEquals(EasyPermissionsTextTransformer.CAMERA, text)
    }
}