package com.example.appgs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appgs.databinding.FragmentFavoritoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class favoritoFragment : Fragment() {

    private var _binding: FragmentFavoritoBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val dicas by lazy { mutableListOf<String>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritoBinding.inflate(inflater, container, false)
        val view = binding.root

        // Configurar RecyclerView
        val adapter = DicaAdapter(dicas) { dica -> excluirDica(dica) }
        binding.recyclerViewDicas.adapter = adapter
        binding.recyclerViewDicas.layoutManager = LinearLayoutManager(requireContext())

        carregarDicas(adapter)

        return view
    }

    private fun carregarDicas(adapter: DicaAdapter) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    val listaDicas = document.get("dicasSalvas") as? List<String> ?: emptyList()
                    dicas.clear()
                    dicas.addAll(listaDicas)
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar dicas", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun excluirDica(dica: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = db.collection("usuarios").document(uid)

            userRef.update("dicasSalvas", FieldValue.arrayRemove(dica))
                .addOnSuccessListener {
                    dicas.remove(dica)
                    binding.recyclerViewDicas.adapter?.notifyDataSetChanged()
                    Toast.makeText(context, "Dica excluída com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao excluir dica", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
