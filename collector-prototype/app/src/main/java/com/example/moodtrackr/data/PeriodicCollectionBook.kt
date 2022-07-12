package com.example.moodtrackr.data

data class PeriodicCollectionBook(
    var book: MutableMap<Int, PeriodicCollection>
    )
{
    constructor() : this(mutableMapOf<Int, PeriodicCollection>())
    operator fun get(idx: Int): PeriodicCollection? { return this.book[idx] }
    operator fun set(idx: Int, value: PeriodicCollection) { this.book[idx] = value }
    fun size(): Int { return this.book.keys.size }
    fun insert(coll: PeriodicCollection){ book[size()] = coll }
}

