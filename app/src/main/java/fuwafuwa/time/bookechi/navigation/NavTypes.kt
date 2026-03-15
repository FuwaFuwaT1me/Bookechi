package fuwafuwa.time.bookechi.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import fuwafuwa.time.bookechi.data.model.Book

object BookNavType : NavType<Book>(isNullableAllowed = false) {
    private val gson = Gson()

    override fun put(bundle: Bundle, key: String, value: Book) {
        bundle.putParcelable(key, value)
    }

    @Suppress("DEPRECATION")
    override fun get(bundle: Bundle, key: String): Book? = bundle.getParcelable(key)

    override fun parseValue(value: String): Book {
        return gson.fromJson(Uri.decode(value), Book::class.java)
    }

    override fun serializeAsValue(value: Book): String {
        return Uri.encode(gson.toJson(value))
    }
}
