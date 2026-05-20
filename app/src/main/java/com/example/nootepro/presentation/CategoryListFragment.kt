package com.example.nootepro.presentation

import android.os.Bundle
import android.view.*import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nootepro.R
import com.example.nootepro.databinding.FragmentCategoryListBinding
import com.example.nootepro.domain.models.CategoryModel
import com.example.nootepro.presentation.adapters.CategoryAdapter
import com.example.nootepro.presentation.viewmodels.NoteViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModel()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Toolbar Setup (Professional My Categories Bar)
        val toolbar: Toolbar = binding.toolbar
        setupMenu(toolbar)

        // 2. RecyclerView Setup
        setupRecyclerView()

        // 3. Data Observe Karein
        observeCategories()

        // 4. Add Category Button
        binding.btnAddCategory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategorySelectionFragment())
                .addToBackStack(null)
                .commit()
        }

        // initial Fetch
        viewModel.fetchCategories()
    }

    private fun setupMenu(toolbar: Toolbar) {
        toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_logout -> {
                        showLogoutDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

// C:/Users/dani7/AndroidStudioProjects/Nootepro/app/src/main/java/com/example/nootepro/presentation/CategoryListFragment.kt

    private fun setupRecyclerView() {
        // Adapter ko initialize karein aur teeno callbacks (click, delete, long click) pass karein
        adapter = CategoryAdapter(
            onCategoryClick = { category ->
                // Normal click par notes khulenge
                val fragment = CategoryNotesFragment.newInstance(category.name, category.color)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { category ->
                // Delete icon par click karne se delete confirmation aayegi
                showDeleteCategoryDialog(category)
            },
            onLongClick = { category ->
                // Box par daba kar rakhne (Long Press) se rename dialog khulega
                showRenameDialog(category)
            }
        )

        // RecyclerView ko grid layout aur adapter assign karein
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@CategoryListFragment.adapter
        }
    }

    // --- RENAME DIALOG ---
    private fun showRenameDialog(category: CategoryModel) {
        val input = EditText(requireContext())
        input.setText(category.name)
        input.setPadding(50, 40, 50, 40)

        AlertDialog.Builder(requireContext())
            .setTitle("Rename Category")
            .setMessage("Enter new name for '${category.name}'")
            .setView(input)
            .setPositiveButton("Rename") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty() && newName != category.name) {
                    viewModel.renameCategory(category.name, newName)
                } else if (newName.isEmpty()) {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteCategoryDialog(category: CategoryModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete '${category.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteCategory(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                FirebaseAuth.getInstance().signOut()

                val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
                intent?.let { safeIntent ->
                    safeIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(safeIntent)
                    requireActivity().finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriesListState.collect { result ->
                result?.onSuccess { categories ->
                    adapter.submitList(categories)
                }
                result?.onFailure { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}