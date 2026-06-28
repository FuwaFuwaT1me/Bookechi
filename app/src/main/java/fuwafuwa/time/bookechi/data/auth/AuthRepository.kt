package fuwafuwa.time.bookechi.data.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Обёртка над FirebaseAuth. Источник правды по текущему аккаунту.
 *
 * Стратегия (anonymous-first): на старте делаем тихий анонимный вход, поэтому uid есть
 * всегда и данные сразу скоупятся под него. Вход через Google апгрейдит анонимный аккаунт
 * до полноценного без потери данных (linkWithCredential).
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

    /** Поток текущего пользователя. Эмитит сразу текущее состояние и на каждое изменение. */
    val authUser: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val currentUser: AuthUser?
        get() = auth.currentUser?.toAuthUser()

    /** Гарантирует наличие аккаунта: если никто не вошёл — тихий анонимный вход. */
    suspend fun ensureSignedIn(): AuthUser {
        auth.currentUser?.let { return it.toAuthUser() }
        val result = auth.signInAnonymously().await()
        return result.user!!.toAuthUser()
    }

    /**
     * Вход через Google по idToken (его добывает UI через Credential Manager).
     *
     * Если текущий аккаунт анонимный — апгрейдим его (linkWithCredential), сохраняя uid
     * и данные. При коллизии (этот Google-аккаунт уже существует, например с другого
     * устройства) — входим в существующий аккаунт.
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val current = auth.currentUser
        val result = if (current != null && current.isAnonymous) {
            try {
                current.linkWithCredential(credential).await()
            } catch (e: FirebaseAuthUserCollisionException) {
                // Аккаунт уже привязан к другому профилю — входим в него.
                // TODO(phase 4): предложить мёрж локальных анонимных данных.
                auth.signInWithCredential(credential).await()
            }
        } else {
            auth.signInWithCredential(credential).await()
        }
        result.user!!.toAuthUser()
    }

    /** Вход по почте и паролю. */
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        result.user!!.toAuthUser()
    }

    /**
     * Регистрация по почте. Если текущий аккаунт анонимный — апгрейдим его
     * (linkWithCredential), сохраняя данные; иначе создаём новый. Затем проставляем имя.
     */
    suspend fun registerWithEmail(name: String, email: String, password: String): Result<AuthUser> = runCatching {
        val credential = EmailAuthProvider.getCredential(email.trim(), password)
        val current = auth.currentUser
        val result = if (current != null && current.isAnonymous) {
            current.linkWithCredential(credential).await()
        } else {
            auth.createUserWithEmailAndPassword(email.trim(), password).await()
        }
        val user = result.user!!
        val displayName = name.trim()
        if (displayName.isNotEmpty()) {
            user.updateProfile(userProfileChangeRequest { this.displayName = displayName }).await()
            user.reload().await()
        }
        (auth.currentUser ?: user).toAuthUser()
    }

    /** Отправляет письмо со ссылкой для сброса пароля. */
    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    /** Выход с возвратом в анонимный режим — приложение продолжает работать. */
    suspend fun signOut(): AuthUser {
        auth.signOut()
        return ensureSignedIn()
    }
}

private fun FirebaseUser.toAuthUser() = AuthUser(
    uid = uid,
    isAnonymous = isAnonymous,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl?.toString(),
)
