package com.example.proyectovisualestfg.ViewModels

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log10

class RecorderViewModel : ViewModel(){

    private var recorder: AudioRecord?=null
    private val _uiState: MutableLiveData<RecorderState> = MutableLiveData(RecorderState())
    val uiState: LiveData<RecorderState> = _uiState

    init {
        Log.d("Record State",uiState.value.toString())
    }

    @SuppressLint("MissingPermission")
    fun initializeRecorder(){
        if(recorder==null){
            recorder=AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(AudioFormat.Builder()
                    .setEncoding(ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build())
                .setBufferSizeInBytes(1024)
                .build()
        }
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
    fun startRecording(){
        if (_uiState.value?.isRecording==true){
            return
        }
        initializeRecorder()
        recorder?.startRecording()

        _uiState.value= RecorderState(isRecording = true)
        viewModelScope.launch {
            while (_uiState.value?.isRecording==true){
                val buffer = ShortArray(1024)
                val bytesRead = recorder?.read(buffer, 0, 1024)
                if (bytesRead != null) {
                    if (bytesRead > 0) {
                        val rms = calculateRMS(buffer, bytesRead)
                        val decibels = 20 * log10(rms)
                        _uiState.value=uiState.value?.copy(decibels = decibels)
                        //Log.d("Decibels",decibels.toString())
                        _uiState.value= RecorderState(isRecording = true,decibels = decibels)
                    }
                }
                withContext(Dispatchers.IO) {
                    Thread.sleep(100)
                }
            }
        }
        //val db:Int=audioRecord.maxAmplitude

        Log.d("Start Button",uiState.value?.isRecording.toString())
        //Log.d("Decibels",db.toString())
    }

    fun stopRecording(){
        if (_uiState.value?.isRecording==false){
            return
        }
        _uiState.value= RecorderState(isRecording = false)

        recorder?.stop()
        recorder?.release()
        recorder=null

        Log.d("Stop Button", uiState.value?.isRecording.toString())
    }
}