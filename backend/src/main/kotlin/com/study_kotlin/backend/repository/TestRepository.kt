package com.study_kotlin.backend.repository

import com.study_kotlin.backend.entity.TestTable
import org.springframework.data.repository.CrudRepository

interface TestRepository : CrudRepository<TestTable, Int>
