package com.study_kotlin.backend.controller

import com.study_kotlin.backend.repository.TestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @Autowired
    lateinit var testRepository: TestRepository

    @GetMapping("/")
    fun index(): String {
        val testTable = testRepository.findById(1)

        return testTable.get().value!!
    }
}
