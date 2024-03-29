package com.radixdlt.extensions

fun List<Byte>.startsWith(prefix: List<Byte>)
        = size >= prefix.size &&
        prefix.indices.all { this[it] == prefix[it] }