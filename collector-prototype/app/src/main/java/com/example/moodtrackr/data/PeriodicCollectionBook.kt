package com.example.moodtrackr.data

data class PeriodicCollectionBook(
    var book: MutableMap<Int, PeriodicCollection>
    )
{
    constructor() : this(mutableMapOf<Int, PeriodicCollection>())
    operator fun get(idx: Int): PeriodicCollection? { return this.book[idx] }
    operator fun set(idx: Int, value: PeriodicCollection) { if (idx<72) this.book[idx] = value }
    fun size(): Int { return this.book.keys.size }
    fun isFull(): Boolean { return this.book.keys.size == 72 }
    fun insert(coll: PeriodicCollection){ book[size()] = coll }
}

