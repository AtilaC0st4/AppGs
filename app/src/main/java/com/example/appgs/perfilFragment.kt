package com.example.appgs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appgs.MainActivity
import com.example.appgs.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val autenticacao by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root

        // Mostra o nome atual do usuário
        carregarNomeAtual()

        // Atualiza o nome no Firestore
        binding.btnAtualizarNome.setOnClickListener {
            atualizarNome()
        }

        // Botão de sair
        binding.btnSair.setOnClickListener {
            autenticacao.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "Desconectado com sucesso", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun carregarNomeAtual() {
        val userId = autenticacao.currentUser?.uid
        if (userId != null) {
            firestore.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val nomeAtual = document.getString("nome")
                    binding.textViewNome.text = "Olá: ${nomeAtual ?: "Não disponível"}"
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Erro ao carregar nome", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun atualizarNome() {
        val novoNome = binding.editTextNovoNome.text.toString().trim()

        if (novoNome.isEmpty()) {
            Toast.makeText(requireContext(), "Digite um nome válido!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = autenticacao.currentUser?.uid
        if (userId != null) {
            firestore.collection("usuarios")
                .document(userId)
                .update("nome", novoNome)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    carregarNomeAtual() // Atualiza o nome exibido
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Erro ao atualizar nome: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
