package com.mateo.server.mock.service

import com.mateo.server.mock.entity.DummyEntity
import com.mateo.server.mock.repository.IDummyRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class DummyServiceImpl {

    @Autowired
    private lateinit var dummyRepository: IDummyRepository

    fun saveDummyEntity(dummyEntity: DummyEntity): DummyEntity {
        return dummyRepository.save(dummyEntity)
    }

    fun findAll() = dummyRepository.findAll()
}