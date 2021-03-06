package com.example.duplicatetelegram.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.duplicatetelegram.R
import com.example.duplicatetelegram.activities.MainActivity
import com.example.duplicatetelegram.databinding.FragmentChatsBinding
import com.example.duplicatetelegram.utilits.APP_ACTIVITY
import com.example.duplicatetelegram.utilits.hideKeyBoard


class ChatsFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        APP_ACTIVITY.title = "telegram"
        hideKeyBoard()
    }


}
