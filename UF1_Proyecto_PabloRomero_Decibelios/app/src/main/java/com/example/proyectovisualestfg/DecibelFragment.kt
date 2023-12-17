package com.example.proyectovisualestfg

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.proyectovisualestfg.ViewModels.RecorderViewModel
import com.github.anastr.speedviewlib.SpeedView
import kotlin.math.log10


class DecibelFragment : Fragment() {
    private val recordVM: RecorderViewModel by viewModels()
    val REQUEST_RECORD_AUDIO_PERMISSION = 200
    var permisosConcedidos: Boolean = false;

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permisosConcedidos=true
                } else {
                    permisosConcedidos=false
                    return
                }
                return
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_decibel, container, false)
        val velocimetro=view.findViewById<SpeedView>(R.id.speedDometer)
        val texto=view.findViewById<TextView>(R.id.decibeliosTxt)
        val btnStop=view.findViewById<Button>(R.id.stopButton)
        val btnStart=view.findViewById<Button>(R.id.startButton)
        velocimetro.maxSpeed=140F
        velocimetro.unit="db"

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permisosConcedidos = true
        } else {
            // Permiso no concedido, solicita permisos
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            //Si el usuario no concede permisos, se sale de la aplicación
        }


            btnStart.setOnClickListener {
                try {
                    recordVM.startRecording()
                    texto.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    recordVM.uiState.observe(viewLifecycleOwner) { state ->
                        texto.text = state.decibels.toString()
                        if (state.decibels.toFloat() > 85){
                            texto.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                        }
                        velocimetro.speedTo(state.decibels.toFloat())
                    }
                }catch (e:UnsupportedOperationException){
                    texto.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    texto.text = getString(R.string.failed_permission)
                    btnStart.isEnabled=false
                    btnStop.isEnabled=false
                }
            }

            btnStop.setOnClickListener {
                recordVM.stopRecording()
                velocimetro.speedTo(0F)
                texto.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }

        return view
    }
}