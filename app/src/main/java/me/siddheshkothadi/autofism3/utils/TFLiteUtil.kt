package me.siddheshkothadi.autofism3.utils

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object TFLiteUtil {
    @Throws(IOException::class)
    fun loadModelFile(context: Context): MappedByteBuffer? {
        val fileDescriptor = context.assets.openFd("rgbhsvtoph.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declareLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength)
    }

    fun doInference(inp: FloatArray, tflite: Interpreter): Float {
        val output = Array(1) {
            FloatArray(
                1
            )
        }
        tflite.run(inp, output)
        return output[0][0]
    }

    fun doInference(inp: Array<Array<FloatArray>>, tflite: Interpreter): Float {
        val output = Array(1) {
            FloatArray(
                1
            )
        }
        tflite.run(inp, output)
        return output[0][0]
    }
}