package fuwafuwa.time.bookechi.data.auth

/**
 * Доменная модель текущего пользователя — UI и MVI не зависят напрямую от FirebaseUser.
 *
 * Анонимный аккаунт ([isAnonymous] == true) создаётся автоматически при первом запуске,
 * чтобы приложение работало без явного входа. Через [signInWithGoogle] он апгрейдится
 * до полноценного, сохраняя [uid] и все данные.
 */
data class AuthUser(
    val uid: String,
    val isAnonymous: Boolean,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
) {
    /** Вошёл по-настоящему (Google), а не анонимно. */
    val isSignedIn: Boolean get() = !isAnonymous
}
