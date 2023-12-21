package ru.practicum.android.diploma.search.presentation

import androidx.recyclerview.widget.DiffUtil
import ru.practicum.android.diploma.search.domain.models.Vacancy

class DiffCallbackJob(private val oldList: List<Vacancy>, private val newList: List<Vacancy>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old.id == new.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return old == new
    }
}