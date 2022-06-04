package com.mateo.server.mock.service.passwords

import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator
import org.springframework.stereotype.Service

@Service
class PasswordService {
    fun generatePassword(length: Int): String {
        val digits = CharacterRule(EnglishCharacterData.Digit)
        val special = CharacterRule(EnglishCharacterData.Special)
        val upper = CharacterRule(EnglishCharacterData.UpperCase)
        val lower = CharacterRule(EnglishCharacterData.LowerCase)
        return PasswordGenerator().generatePassword(length, upper, lower, digits, special)
    }
}