package tw.com.andyawd.androidsystem

class PermissionsTransformer {
    companion object {
        const val CALENDAR = "日曆"
        const val CAMERA = "相機"
        const val ACCOUNTS = "聯絡人"
        const val LOCATION = "位置"
        const val AUDIO = "麥克風"
        const val CALLS = "電話"
        const val SENSORS = "人體感應器"
        const val SMS = "簡訊"
        const val STORAGE = "儲存"
        const val ELSE = "未知的權限"

        const val READ_CALENDAR = "android.permission.READ_CALENDAR"
        const val WRITE_CALENDAR = "android.permission.WRITE_CALENDAR"
        const val PERMISSION_CAMERA = "android.permission.CAMERA"
        const val READ_CONTACTS = "android.permission.READ_CONTACTS"
        const val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"
        const val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"
        const val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
        const val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"
        const val RECORD_AUDIO = "android.permission.RECORD_AUDIO"
        const val READ_PHONE_STATE = "android.permission.READ_PHONE_STATE"
        const val CALL_PHONE = "android.permission.CALL_PHONE"
        const val READ_CALL_LOG = "android.permission.READ_CALL_LOG"
        const val WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG"
        const val VOICEMAIL_ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL"
        const val ADD_VOICEMAIL = "android.permission.ADD_VOICEMAIL"
        const val USE_SIP = "android.permission.USE_SIP"
        const val PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS"
        const val BODY_SENSORS = "android.permission.BODY_SENSORS"
        const val RECEIVE_SMS = "android.permission.RECEIVE_SMS"
        const val READ_SMS = "android.permission.READ_SMS"
        const val SEND_SMS = "android.permission.SEND_SMS"
        const val RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH"
        const val RECEIVE_MMS = "android.permission.RECEIVE_MMS"
        const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
        const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    }

    fun getText(perms: String): String {
        when (perms) {
            READ_CALENDAR,
            WRITE_CALENDAR -> return CALENDAR
            PERMISSION_CAMERA -> return CAMERA
            READ_CONTACTS,
            WRITE_CONTACTS,
            GET_ACCOUNTS -> return ACCOUNTS
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION -> return LOCATION
            RECORD_AUDIO -> return AUDIO
            READ_PHONE_STATE,
            CALL_PHONE,
            READ_CALL_LOG,
            WRITE_CALL_LOG,
            VOICEMAIL_ADD_VOICEMAIL,
            ADD_VOICEMAIL,
            USE_SIP,
            PROCESS_OUTGOING_CALLS -> return CALLS
            BODY_SENSORS -> return SENSORS
            RECEIVE_SMS,
            READ_SMS,
            RECEIVE_WAP_PUSH,
            SEND_SMS,
            RECEIVE_MMS -> return SMS
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE -> return STORAGE
            else -> return ELSE
        }
    }

    fun getPermissionsList(perms: List<String>): StringBuilder {
        val permissionsList = HashSet<String>()
        val permissionsText = StringBuilder()

        perms.forEach { it ->
            permissionsList.add(getText(it))
        }

        permissionsList.forEach { it ->
            permissionsText.append(it)
            permissionsText.append("\n")
        }

        return permissionsText
    }
}