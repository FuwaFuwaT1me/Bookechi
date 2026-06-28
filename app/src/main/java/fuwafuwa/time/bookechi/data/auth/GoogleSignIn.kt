package fuwafuwa.time.bookechi.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/**
 * Получение Google idToken через Credential Manager (системный выбор аккаунта).
 *
 * Живёт в UI-слое по смыслу: показ выбора аккаунта требует Activity-контекста. Полученный
 * idToken передаётся в [AuthRepository.signInWithGoogle] — там уже бизнес-логика входа.
 */
object GoogleSignIn {

    /** Google-провайдер не настроен (нет web client id в ресурсах). */
    class NotConfiguredException : Exception(
        "Google sign-in is not configured: enable Google in Firebase Auth, add the debug SHA-1, " +
            "then re-download google-services.json.",
    )

    /**
     * Показывает выбор Google-аккаунта и возвращает idToken.
     * [context] должен быть Activity-контекстом (нужен для показа UI).
     *
     * При отмене пользователем вернётся [Result.failure] с
     * [androidx.credentials.exceptions.GetCredentialCancellationException].
     */
    suspend fun getIdToken(context: Context): Result<String> {
        val serverClientId = webClientId(context)
            ?: return Result.failure(NotConfiguredException())

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return runCatching {
            val response = CredentialManager.create(context).getCredential(context, request)
            GoogleIdTokenCredential.createFrom(response.credential.data).idToken
        }
    }

    /**
     * Web client id, который плагин google-services кладёт в ресурсы как
     * `default_web_client_id` — но только если в проекте включён Google-провайдер.
     * Ищем по имени, чтобы код собирался и до настройки Google (анонимный вход работает без него).
     */
    private fun webClientId(context: Context): String? {
        val resId = context.resources.getIdentifier(
            "default_web_client_id", "string", context.packageName,
        )
        return if (resId != 0) context.getString(resId) else null
    }
}
