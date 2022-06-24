package com.example.moodtrackr.data

data class HourlyCollectionBook(
    var book: MutableMap<Int, HourlyCollection>
    )
{
    constructor() : this(mutableMapOf<Int, HourlyCollection>())
    operator fun get(idx: Int): HourlyCollection? { return this.book[idx] }
    operator fun set(idx: Int, value: HourlyCollection) { if (idx<72) this.book[idx] = value }
    fun size(): Int { return this.book.keys.size }
    fun isFull(): Boolean { return this.book.keys.size == 72 }
    fun insert(coll: HourlyCollection){ book[size()] = coll }
}

