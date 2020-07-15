package am.todo

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_phone_login.*
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = mAuth.currentUser

    private val root: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mUsers: DatabaseReference = root.reference.child("Users")

    private var callbacks: OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private var mResendToken: ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        sendAndCheckCode()
    }

    private fun sendAndCheckCode() {
        callbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "Invalid Phone Number, Please enter the correct number with your country code ...",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "Code has been sent to  ${phone_login_number.text} number, please check and verify ...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        phone_login_send_code.setOnClickListener {
            val phoneNumber: String = phone_login_number.text.toString()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "Please enter the correct phone number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this@PhoneLoginActivity,
                    callbacks as OnVerificationStateChangedCallbacks
                )
            }
        }

        phone_login_check_code.setOnClickListener {
            val code: String = phone_login_code.text.toString()
            if (code.isEmpty()) {
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "Please enter the verification code",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val credential = PhoneAuthProvider.getCredential(mVerificationId.toString(), code)
                signInWithPhoneAuthCredential(credential)
            }

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.setLanguageCode("am")
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "You are successfully logged",
                        Toast.LENGTH_LONG
                    ).show()

                    val userListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            if (!dataSnapshot.hasChild("${mAuth.currentUser?.uid}")) {
                                mUsers.child("${mAuth.currentUser?.uid}").setValue("")
                                mUsers.child("${mAuth.currentUser?.uid}").child("index").setValue(0)
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    }
                    mUsers.addListenerForSingleValueEvent(userListener)

                    moveToMain()
                } else {
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "Error ${task.exception.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun moveToMain() {
        val intent = Intent(this@PhoneLoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}