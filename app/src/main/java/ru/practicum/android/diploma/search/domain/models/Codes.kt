package ru.practicum.android.diploma.search.domain.models

enum class Codes(private val code: Int) {
    NO_NET_CONNECTION(500),
    SUCCESS(200),
    NO_RESULTS(403),
    ERROR(400);
}