package com.dmrhimali.auth.jwtauth.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.TextNode
import com.dmrhimali.auth.jwtauth.model.Role

class RoleDeserializer : JsonDeserializer<Role>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Role {
        val node = p.codec.readTree<TextNode>(p)  // Get the string value
        return Role(name = node.asText())  // Create and return the Role object
    }
}