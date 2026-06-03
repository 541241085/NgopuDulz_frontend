package com.hanna.ngopidulz_frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AdminProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Nyambungin ke desain XML Profil Admin
        return inflater.inflate(R.layout.fragment_admin_profile, container, false)
    }
}