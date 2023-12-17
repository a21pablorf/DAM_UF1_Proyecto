package com.example.proyectovisualestfg

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlin.math.log10

class NoiseMeter(private val context: Context?) : AudioRecorder {

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    override fun startRecording() {
        if (audioRecord == null) {
            context?.let {
                if (ContextCompat.checkSelfPermission(
                        it,
                        android.Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Manejar la solicitud de permisos si es necesario
                    return
                }
            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
        }

        isRecording = true
        audioRecord?.startRecording()


            while (isRecording) {
                val buffer = ShortArray(bufferSize)
                val bytesRead = audioRecord?.read(buffer, 0, bufferSize) ?: 0

                if (bytesRead > 0) {
                    val rms = calculateRMS(buffer, bytesRead)
                    val decibels = 20 * log10(rms)
                    println("Nivel de ruido: $decibels dB")
                }

                Thread.sleep(1000) // Actualizar cada segundo
            }

    }

    override fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun calculateRMS(buffer: ShortArray, bytesRead: Int): Double {
        var rms = 0.0
        for (i in 0 until bytesRead) {
            rms += buffer[i].toDouble() * buffer[i].toDouble()
        }
        rms /= bytesRead
        rms = Math.sqrt(rms)
        return rms
    }
}

fun main() {
    val noiseMeter = NoiseMeter(context = null)
    // Iniciar la grabación en un hilo separado
    Thread {
        noiseMeter.startRecording()
        // Esperar 10 segundos antes de detener la medición (esto puede variar según tus necesidades)
        Thread.sleep(10000)
        // Detener la grabación
        noiseMeter.stopRecording()
    }.start()
}