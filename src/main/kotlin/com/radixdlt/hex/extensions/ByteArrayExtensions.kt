package com.radixdlt.hex.extensions

import com.radixdlt.hex.encode

/**
 * Converts [this] [ByteArray] into its hexadecimal representation without prepending any prefix to it.
 */
fun ByteArray.toHexString(): String = encode(this)
