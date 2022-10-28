package com.radixdlt.crypto.hash.sha256.extensions

import com.radixdlt.crypto.hash.sha256.Sha256

/**
 * Returns the SHA256 digest of this byte array.
 */
fun ByteArray.sha256(): ByteArray = Sha256.digest(this)

/**
 * Returns the SHA256 digest of this string.
 */
fun String.sha256(): ByteArray = this.encodeToByteArray().sha256()
