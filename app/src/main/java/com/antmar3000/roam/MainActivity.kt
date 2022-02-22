package com.antmar3000.roam

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.antmar3000.roam.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter: RecyclerMessageAdapter by lazy {RecyclerMessageAdapter()}
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = Firebase.auth
        setActionBar()

        val database = Firebase.database
        val reference = database.getReference(Constances.REFERENCE)
        binding.buttonSend.setOnClickListener {
            reference.child(reference.push().key ?: Constances.NULL_KEY)
                .setValue(
                    AuthenticationData(auth.currentUser?.displayName,
                    binding.editMsg.text.toString())
                )
            binding.recyclerMessage.smoothScrollToPosition(binding.recyclerMessage.adapter!!.itemCount)
        }
        msgListener(reference)
        initRecyclerMessage()

    }

    private fun initRecyclerMessage() = with(binding) {
        recyclerMessage.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerMessage.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.right_upper_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {auth.signOut()
                finish() }
            R.id.preferences -> {}
        }
        return super.onOptionsItemSelected(item)
    }


    private fun msgListener(databaseReference: DatabaseReference) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<AuthenticationData>()
                for (el in snapshot.children) {
                    val user = el.getValue(AuthenticationData::class.java)
                    if (user != null) list.add(user)
                }
                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setActionBar() {
        val actionBar = supportActionBar
        val defaultUri = Uri.parse(R.string.default_uri.toString())
        Thread {
            val bitmap = Glide.with(this).asBitmap().fallback(R.drawable.ic_baseline_no_photography_24)
                .load(auth.currentUser?.photoUrl ?: defaultUri).circleCrop().placeholder(R.drawable.ic_baseline_no_photography_24)
                .error(R.drawable.ic_baseline_no_photography_24).submit().get()
            val userIcon = BitmapDrawable(resources, bitmap)
            runOnUiThread {
                actionBar?.setDisplayHomeAsUpEnabled(true)
                actionBar?.setHomeAsUpIndicator(userIcon)
            }
        }.start()
        actionBar?.title = auth.currentUser?.displayName ?: getString(R.string.default_user_name)

    }

}