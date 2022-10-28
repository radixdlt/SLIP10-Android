package com.radixdlt.crypto.hash

sealed class DigestParams(val keySize: Int) {
    object Sha256 : DigestParams(256)
    object Sha512 : DigestParams(512)
}
