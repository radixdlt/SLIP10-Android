package com.radixdlt.hex.extensions

import com.radixdlt.hex.encode

/**
 * Converts [this] [Collection] of bytes into its hexadecimal representation without prepending any prefix to it.
 */
fun Collection<Byte>.toHexString(): String = encode(this.toByteArray())
