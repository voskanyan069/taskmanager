package am.todo

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val root: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mUsers: DatabaseReference = root.reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        createUser()
        already_have_an_account_link.setOnClickListener {
            moveToLogin()
        }
    }

    private fun createUser() {
        create_account_button.setOnClickListener {
            val email: String = registration_email.text.toString()
            val pass: String = registration_password.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email ...", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Please enter password ...", Toast.LENGTH_SHORT).show()
            } else {
                mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mUsers.child("${mAuth.currentUser?.uid}").setValue("")
                            mUsers.child("${mAuth.currentUser?.uid}").child("index").setValue(0)

                            moveToMain()
                            Toast.makeText(this@RegistrationActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            val errMessage = task.exception.toString()
                            Toast.makeText(this@RegistrationActivity, "Error $errMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun moveToMain() {
        val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun moveToLogin() {
        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}