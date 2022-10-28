package com.radixdlt.crypto.ec

import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import java.math.BigInteger
import java.util.*


fun EllipticCurve.publicFromPrivate(privateKey: BigInteger): BigInteger {

    val point = publicPointFromPrivate(privateKey)

    val encoded = point.getEncoded(false)
    return BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.size))
}

/**
 * Returns public key point from the given private key.
 */
private fun EllipticCurve.publicPointFromPrivate(privateKey: BigInteger): ECPoint {
    /*
 * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
 * order, but that could change in future versions.
 */
    val postProcessedPrivateKey = if (privateKey.bitLength() > curveParams.n.bitLength()) {
        privateKey.mod(domainParams.n)
    } else {
        privateKey
    }
    return FixedPointCombMultiplier().multiply(domainParams.g, postProcessedPrivateKey)
}

