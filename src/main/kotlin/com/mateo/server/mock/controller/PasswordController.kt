package com.mateo.server.mock.controller

import com.mateo.server.mock.entity.password.PasswordGenerateResponse
import com.mateo.server.mock.entity.password.PasswordGenerationRequest
import com.mateo.server.mock.service.passwords.PasswordService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/password")
class PasswordController {

    @Autowired
    lateinit var passwordService: PasswordService

    @GetMapping("/generate")
    fun generatePassword(@RequestBody body: PasswordGenerationRequest): ResponseEntity<PasswordGenerateResponse> {
        return ResponseEntity.ok(PasswordGenerateResponse(passwordService.generatePassword(body.length)))
    }
}