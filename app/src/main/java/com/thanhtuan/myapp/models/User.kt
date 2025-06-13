package com.thanhtuan.myapp.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val isParent: Boolean = true,
    val age: Int? = null,
    val linkedParent: String? = null,
    val linkedChildren: List<String> = emptyList(),
    val coins: Int = 0
) {
    companion object {
        const val ROLE_PARENT = "parent"
        const val ROLE_CHILD = "child"
    }
} 