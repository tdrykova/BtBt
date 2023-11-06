package com.example.bt_lib

import android.bluetooth.BluetoothManager
import android.content.Context.BLUETOOTH_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment

fun Fragment.changeImageButtonColor(button: ImageButton, color: Int) {
    val drawable = button.drawable
    DrawableCompat.setTint(drawable, color)
    button.setImageDrawable(drawable)
}

fun Fragment.addPermissionToRequestedList(
    stateOfPermission: Boolean, permission: String,
    list: ArrayList<String>
) {
    if (!stateOfPermission)
        list.add(permission)
}


fun Fragment.isOpenBluetooth(): Boolean {
    val manager = context?.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    val adapter = manager.adapter ?: return false
    return adapter.isEnabled
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun Fragment.isAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S


fun Fragment.hasPermission(permission: String) =
    checkSelfPermission(activity as AppCompatActivity, permission) == PackageManager.PERMISSION_GRANTED

fun Fragment.showMsg(msg: String) {
    Toast.makeText(activity as AppCompatActivity, msg, Toast.LENGTH_SHORT).show()
}

