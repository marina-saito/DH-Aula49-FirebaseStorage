package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference
    private val CODE_IMG = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        config()

        fbUpload.setOnClickListener {
            setIntent()
        }
    }

    fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()
        storageReference = FirebaseStorage.getInstance().getReference("prod_img")
    }

    // Configura a intent para obter a imagem da galeria
    fun setIntent() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura imagem"), CODE_IMG)
    }

    // Função sobrescrita da Activity que recuper eventos
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verfica se está capturando de fato o evento chamado na função startActivityForResult
        if(requestCode == CODE_IMG) {
            alertDialog.show()
            val uploadTask = storageReference.putFile(data!!.data!!)
            val task = uploadTask.continueWithTask { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Chegando", Toast.LENGTH_SHORT).show()
                }
                storageReference!!.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = downloadUri!!.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))
                    Log.i("Url referencia ", url)
                    alertDialog.dismiss()
                    Picasso.get().load(url).into(ivRec)
                }
            }
        }
    }
}