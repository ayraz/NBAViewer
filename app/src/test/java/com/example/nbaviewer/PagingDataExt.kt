package com.example.nbaviewer

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single

/**
 * Extracts the list of data from a PagingData object.
 * For testing transformations on PagingData.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T : Any> PagingData<T>.toList(): List<T> {
    val flow = PagingData::class.java.getDeclaredField("flow").apply {
        isAccessible = true
    }.get(this) as Flow<Any?>
    val staticList = flow.single()
    val staticListClass = Class.forName("androidx.paging.PageEvent\$StaticList")
    val dataField = staticListClass.getDeclaredField("data").apply {
        isAccessible = true
    }
    val data = dataField.get(staticList) as List<Any?>
    return data as List<T>
}