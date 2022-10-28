package com.radixdlt.crypto.ec

import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger

class EllipticCurvePoint(private val ecPoint: ECPoint) {
    val x: BigInteger
        get() = ecPoint.xCoord.toBigInteger()
    val y: BigInteger
        get() = ecPoint.yCoord.toBigInteger()

    fun mul(n: BigInteger): EllipticCurvePoint =
        ecPoint.multiply(n).toCurvePoint()

    fun add(p: EllipticCurvePoint): EllipticCurvePoint =
        ecPoint.add(p.ecPoint).toCurvePoint()

    fun normalize(): EllipticCurvePoint =
        ecPoint.normalize().toCurvePoint()

    fun isInfinity(): Boolean =
        ecPoint.isInfinity

    fun encoded(compressed: Boolean = false): ByteArray =
        ecPoint.getEncoded(compressed)
}

internal fun ECPoint.toCurvePoint(): EllipticCurvePoint = EllipticCurvePoint(this)
