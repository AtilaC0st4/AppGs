package com.example.appgs

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appgs.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private  val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnEntrar.setOnClickListener {
            logarUsuario()
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }

    }

    private fun logarUsuario() {
        val email = binding.editTextLogin.text.toString()
        val senha = binding.editTextSenha.text.toString()

        autenticacao.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                startActivity(Intent(this,PrincipalActivity::class.java))
            }.addOnFailureListener { exception ->
                val mensagemErro = exception.message
                AlertDialog.Builder(this)
                    .setTitle("ERRO")
                    .setMessage("Error ao Logar ${mensagemErro}")
                    .setPositiveButton("Fechar"){dialog,posicao->}
                    .create().show()

            }

    }




}