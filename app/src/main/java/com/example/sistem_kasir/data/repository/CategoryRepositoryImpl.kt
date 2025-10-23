package com.example.sistem_kasir.data.repository

import com.example.sistem_kasir.data.local.dao.CategoryDao
import com.example.sistem_kasir.data.mapper.toDomain
import com.example.sistem_kasir.data.mapper.toEntity
import com.example.sistem_kasir.domain.model.Category
import com.example.sistem_kasir.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories() = dao.getAllCategories().map { it.map { category -> category.toDomain() } }

    override suspend fun insertCategory(category: Category): Long {
        return dao.insertCategory(category.toEntity())
    }
}