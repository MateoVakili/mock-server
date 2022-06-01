package com.mateo.server.mock.controller

import com.mateo.server.mock.entity.DummyEntity
import com.mateo.server.mock.service.DummyServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/test")
class DummyController {

    @Autowired
    private lateinit var dummyService: DummyServiceImpl

    @PostMapping
    fun saveProduct(@RequestBody dummyEntity: DummyEntity): ResponseEntity<DummyEntity> {
        return ResponseEntity<DummyEntity>(dummyService.saveDummyEntity(dummyEntity), HttpStatus.CREATED)
    }
}