package th.ac.kku.cis.mobileapp.myaccount

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main_r.*
import java.util.*


 class receipts {
    companion object Factory {
        fun create(): receipts = receipts()
    }
    var Id: String? = null
    var Amount: String? = null
    var category: String? = null
    var detail: String? = null
    var from: String? = null
    var date: String? = null
    var image_uri: String? = null
//     constructor()
}
class MainR : AppCompatActivity() {

    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    lateinit var mDB: DatabaseReference
    lateinit var auth: FirebaseAuth
    var newData: receipts = receipts.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_r)

        button.setOnClickListener {
            //if system os is Marshmallow or Above, we need to request runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enabled
                    val permission = arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    //permission already granted
                    openCamera()
                }
            } else {
                //system os is < marshmallow
                openCamera()
            }
        }
        auth = FirebaseAuth.getInstance()
        mDB = FirebaseDatabase.getInstance().reference

        button5.setOnClickListener {
            AddData()
        }
    }
    fun AddData() {
        val obj = mDB.child("receipts").push()
        newData.Amount = mount.text.toString()
        newData.category = calory.text.toString()
        newData.detail = details.text.toString()
        newData.from = fromm.text.toString()
        newData.date = datee.text.toString()
        newData.Id = obj.key
        obj.setValue(newData)
    }


    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            imageView4.setImageURI(image_uri)

        }

        uploadImageToFirebaseStorage()
    }

    private fun uploadImageToFirebaseStorage() {
        if (image_uri == null) return

        val filename = "images/" + UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance()
        val ref = storage.getReference(filename)

        ref.putFile(image_uri!!)
            .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot?> {
                // get the image Url of the file uploaded
                ref.getDownloadUrl()
                    .addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                        // getting image uri and converting into string
                        newData.image_uri = uri.toString()
                    })
            })
    }

}






