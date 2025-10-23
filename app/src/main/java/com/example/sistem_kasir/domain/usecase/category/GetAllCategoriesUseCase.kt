package com.example.sistem_kasir.domain.usecase.category

import com.example.sistem_kasir.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<com.example.sistem_kasir.domain.model.Category>> {
        return repository.getAllCategories()
    }
}