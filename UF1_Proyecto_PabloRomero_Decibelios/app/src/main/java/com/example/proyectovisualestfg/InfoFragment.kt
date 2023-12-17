package com.example.proyectovisualestfg

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import com.github.anastr.speedviewlib.DeluxeSpeedView
import com.github.anastr.speedviewlib.SpeedView


class InfoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_info, container, false)
//        val actualSp: Float=110F
//        val sv=view.findViewById<SpeedView>(R.id.speedometer)
//        sv.unit="dB"
//        sv.speedTo(100F)
//        sv.maxSpeed=140f


        return view
    }
}