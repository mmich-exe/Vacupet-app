package com.example.vacupet.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val LISTADO = "listado"
    const val AGENDA = "agenda"
    const val INICIO = "inicio"            // menú principal después del login
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"            // ver perfil
    const val PERFIL_EDITAR = "perfil_editar"

    const val ARG_ID = "id"

    // Estas rutas reciben el ID de la mascota como parámetro.
    const val DETALLE = "detalle/{$ARG_ID}"
    fun detalle(id: Int) = "detalle/$id"

    const val RECORDATORIOS = "recordatorios/{$ARG_ID}"
    fun recordatorios(id: Int) = "recordatorios/$id"

    // Formulario: id = -1 significa "registrar mascota nueva"; otro valor significa "editar".
    const val FORMULARIO = "formulario?$ARG_ID={$ARG_ID}"
    fun formulario(id: Int? = null) = "formulario?$ARG_ID=${id ?: -1}"
}
