package com.example.duplicatetelegram.utilits

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.duplicatetelegram.R
import com.example.duplicatetelegram.activities.MainActivity
import com.example.duplicatetelegram.database.updateNumbersToDatabase
import com.example.duplicatetelegram.models.CommonModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.dataContainer,
                fragment
            ).commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.dataContainer,
                fragment
            ).commit()
    }

}



fun hideKeyBoard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get().load(url)
        .fit()
        .placeholder(R.drawable.phro_1)
        .into(this)
}

fun initContacts() {
    if (checkPermissions(READ_CONTACTS)) {
        var arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullName = it.getString(
                    it.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                val number = it.getString(
                    it.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                )
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.number = number.replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updateNumbersToDatabase(
            arrayContacts
        )
    }
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}