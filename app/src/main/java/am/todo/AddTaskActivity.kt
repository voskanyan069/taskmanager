package am.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.bottom_app_menu.*

class AddTaskActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = mAuth.currentUser
    private val currentUserID: String? = currentUser?.uid

    private val root: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mUsers: DatabaseReference = root.reference.child("Users")
    private val mCurrentUser: DatabaseReference? = mUsers.child(currentUserID.toString())
    private val mItems: DatabaseReference? = mCurrentUser?.child("notes")

    private var index: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        addTodo()
        menuInit()
    }

    private fun addTodo() {
        val indexListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                index = (dataSnapshot.value as Long?)?.toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        mCurrentUser?.child("index")?.addListenerForSingleValueEvent(indexListener)

        add_todo_button.setOnClickListener {
            val mItem: DatabaseReference? = mItems?.child("$index")

            val name: String = add_todo_name.text.toString()
            val description: String = add_todo_description.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter the TODO name", Toast.LENGTH_LONG).show()
            }
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter the TODO description", Toast.LENGTH_LONG).show()
            } else {
                mItem?.child("name")?.setValue(name)
                mItem?.child("description")?.setValue(description)
                mCurrentUser?.child("index")?.setValue(index?.plus(1))

                moveToMain()
            }
        }
    }

    private fun moveToMain() {
        val intent = Intent(this@AddTaskActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun menuInit() {
        menu_home.setOnClickListener {
            val intent = Intent(this@AddTaskActivity, MainActivity::class.java)
            startActivity(intent)
        }
        menu_user.setOnClickListener {
            val intent = Intent(this@AddTaskActivity, AccountActivity::class.java)
            startActivity(intent)
        }
    }
}