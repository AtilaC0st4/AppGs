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
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private  val autenticacao by lazy {
        FirebaseAuth.getInstance()
    }

     private val bd by lazy {
         FirebaseFirestore.getInstance()
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
        val nome = binding.editTextNome.text.toString() // Certifique-se de que há um campo para o nome no layout

        autenticacao.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid

                if (userId != null) {
                    // Cria um documento com o ID do usuário
                    val usuario = hashMapOf(
                        "nome" to nome,
                        "email" to email
                    )

                    bd.collection("usuarios")
                        .document(userId)
                        .set(usuario)
                        .addOnSuccessListener {
                            AlertDialog.Builder(this)
                                .setTitle("SUCESSO")
                                .setMessage("Sucesso ao criar conta e salvar os dados.")
                                .setPositiveButton("OK") { _, _ ->
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                                .setCancelable(false).create().show()
                        }
                        .addOnFailureListener { exception ->
                            AlertDialog.Builder(this)
                                .setTitle("ERROR")
                                .setMessage("Erro ao salvar os dados: ${exception.message}")
                                .setPositiveButton("Fechar") { dialog, _ -> dialog.dismiss() }
                                .create().show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                val mensagemErro = exception.message
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Erro ao criar conta: $mensagemErro")
                    .setPositiveButton("Fechar") { dialog, _ -> dialog.dismiss() }
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