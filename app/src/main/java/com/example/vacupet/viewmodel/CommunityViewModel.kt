package com.example.vacupet.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacupet.data.model.LostPetPost
import com.example.vacupet.repository.CatRepository
import com.example.vacupet.repository.CommunityRepository
import com.example.vacupet.repository.DogRepository
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityViewModel : ViewModel() {
    private val repository = CommunityRepository()
    private val dogRepository = DogRepository()
    private val catRepository = CatRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _posts = MutableStateFlow<List<LostPetPost>>(emptyList())
    val posts: StateFlow<List<LostPetPost>> = _posts.asStateFlow()

    private val _dogBreeds = MutableStateFlow<List<String>>(emptyList())
    val dogBreeds: StateFlow<List<String>> = _dogBreeds.asStateFlow()

    private val _catBreeds = MutableStateFlow<List<String>>(emptyList())
    val catBreeds: StateFlow<List<String>> = _catBreeds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>>(Pair(0.0, 0.0))
    val currentLocation: StateFlow<Pair<Double, Double>> = _currentLocation.asStateFlow()

    private val _tempImageUri = MutableStateFlow<Uri?>(null)
    val tempImageUri: StateFlow<Uri?> = _tempImageUri.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListeningToPosts()
        loadApiBreeds()
    }

    private fun startListeningToPosts() {
        _isLoading.value = true
        listenerRegistration = repository.listenToLostPets { posts ->
            _posts.value = posts
            _isLoading.value = false
        }
    }

    private fun loadApiBreeds() {
        viewModelScope.launch {
            dogRepository.getBreeds().fold(
                onSuccess = { list ->
                    _dogBreeds.value = listOf("Mestizo / Desconocido") + list.map { it.name }
                },
                onFailure = {
                    _dogBreeds.value = listOf("Mestizo / Desconocido")
                }
            )
            catRepository.getBreeds().fold(
                onSuccess = { list ->
                    _catBreeds.value = listOf("Común Europeo / Desconocido") + list.map { it.name }
                },
                onFailure = {
                    _catBreeds.value = listOf("Común Europeo / Desconocido")
                }
            )
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _posts.value = repository.getLostPets()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPost(
        title: String,
        description: String,
        breedName: String,
        localPhotoUri: String,
        latitude: Double,
        longitude: Double,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    onResult(false, "No hay sesión activa en Firebase. Por favor inicie sesión.")
                    return@launch
                }
                val post = LostPetPost(
                    userId = currentUser.uid,
                    userEmail = currentUser.email ?: "Sin email",
                    title = title,
                    description = description,
                    breedName = breedName,
                    localPhotoUri = localPhotoUri,
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = System.currentTimeMillis()
                )
                val success = repository.addLostPetPost(post)
                if (success) {
                    onResult(true, null)
                } else {
                    onResult(false, "No se pudo guardar el aviso en Firestore.")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(id: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.deleteLostPetPost(id)
                onResult(success)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTempImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(context.cacheDir, "JPEG_${timeStamp}_.jpg")
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        _tempImageUri.value = uri
        return uri
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _currentLocation.value = Pair(location.latitude, location.longitude)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
