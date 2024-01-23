package tech.almost_senseless.voskle

sealed class ErrorKind {
    data class ConnectionFailed(val message: String): ErrorKind()
    data class UnexpectedResponse(val message: String): ErrorKind()
    data class DataProcessionFailed(val message: String): ErrorKind()
    object TranscriptionTimeout: ErrorKind()
    data class ModelError(val message: String): ErrorKind()
}