package com.radixdlt.model

import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.extensions.hexToBigInteger
import com.radixdlt.extensions.toBigInteger
import com.radixdlt.hex.model.HexString
import com.radixdlt.slip10.model.ExtendedKey
import java.math.BigInteger

class PrivateKey(val key: BigInteger, val curveType: EllipticCurveType) {
    constructor(privateKey: ByteArray, curveType: EllipticCurveType) : this(privateKey.toBigInteger(), curveType)
    constructor(hex: HexString, curveType: EllipticCurveType) : this(hex.hexToBigInteger(), curveType)

    override fun toString() = key.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PrivateKey

        if (key != other.key) return false
        if (curveType != other.curveType) return false

        return true
    }
}

class PublicKey(val key: BigInteger, val curveType: EllipticCurveType) {
    constructor(publicKey: ByteArray, curveType: EllipticCurveType) : this(publicKey.toBigInteger(), curveType)
    constructor(publicKey: HexString, curveType: EllipticCurveType) : this(publicKey.hexToBigInteger(), curveType)

    override fun toString() = key.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicKey

        if (key != other.key) return false
        if (curveType != other.curveType) return false

        return true
    }
}

data class ECKeyPair(val privateKey: PrivateKey, val publicKey: PublicKey)

fun ECKeyPair.curveType(): EllipticCurveType {
    return privateKey.curveType
}
