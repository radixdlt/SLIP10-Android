package com.radixdlt.model

import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.extensions.hexToBigInteger
import com.radixdlt.extensions.toBigInteger
import com.radixdlt.extensions.toBytesPadded
import com.radixdlt.hex.extensions.toHexString
import com.radixdlt.hex.model.HexString
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

    /**
     * Since BigInteger by design does not retain leading zeros, for any bytearray with leading zero elements,
     * they will be cut off when converting to BigInteger.
     * When accessing key and converting to byteArray we need to use that method which ensures that key
     * will be always 32 bytes and leading zeros will be added in front if missing
     */
    fun keyByteArray(): ByteArray {
        return key.toBytesPadded(PRIVATE_KEY_SIZE)
    }

    fun toHexString(): String {
        return keyByteArray().toHexString()
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + curveType.hashCode()
        return result
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

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + curveType.hashCode()
        return result
    }
}

data class ECKeyPair(val privateKey: PrivateKey, val publicKey: PublicKey)

fun ECKeyPair.curveType(): EllipticCurveType {
    return privateKey.curveType
}
