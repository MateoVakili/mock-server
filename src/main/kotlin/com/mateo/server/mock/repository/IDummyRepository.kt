package com.mateo.server.mock.repository

import com.mateo.server.mock.entity.DummyEntity

import org.springframework.data.jpa.repository.JpaRepository


interface IDummyRepository : JpaRepository<DummyEntity, Long>