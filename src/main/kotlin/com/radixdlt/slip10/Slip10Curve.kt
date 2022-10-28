package com.radixdlt.slip10

import com.radixdlt.crypto.ec.EllipticCurveType

fun EllipticCurveType.toHMacKey(): ByteArray {
    return when (this) {
        EllipticCurveType.Secp256k1 -> "Bitcoin seed".toByteArray()
        EllipticCurveType.P256 -> "Nist256p1 seed".toByteArray()
        EllipticCurveType.Ed25519 -> "ed25519 seed".toByteArray()
    }
}
