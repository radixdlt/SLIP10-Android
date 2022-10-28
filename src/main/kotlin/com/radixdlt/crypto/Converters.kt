package com.radixdlt.crypto

import com.radixdlt.crypto.ec.EllipticCurvePoint
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.ec.publicFromPrivate
import com.radixdlt.crypto.ec.toEllipticCurve
import com.radixdlt.model.*
import java.math.BigInteger


fun PrivateKey.toECKeyPair() = ECKeyPair(this, publicKeyFromPrivate(this))

/**
 * Returns public key from the given private key.
 *
 * @param privateKey the private key to derive the public key from
 * @return BigInteger encoded public key
 */
fun publicKeyFromPrivate(privateKey: PrivateKey): PublicKey {
    return PublicKey(privateKey.curveType.toEllipticCurve().publicFromPrivate(privateKey.key), privateKey.curveType)
}


/**
 * Decodes an uncompressed public key (without 0x04 prefix) given an ECPoint
 */
fun EllipticCurvePoint.toPublicKey(curveType: EllipticCurveType): PublicKey {
    val encodedKey = encoded()
    return PublicKey(BigInteger(1, encodedKey.copyOfRange(1, encodedKey.size)), curveType)
}
