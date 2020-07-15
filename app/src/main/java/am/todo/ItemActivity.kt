package am.todo

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.bottom_app_menu.*

class ItemActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = mAuth.currentUser
    private val currentUserID: String? = currentUser?.uid

    private val root: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mUsers: DatabaseReference = root.reference.child("Users")
    private val mCurrentUser: DatabaseReference? = mUsers.child(currentUserID.toString())
    private val mNotes: DatabaseReference? = mCurrentUser?.child("notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val index = intent.getStringExtra("index")

        if (index != null) {
            setItemInfo(index)

        } else {
            moveToMain()
        }
    }

    private fun setItemInfo(position: String) {
        val noteListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val itemPath = dataSnapshot.child(position)

                val itemName = itemPath.child("name").value.toString()
                val itemDescription = itemPath.child("description").value.toString()

                item_name.text = itemName
                item_description.text = itemDescription
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        mNotes?.addListenerForSingleValueEvent(noteListener)

        item_edit.setOnClickListener {
            edit(position)
        }
        edit_link.setOnClickListener {
            edit(position)
        }

        item_delete.setOnClickListener {
            alertForTaskDeleting(position)
        }
        delete_task_link.setOnClickListener {
            alertForTaskDeleting(position)
        }
    }

    private fun edit(index: String) {
        if (item_action_edit.visibility == View.VISIBLE) {
            item_action_edit.visibility = View.INVISIBLE
        } else {
            item_action_edit.visibility = View.VISIBLE
        }

        edit_save.setOnClickListener {
            val name: String = edit_name.text.toString()
            val description: String = edit_description.text.toString()

            mNotes?.child(index)?.child("name")?.setValue(name)
            mNotes?.child(index)?.child("description")?.setValue(description)

            moveToMain()
        }
    }

    private fun delete(index: String) {
        mNotes?.child(index)?.child("name")?.setValue("")
        mNotes?.child(index)?.child("description")?.setValue("")
        moveToMain()
    }

    private fun alertForTaskDeleting(index: String) {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Task Deleting")
        builder.setMessage("Are you sure want to delete this Task?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, button ->
            when(button){
                DialogInterface.BUTTON_POSITIVE -> delete(index)
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(this, "Task deleting Canceled", Toast.LENGTH_SHORT).show()
                DialogInterface.BUTTON_NEUTRAL -> Toast.makeText(this, "Task deleting Canceled", Toast.LENGTH_SHORT).show()
            }
        }


        builder.setPositiveButton("YES", dialogClickListener)
        builder.setNegativeButton("NO", dialogClickListener)
        builder.setNeutralButton("CANCEL", dialogClickListener)

        dialog = builder.create()
        dialog.show()
    }

    private fun moveToMain() {
        val intent = Intent(this@ItemActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun menuInit() {
        menu_home.setOnClickListener {
            val intent = Intent(this@ItemActivity, MainActivity::class.java)
            startActivity(intent)
        }
        menu_add.setOnClickListener {
            val intent = Intent(this@ItemActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }
        menu_user.setOnClickListener {
            val intent = Intent(this@ItemActivity, AccountActivity::class.java)
            startActivity(intent)
        }
    }
}
