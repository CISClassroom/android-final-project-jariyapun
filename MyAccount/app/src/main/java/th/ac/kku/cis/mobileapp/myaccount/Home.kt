package th.ac.kku.cis.mobileapp.myaccount

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*

import kotlinx.android.synthetic.main.activity_main.*

class Home : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var checklog: Boolean = false

    lateinit var listView: ListView
    lateinit var ref: DatabaseReference
    lateinit var items:MutableList<receipts>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        listView = findViewById(R.id.listViewItems2)//นำข้อมูลจาก fibase มาแสดงในlist
        items = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("receipts")//เรียกใช้ข้อมูลใน fibase
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

           }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){
                    items.clear()
                    for (e in p0.children){
                        val rec = e.getValue(receipts::class.java)
                        items.add(rec!!)
                    }
                    val adapter = Adapter(this@Home,R.layout.detail ,items)
                    listView.adapter = adapter//เอาข้อมูลจาก fibase มาแสดงหน้า Home
                }
           }
        })

        auth = FirebaseAuth.getInstance()
        val NameSetting: TextView = findViewById(R.id.name)//ชื่อ
        val Profile: ImageView = findViewById(R.id.imageView2)//รูป
        val Email: TextView = findViewById(R.id.mail)//อีเมล
        val xx: Uri? = auth.currentUser!!.photoUrl
        NameSetting.text = auth.currentUser!!.displayName.toString()
        Picasso.get().load(xx).into(Profile)
        Email.text = auth.currentUser!!.email

        auth.currentUser!!.email


        val btnlogout: Button = findViewById(R.id.button12)//ปุ่มออกจากระบบ
        btnlogout.setOnClickListener({ v -> singOut() })
        button11.setOnClickListener {

            var i = Intent(this, MainR::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

    }

    private fun myaccount() {
        if (checklog) {
            var i = Intent(this, MainActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

    }
    private fun singOut() {

        auth.signOut()
        checklog = true
        myaccount()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
        } else {
            myaccount()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuth(account!!)
                //FirebaseAuth(account)
            } catch (e: ApiException) {
                Log.i("Error OOP", e.toString())
                checklog = false
                updateUI(null)
            }
        }
    }

    private fun firebaseAuth(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    checklog = true
                    updateUI(user)
                } else {
                    checklog = false
                    updateUI(null)
                }
            }

    }

}





