package am.todo

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.bottom_app_menu.*

class AccountActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val root: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mUsers: DatabaseReference = root.reference.child("Users")
    private val currentUserID: String? = mAuth.currentUser?.uid
    private val mCurrentUser: DatabaseReference = mUsers.child(currentUserID.toString())
    private val currentUser: FirebaseUser? = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        logout()
        accountDelete()
        menuInit()
    }

    private fun logout() {
        account_logout.setOnClickListener {
            mAuth.signOut()
            moveToLogin()
        }
        logout_link.setOnClickListener {
            mAuth.signOut()
            moveToLogin()
        }
    }

    private fun accountDelete() {
        account_delete.setOnClickListener {
            alertForAccountDeleting()
        }
        delete_account_link.setOnClickListener {
            alertForAccountDeleting()
        }
    }

    private fun delete() {
        mCurrentUser.removeValue()
        currentUser?.delete()
        moveToLogin()
    }

    private fun alertForAccountDeleting() {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Account Deleting")
        builder.setMessage("Are you sure want to delete your Account?")

        val dialogClickListener = DialogInterface.OnClickListener{_, button ->
            when(button){
                DialogInterface.BUTTON_POSITIVE -> delete()
                DialogInterface.BUTTON_NEGATIVE -> toast("Account deleting Canceled")
                DialogInterface.BUTTON_NEUTRAL -> toast("Account deleting Canceled")
            }
        }

        builder.setPositiveButton("YES", dialogClickListener)
        builder.setNegativeButton("NO", dialogClickListener)
        builder.setNeutralButton("CANCEL", dialogClickListener)

        dialog = builder.create()
        dialog.show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun menuInit() {
        menu_home.setOnClickListener {
            val intent = Intent(this@AccountActivity, MainActivity::class.java)
            startActivity(intent)
        }
        menu_add.setOnClickListener {
            val intent = Intent(this@AccountActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this@AccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}