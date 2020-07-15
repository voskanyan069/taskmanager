package am.todo

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_app_menu.*

class MainActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser: FirebaseUser? = mAuth.currentUser
    private val currentUserID: String? = currentUser?.uid

    private val root: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mUsers: DatabaseReference = root.reference.child("Users")
    private val mCurrentUser: DatabaseReference? = mUsers.child(currentUserID.toString())

    private var size: Int? = null

    private val todo_names = mutableListOf<String>()
    private val todo_description = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (currentUser == null) {
            moveToLogin()
            return
        } else {
            loadNotes()
            itemClick()
            menuInit()
        }
    }

    private fun loadNotes() {
        val notesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                size = (dataSnapshot.child("index").value as Long?)?.toInt()
                for (i in 0 until size!!) {
                    if (dataSnapshot.child("notes").child("$i").child("name").value == null && dataSnapshot.child("notes").child("$i").child("description").value == null) {
                        continue
                    }

                    val name = (dataSnapshot.child("notes").child("$i").child("name").value.toString())
                    val description = (dataSnapshot.child("notes").child("$i").child("description").value.toString())

                    todo_names.add(name)
                    todo_description.add(description)

                    setAdapter()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        mCurrentUser?.addListenerForSingleValueEvent(notesListener)
    }

    private fun setAdapter() {
        val customListAdapter = CustomListAdapter(this, todo_names, todo_description)
        todo_list.adapter = customListAdapter
    }

    private fun itemClick() {
        todo_list.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val itemValue = todo_list.getItemAtPosition(position) as String

                println("----------------------------------------------------------")
                println("---------------------------------------------------------- PARENT - $parent")
                println("---------------------------------------------------------- VIEW - $view")
                println("---------------------------------------------------------- POSITION - $position")
                println("---------------------------------------------------------- ID - $id")
                println("----------------------------------------------------------")

                moveToItem(position.toString())
            }
    }

    private fun menuInit() {
        menu_add.setOnClickListener {
            val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }
        menu_user.setOnClickListener {
            val intent = Intent(this@MainActivity, AccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveToItem(index: String) {
        val intent = Intent(this@MainActivity, ItemActivity::class.java)
        intent.putExtra("index", index)
        startActivity(intent)
    }

    private fun moveToLogin() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}
