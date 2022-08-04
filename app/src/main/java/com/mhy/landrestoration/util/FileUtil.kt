package com.mhy.landrestoration.util

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileUtil {

    companion object {
        fun writeFileToDownload(fileName: String, data: String): String? {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val dir = File(path)
            if (!dir.exists()) {
                if (!dir.mkdir()) return null
            }
            val file = File(path, fileName)
            if (file.exists()) {
                if (!file.delete()) return null
            }
            try {
                val fos = FileOutputStream(file)
                fos.write(data.toByteArray())
                fos.close()
                return file.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}