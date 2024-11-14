package com.example.appgs

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appgs.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth

 class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private  val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        verificaUsuarioLogado()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener{
            cadastroUsuario()
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun cadastroUsuario() {
        val email = binding.editTextLogin.text.toString()
        val senha = binding.editTextSenha.text.toString()

        autenticacao.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                AlertDialog.Builder(this)
                    .setTitle("SUCESSO")
                    .setMessage("Sucesso ao criar conta.")
                    .setPositiveButton("OK"){dialog,posicao->
                        startActivity(Intent(this,MainActivity::class.java))
                    }
                    .setCancelable(false).create().show()

            }.addOnFailureListener { exception ->
                val mensagemErro = exception.message
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Error ao criar conta ${mensagemErro}")
                    .setPositiveButton("Fechar"){dialog,posicao->}
                    .create().show()

            }

    }

    private fun verificaUsuarioLogado() {
        val usuario = autenticacao.currentUser

        if (usuario != null ){
            startActivity(Intent(this,PrincipalActivity::class.java))
        }
    }
}