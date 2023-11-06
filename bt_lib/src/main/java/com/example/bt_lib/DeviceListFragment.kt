package com.example.bt_lib

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bt_lib.databinding.FragmentListBinding
import com.google.android.material.snackbar.Snackbar

class DeviceListFragment : Fragment(), DeviceAdapter.Listener {

    private var btAdapter: BluetoothAdapter? = null
    private lateinit var binding: FragmentListBinding
    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var deviceAdapter: DeviceAdapter
    private var preferences: SharedPreferences? = null

    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var pLauncher2: ActivityResultLauncher<Array<String>>
    private var isBtConnectPermissionGranted = false
    private var isBtScanPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences =
            activity?.getSharedPreferences(BluetoothConstants.PREF_TABLE, Context.MODE_PRIVATE)

        initRcViews()
        registerBtLauncher()
        initBtAdapter()

        bluetoothState()

        Log.d("MyLog", "${btAdapter?.isEnabled}")
        binding.imBtOn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                requestMultiplePermissions.launch(
//                    arrayOf(
//                        Manifest.permission.BLUETOOTH_CONNECT,
//                        Manifest.permission.BLUETOOTH_SCAN
//                    )
//                )
//            } else {
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                btLauncher.launch(enableBtIntent)
//            }

            //whether bluetooth is turned on
            if (isOpenBluetooth()){
                showMsg("Bluetooth is on")
                return@setOnClickListener
            }
            // is Android12
            if (isAndroid12()) {
                //Check for BLUETOOTH_CONNECT permission
                if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    // turn on bluetooth
                    enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                } else {
                    //Request permission
                    requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
                return@setOnClickListener

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                registerPermissions()
//            } else {
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                btLauncher.launch(enableBtIntent)
//            }
        }
    }
    }

    override fun onResume() {
        super.onResume()
        bluetoothState()
    }


    //Open bluetooth intent
    private val enableBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            showMsg(if (isOpenBluetooth()) "Bluetooth is on" else "Bluetooth is not on")
        }
    }

    //Request BLUETOOTH_CONNECT permission intent
    private val requestBluetoothConnect = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            // turn on bluetooth
            enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            showMsg("If this permission is not obtained in Android12, Bluetooth cannot be turned on.")
        }
    }

    private fun initRcViews() = with(binding) {
        rcViewPaired.layoutManager = LinearLayoutManager(requireContext())
        // указание на фрагмент, а не на binding
        deviceAdapter = DeviceAdapter(this@DeviceListFragment)
        rcViewPaired.adapter = deviceAdapter
    }

    private fun getPairedDevices() {

//        if (btAdapter == null) {
//            Toast.makeText(activity as AppCompatActivity, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show()
//        } else {
//            // Arraylist of all the bonded (paired) devices
//            val pairedDevices = btAdapter?.bondedDevices
//            if (pairedDevices?.size!! > 0) {
//                if (pairedDevices != null) {
//                    for (device in pairedDevices) {
//
//                        // get the device name
//                        val deviceName = device.name
//
//                        // get the mac address
//                        val macAddress = device.address
//
//                        // append in the two separate views
//                        Log.d("MyLog","$deviceName\n $macAddress\n")
//                    }
//                }
//            }
//        }
//        TODO("Android 12")
        // due to connection permission for Android 11/12
//        try {
        val list = ArrayList<DeviceItem>()
        val devicesList = btAdapter?.bondedDevices as Set<BluetoothDevice>
        devicesList.forEach {
            list.add(
                DeviceItem(
                    it.name,
                    it.address,
                    it.address == preferences?.getString(BluetoothConstants.KEY_MAC, "")
                )
            )
        }
        binding.tvEmptyPaired.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        deviceAdapter.submitList(list)
        Log.d("MyLog", "Paired dev: $list")
//        } catch (e: SecurityException) {
//            Log.d("MyLog", "Android 12")
//        }

    }

    private fun initBtAdapter() {
        val btManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
    }

    private fun bluetoothState() {
        if (btAdapter?.isEnabled == true) {
            changeImageButtonColor(binding.imBtOn, Color.GREEN)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    )
                )
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                val enableBtIntent2 = Intent(BluetoothAdapter.STATE_ON.toString())
                btLauncher.launch(enableBtIntent)
                btLauncher.launch(enableBtIntent2)
            }

            getPairedDevices()
        } else {
            changeImageButtonColor(binding.imBtOn, Color.RED)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun registerPermissions() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isBtScanPermissionGranted =
                        permissions[android.Manifest.permission.BLUETOOTH_SCAN]
                            ?: isBtScanPermissionGranted
                    isBtConnectPermissionGranted =
                        permissions[android.Manifest.permission.BLUETOOTH_CONNECT]
                            ?: isBtConnectPermissionGranted
                }

            }


        val permissionRequestList = ArrayList<String>()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addPermissionToRequestedList(
                isBtScanPermissionGranted,
                android.Manifest.permission.BLUETOOTH_SCAN, permissionRequestList
            )
            addPermissionToRequestedList(
                isBtConnectPermissionGranted,
                android.Manifest.permission.BLUETOOTH_CONNECT, permissionRequestList
            )
        }

        if (permissionRequestList.isNotEmpty()) {
            if (permissionRequestList.size == 1) {
                pLauncher.launch(arrayOf(permissionRequestList[0]))
            } else if (permissionRequestList.size > 1) {
                pLauncher.launch(arrayOf(permissionRequestList[0]))
                pLauncher2.launch(arrayOf(permissionRequestList[1]))
            }
        } else {
//            initOsm()
        }
        Log.d("MyLog", "perm-s: $permissionRequestList")
    }


    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }

    // Bluetooth turn on / off in settings system intent
    private fun registerBtLauncher() {
        btLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                changeImageButtonColor(binding.imBtOn, Color.GREEN)
                getPairedDevices()
                Snackbar.make(binding.root, "Bluetooth is turned on", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, "Bluetooth is turned off", Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun saveMac(mac: String) {
        val editor = preferences?.edit()
        editor?.putString(BluetoothConstants.KEY_MAC, mac)
        editor?.apply()
    }

    override fun onClick(device: DeviceItem) {
        saveMac(device.mac)
    }


}