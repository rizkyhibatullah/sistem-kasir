package com.example.sistem_kasir.domain.usecase.category

import com.example.sistem_kasir.domain.model.Category
import com.example.sistem_kasir.domain.repository.CategoryRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Long> {
        return try {
            val id = repository.insertCategory(category)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}