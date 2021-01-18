package com.example.placebook.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object ImageUtils {

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename:String){

        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        val bytes = stream.toByteArray()
        ImageUtils.saveBytesToFile(context, bytes, filename)
    }

    private fun saveBytesToFile(context: Context, bytes: ByteArray, filename: String){
        val outputString: FileOutputStream

        try {
            outputString = context.openFileOutput(filename, Context.MODE_PRIVATE)

            outputString.write(bytes)
            outputString.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun loadBitmapFromFile(context: Context, filename: String): Bitmap? {
        val filePath = File(context.filesDir, filename).absolutePath

        return BitmapFactory.decodeFile(filePath)
    }
}