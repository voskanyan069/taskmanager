package am.todo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login()
        moveToRegistration()
//        moveToPhoneLogin()
    }
    
    private fun login() {
        login_button.setOnClickListener { 
            val email: String = login_email.text.toString()
            val password: String = login_password.text.toString()
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter the correct E-Mail and try again", Toast.LENGTH_LONG).show()
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter the correct Password and try again", Toast.LENGTH_LONG).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(this@LoginActivity, "Successful Logged in ", Toast.LENGTH_SHORT).show()
                    moveToMain()
                }
                    .addOnCanceledListener {
                        Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    
    private fun moveToMain() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun moveToRegistration() {
        need_new_account_link.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    /*

    private fun moveToPhoneLogin() {
        val intent = Intent(this@LoginActivity, PhoneLoginActivity::class.java)

        login_using_your.setOnClickListener {
            startActivity(intent)
        }

        login_phone_button.setOnClickListener {
            startActivity(intent)
        }
    }

    */
}