package com.example.appgs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appgs.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class homeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db by lazy {
        FirebaseFirestore.getInstance()
    }

    private val dicas by lazy {
        mutableListOf<String>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnNovaDica.setOnClickListener {
            carregarDicas() // Carrega as dicas antes de sortear
        }

        binding.bntSalvarDica.setOnClickListener {
            val dicaAtual = binding.textViewDica.text.toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid != null && dicaAtual.isNotEmpty()) {
                salvarDicaParaUsuario(uid, dicaAtual)
            } else {
                Toast.makeText(context, "Erro: Usuário não autenticado ou dica vazia!", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    fun carregarDicas() {
        db.collection("dicas")
            .get()
            .addOnSuccessListener { result ->
                dicas.clear()
                for (document in result) {
                    document.getString("texto")?.let { dicas.add(it) }
                }

                // Depois de carregar as dicas, sorteia uma e exibe
                val dica = sortearDica()
                if (dica != null) {
                    binding.textViewDica.text = dica
                } else {
                    binding.textViewDica.text = "Nenhuma dica disponível no momento."
                }
            }
            .addOnFailureListener { exception ->
                // Tratar erros, como exibir uma mensagem ao usuário
                Toast.makeText(context, "Erro ao carregar dicas", Toast.LENGTH_SHORT).show()
            }
    }

    fun sortearDica(): String? {
        if (dicas.isNotEmpty()) {
            val indiceAleatorio = (dicas.indices).random()
            return dicas[indiceAleatorio]
        }
        return null // Caso a lista esteja vazia\
    }

    fun salvarDicaParaUsuario(uid: String, dica: String) {
        val userRef = db.collection("usuarios").document(uid)

        // Atualiza ou cria o campo dicasSalvas com a dica
        userRef.update("dicasSalvas", FieldValue.arrayUnion(dica))
            .addOnSuccessListener {
                Toast.makeText(context, "Dica salva com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Caso o documento não exista, cria um novo
                userRef.set(mapOf("dicasSalvas" to listOf(dica)))
                    .addOnSuccessListener {
                        Toast.makeText(context, "Dica salva com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(context, "Erro ao salvar dica: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }

}
