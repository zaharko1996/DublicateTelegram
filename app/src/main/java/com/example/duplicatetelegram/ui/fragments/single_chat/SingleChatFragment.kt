package com.example.duplicatetelegram.ui.fragments.single_chat

import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import com.example.duplicatetelegram.R
import com.example.duplicatetelegram.database.*
import com.example.duplicatetelegram.models.CommonModel
import com.example.duplicatetelegram.models.UserMode
import com.example.duplicatetelegram.ui.fragments.BaseFragment
import com.example.duplicatetelegram.utilits.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*

/**
 * A simple [Fragment] subclass.
 */
class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserMode
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private var mListListeners = mutableListOf<AppChildEventListener>()

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = single_chat_recyclerView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATA_BASE_ROOT
            .child(NODE_MESSAGE)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter

        mMessagesListener = AppChildEventListener {
            mAdapter.addItem(it.getCommonModel())
            if (mSmoothScrollToPosition) {
                mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            }
        }


        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        mListListeners.add(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 15
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        mListListeners.add(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATA_BASE_ROOT.child(
            NODE_USERS
        ).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        single_chat_btn_send_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = single_chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("ВВедите сообщение")
            } else sendMessage(
                message,
                contact.id,
                TYPE_TEXT
            ) {
                single_chat_input_message.setText("")
            }
        }
    }


    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.single_chat_fullname.text = contact.fullname
        } else mToolbarInfo.single_chat_fullname.text = mReceivingUser.fullname

        mToolbarInfo.single_chat_photo.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.single_chat_status.text = mReceivingUser.status
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)

        mListListeners.forEach {
            mRefMessages.removeEventListener(it)
        }

        println()

    }
}
