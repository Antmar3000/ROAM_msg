package com.antmar3000.roam

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.antmar3000.roam.databinding.ActivityAccountBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class AccountActivity : AppCompatActivity() {

    private val binding: ActivityAccountBinding by lazy {
        ActivityAccountBinding.inflate(layoutInflater)
    }
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = Firebase.auth
        checkAuth()

        binding.imageGoogleSignIn.setImageResource(R.drawable.ic_icon_google)
        binding.imageFacebookSignIn.setImageResource(R.drawable.ic_icon_facebook)
        binding.imageSubmitButton.setImageResource(R.drawable.ic_icon_signin)
        binding.buttonShowRegistration.setImageResource(R.drawable.ic_icon_new_account)

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.d("LogRoam", "Api Exception")
            }
        }

        binding.buttonCancel.setOnClickListener { slideVerticalDown() }
        binding.imageSubmitButton.setOnClickListener { emailPasswordLogIn() }
        binding.imageGoogleSignIn.setOnClickListener { signInGoogle() }
        binding.buttonShowRegistration.setOnClickListener { slideVerticalUp() }
        binding.buttonSubmit.setOnClickListener { createAccount() }
    }

    override fun onBackPressed() {
        if (binding.cardViewReg.visibility == View.VISIBLE){
            slideVerticalDown()
        } else {super.onBackPressed()}
    }

    private fun createAccount() {
        binding.apply {
            if (editEmailRegister.text.isNullOrEmpty() || editPasswordRegister.text.isNullOrEmpty()) {
                editEmailRegister.error = getString(R.string.error_email)
            } else {
                auth.createUserWithEmailAndPassword(editEmailRegister.text.toString(), editPasswordRegister.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@AccountActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                            checkAuth()
                        }
                    }
            }
        }
    }

    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuthGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                checkAuth()
            } else {
                Toast.makeText(this, R.string.register_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun emailPasswordLogIn () {
        auth.signInWithEmailAndPassword(binding.editEmail.text.toString(), binding.editPassword.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    checkAuth()
                } else {
                    Toast.makeText(this@AccountActivity, R.string.sign_in_fail, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkAuth() {
        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun slideVerticalUp(){
        val transition = Slide(Gravity.BOTTOM)
        transition.duration = 500
        transition.addTarget(binding.cardViewReg)

        TransitionManager.beginDelayedTransition(binding.ConstraintLayout, transition)
        binding.cardViewReg.visibility = View.VISIBLE


    }

    private fun slideVerticalDown (){
        val transition = Slide(Gravity.BOTTOM)
        transition.duration = 800
        transition.addTarget(binding.cardViewReg)

        TransitionManager.beginDelayedTransition(binding.ConstraintLayout, transition)
        binding.cardViewReg.visibility = View.GONE
    }
}