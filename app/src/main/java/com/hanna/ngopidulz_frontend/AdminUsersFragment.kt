package com.hanna.ngopidulz_frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AdminUsersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Nyambungin ke desain XML List Akun
        return inflater.inflate(R.layout.fragment_admin_users, container, false)
    }
}