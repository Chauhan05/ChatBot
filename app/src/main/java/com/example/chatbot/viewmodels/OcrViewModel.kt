package com.example.chatbot.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class OcrViewModel : ViewModel() {
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _extractedText = MutableStateFlow("")
    val extractedText: StateFlow<String> = _extractedText

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Lazy initialization of the text recognizer
    private val textRecognizer: TextRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
        // Reset states when a new image is selected
        _extractedText.value = ""
        _errorMessage.value = null
    }

    fun extractTextFromImage(context: Context) {
        val uri = _imageUri.value ?: run {
            _errorMessage.value = "No image selected"
            return
        }

        _isProcessing.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get bitmap from the URI with better error handling
                val bitmap = getBitmapFromUri(context, uri)
                    ?: throw IOException("Failed to load image")

                // Rotate bitmap if needed based on EXIF data
                val rotatedBitmap = rotateBitmapIfRequired(context, uri, bitmap)

                // Create input image from bitmap
                val image = InputImage.fromBitmap(rotatedBitmap, 0)

                // Process image with ML Kit
                withContext(Dispatchers.Main) {
                    processImageWithMlKit(image)
                }
            } catch (e: Exception) {
                Log.e("OCR", "Error processing image: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _extractedText.value = ""
                    _errorMessage.value = "Error processing image: ${e.message}"
                    _isProcessing.value = false
                }
            }
        }
    }

    private suspend fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            withContext(Dispatchers.IO) {
                when {
                    uri.toString().startsWith("content://") -> {
                        // For content URIs, use ContentResolver
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }

                    uri.toString().startsWith("file://") -> {
                        // For file URIs, use BitmapFactory
                        BitmapFactory.decodeFile(uri.path)
                    }

                    else -> {
                        // For other URIs, try ContentResolver first, then fallback
                        try {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        } catch (e: Exception) {
                            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                BitmapFactory.decodeStream(inputStream)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OCR", "Error getting bitmap from URI: ${e.message}", e)
            null
        }
    }

    private fun processImageWithMlKit(image: InputImage) {
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                viewModelScope.launch {
                    if (visionText.text.isNotBlank()) {
                        _extractedText.value = visionText.text
                    } else {
                        _extractedText.value = ""
                        _errorMessage.value = "No text found in image"
                    }
                    _isProcessing.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Text recognition failed: ${e.message}", e)
                viewModelScope.launch {
                    _extractedText.value = ""
                    _errorMessage.value = "Text recognition failed: ${e.message}"
                    _isProcessing.value = false
                }
            }
    }

    private suspend fun rotateBitmapIfRequired(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        var rotation = 0
        try {
            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    rotation = when (exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("OCR", "Error checking exif data: ${e.message}")
        }

        return if (rotation != 0) {
            withContext(Dispatchers.Default) {
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        } else {
            bitmap
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Close the recognizer to free up resources
        textRecognizer.close()
    }
}