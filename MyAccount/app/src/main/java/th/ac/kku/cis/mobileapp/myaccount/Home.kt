package th.ac.kku.cis.mobileapp.myaccount

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*

import kotlinx.android.synthetic.main.activity_main.*

class Home : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var newpropro: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        val NameSetting: TextView = findViewById(R.id.name)
        val Profile: ImageView = findViewById(R.id.imageView2)
        val Email: TextView = findViewById(R.id.mail)
        val xx: Uri? = auth.currentUser!!.photoUrl
        NameSetting.text = auth.currentUser!!.displayName.toString()
        Picasso.get().load(xx).into(Profile)
        Email.text = auth.currentUser!!.email

        auth.currentUser!!.email


        val btnlogout: Button = findViewById(R.id.button12)
        btnlogout.setOnClickListener({ v -> singOut() })
        button8.setOnClickListener {

            var i = Intent(this, Mainj::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
        button11.setOnClickListener {

            var i = Intent(this, MainR::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
}

    private fun passproject() {
        if (newpropro) {
            var i = Intent(this, MainActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }

    }
    private fun singOut() {

        auth.signOut()
        newpropro = true
        passproject()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            //show.text = "No User"
        } else {
            //show.text = user.email.toString()
            passproject()
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
                newpropro = false
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
                    newpropro = true
                    updateUI(user)
                } else {
                    newpropro = false
                    updateUI(null)
                }
            }

    }

}





