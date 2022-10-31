package com.radixdlt.crypto.ec

import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import java.math.BigInteger

enum class EllipticCurveType {
    Secp256k1,
    P256,
    Ed25519,
}

fun EllipticCurveType.toName(): String {
    return when (this) {
        EllipticCurveType.Secp256k1 -> "secp256k1"
        EllipticCurveType.P256 -> "secp256r1"
        EllipticCurveType.Ed25519 -> "curve25519"
    }
}

class EllipticCurve(val type: EllipticCurveType) {
    val curveParams = CustomNamedCurves.getByName(type.toName())!!

    val n: BigInteger
        get() = curveParams.n

    val g: EllipticCurvePoint
        get() = curveParams.g.toCurvePoint()

    fun decodePoint(data: ByteArray): EllipticCurvePoint =
        curveParams.curve.decodePoint(data).toCurvePoint()

    fun createPoint(x: BigInteger, y: BigInteger): EllipticCurvePoint =
        curveParams.curve.createPoint(x, y).toCurvePoint()

    val domainParams = curveParams.run { ECDomainParameters(curve, g, n, h) }

    /**
     * Takes a public key in compressed encoding (including prefix)
     * and returns the key in uncompressed encoding (without prefix)
     * For Ed25519 does nothing, as there is only compressed key
     */
    fun decompressKey(publicBytes: ByteArray): ByteArray {
        if (type == EllipticCurveType.Ed25519) {
            return publicBytes
        }
        val point = decodePoint(publicBytes)
        val encoded = point.encoded()
        return encoded.copyOfRange(1, encoded.size)
    }
}

val Secp256k1 = EllipticCurve(EllipticCurveType.Secp256k1)
val P256 = EllipticCurve(EllipticCurveType.P256)
val Ed25519 = EllipticCurve(EllipticCurveType.Ed25519)

fun EllipticCurveType.toEllipticCurve(): EllipticCurve {
    return when (this) {
        EllipticCurveType.Secp256k1 -> Secp256k1
        EllipticCurveType.P256 -> P256
        EllipticCurveType.Ed25519 -> Ed25519
    }
}
