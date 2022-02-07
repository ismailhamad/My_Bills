package com.example.my_bills

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import android.graphics.Bitmap


import android.provider.MediaStore

import com.example.my_bills.Model.Bills

import java.io.ByteArrayOutputStream
import android.net.ConnectivityManager

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.my_bills.db.BillsDatabase

import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import java.lang.IllegalArgumentException


class MainActivity : AppCompatActivity() {
    lateinit var mNetworkReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Edit = findViewById(R.id.Edit)
        name = findViewById(R.id.inputText)
        save = findViewById(R.id.save)
        textChooseImage=findViewById(R.id.textView5)
        showImage = findViewById(R.id.imageView)
        storge = Firebase.storage
        referance = storge!!.reference
        inflater = layoutInflater
        mNetworkReceiver = NetworkChangeReceiver()
        db = BillsDatabase.invoke(this)
        registerNetworkBroadcastForNougat()
        Edit.visibility=View.GONE
        Edit.setOnClickListener {
            OptionsSheet().show(this) {
                title("Choose")
                with(
                    Option(R.drawable.ic_baseline_camera_alt_24, "camera"),
                    Option(R.drawable.ic_baseline_insert_photo_24, "gallery")
                )
                onPositive { index: Int, option: Option ->
                    if (index == 0) {
                        dispatchTakePictureIntent()
                    } else if (index == 1) {
                        dispatchTakeGalleryIntent()
                    }

                }


            }
        }

        showImage.setOnClickListener {
            OptionsSheet().show(this) {
                title("Choose")
                with(
                    Option(R.drawable.ic_baseline_camera_alt_24, "camera"),
                    Option(R.drawable.ic_baseline_insert_photo_24, "gallery")
                )
                onPositive { index: Int, option: Option ->
                    if (index == 0) {
                        dispatchTakePictureIntent()
                    } else if (index == 1) {
                        dispatchTakeGalleryIntent()
                    }

                }


            }

        }


    }

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_Gallery_CAPTURE = 2
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun dispatchTakeGalleryIntent() {
        val takePictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(takePictureIntent, REQUEST_Gallery_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    companion object {
        var imageUri: Uri? = null
        var storge: FirebaseStorage? = null
        var referance: StorageReference? = null
        lateinit var db: BillsDatabase
        lateinit var Edit: Button
        lateinit var name: TextView
        lateinit var save: Button
        lateinit var showImage: ImageView
        lateinit var textChooseImage:TextView
        lateinit var inflater: LayoutInflater
        lateinit var imageBitmap: Bitmap
        var dataByteArray: ByteArray? = null
        fun UpLOAD(value: Boolean, context: Context) {
            val inflater_view = inflater.inflate(R.layout.custom_view, null)
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setView(inflater_view)
            val dialog = alertDialog.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            assert(window != null)
            window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            if (value) {
                save.setOnClickListener {
                    if (name.text.isEmpty() || showImage.drawable == null) {
                        Toast.makeText(context, "الرجاء تعبية الحقول", Toast.LENGTH_LONG).show()
                    } else {
                        dialog.show()
                        uploadImage(dataByteArray!!, name.text.toString(), context)
                        name.text = ""
                        showImage.setImageBitmap(null)
                        textChooseImage.visibility=View.VISIBLE
                        Edit.visibility=View.INVISIBLE
                    }

                }
                Thread(Runnable {
                    var c = db.getdiarydao().getallnote()
                    for (x in c) {
                        uploadImage(x.imageUri, x.name, context)
                        db.getdiarydao().delete(x)
                    }
                }).start()


            } else {
                //oofline
                save.setOnClickListener {
                    if (name.text.isEmpty() || showImage.drawable == null) {
                        Toast.makeText(context, "الرجاء تعبية الحقول", Toast.LENGTH_LONG).show()
                    } else {
                        dialog.show()
                        db.getdiarydao().insertBills(Bills(null, name.text.toString(), dataByteArray!!))
                        name.text = ""
                        showImage.setImageBitmap(null)
                        textChooseImage.visibility=View.VISIBLE
                        Edit.visibility=View.INVISIBLE

                   }
                }
            }
        }

        fun uploadImage(byteArray: ByteArray, name: String, context: Context) {
            referance!!.child("Bills/" + name).putBytes(byteArray)
                .addOnSuccessListener { taskSnapshot ->

                }.addOnFailureListener { exception ->
                    Toast.makeText(context, "filedUploadimage1", Toast.LENGTH_LONG).show()

                }


        }


    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_Gallery_CAPTURE && resultCode == RESULT_OK) {
            showImage.setImageURI(data!!.data)
            textChooseImage.visibility=View.INVISIBLE
            Edit.visibility=View.VISIBLE
            imageUri = data.data
            var IBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val baos = ByteArrayOutputStream()
            IBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            dataByteArray = baos.toByteArray()

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            showImage.setImageBitmap(imageBitmap)
            textChooseImage.visibility=View.INVISIBLE
            Edit.visibility=View.VISIBLE
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            dataByteArray = baos.toByteArray()
        }

    }


}