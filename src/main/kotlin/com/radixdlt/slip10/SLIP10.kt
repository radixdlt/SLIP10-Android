package com.radixdlt.slip10

import com.radixdlt.slip10.model.CHAINCODE_SIZE
import com.radixdlt.slip10.model.ExtendedKey
import com.radixdlt.slip10.model.Seed
import com.radixdlt.crypto.getCompressedPublicKey
import com.radixdlt.crypto.mac.Hmac
import com.radixdlt.crypto.*
import com.radixdlt.extensions.toBytesPadded
import com.radixdlt.model.ECKeyPair
import com.radixdlt.model.PRIVATE_KEY_SIZE
import com.radixdlt.model.PrivateKey
import com.radixdlt.bip44.BIP44
import com.radixdlt.bip44.BIP44Element
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.ec.toEllipticCurve
import com.radixdlt.crypto.hash.ripemd160.extensions.digestRipemd160
import com.radixdlt.crypto.hash.sha256.extensions.sha256
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.KeyException

fun Seed.toKey(pathString: String, curveType: EllipticCurveType = EllipticCurveType.Secp256k1, testnet: Boolean = false) = BIP44(pathString).path
        .fold(toExtendedKey(curveType = curveType, testnet = testnet)) { current, bip44Element ->
            current.generateChildKey(bip44Element)
        }


/**
 * Gets an [Int] representation of public key hash
 * @return an Int built from the first 4 bytes of the result of hash160 over the compressed public key
 */
fun ECKeyPair.computeFingerPrint(): Int {
    val pubKeyHash = getCompressedPublicKey()
            .sha256()
            .digestRipemd160()
    var fingerprint = 0
    for (i in 0..3) {
        fingerprint = fingerprint shl 8
        fingerprint = fingerprint or (pubKeyHash[i].toInt() and 0xff)
    }
    return fingerprint
}

fun ExtendedKey.generateChildKey(element: BIP44Element): ExtendedKey {
    require(!(element.hardened && keyPair.privateKey.key == BigInteger.ZERO)) {
        "need private key for private generation using hardened paths"
    }

    val extended: ByteArray
    val pub = keyPair.getCompressedPublicKey()
    if (element.hardened) {
        val privateKeyPaddedBytes = keyPair.privateKey.key.toBytesPadded(PRIVATE_KEY_SIZE)

        extended = ByteBuffer
                .allocate(privateKeyPaddedBytes.size + 5)
                .order(ByteOrder.BIG_ENDIAN)
                .put(0)
                .put(privateKeyPaddedBytes)
                .putInt(element.numberWithHardeningFlag)
                .array()
    } else {
        //non-hardened
        if (curveType() == EllipticCurveType.Ed25519) {
            throw KeyException("Unhardened paths not supported for Ed25519")
        }
        extended = ByteBuffer
                .allocate(pub.size + 4)
                .order(ByteOrder.BIG_ENDIAN)
                .put(pub)
                .putInt(element.numberWithHardeningFlag)
                .array()
    }

    val curve = curveType().toEllipticCurve()

    var S = extended
    var IL: ByteArray
    var IR: ByteArray
    var k: BigInteger
    var m: BigInteger
    while (true) {
        val I = Hmac.init(chainCode).generate(S)
        IL = I.copyOfRange(0, PRIVATE_KEY_SIZE)
        IR = I.copyOfRange(PRIVATE_KEY_SIZE, PRIVATE_KEY_SIZE + CHAINCODE_SIZE)
        if (curveType() == EllipticCurveType.Ed25519) { // CKDpriv Ed25519
            return ExtendedKey(
                PrivateKey(IL, curveType()).toECKeyPair(),
                IR,
                (depth + 1).toByte(),
                keyPair.computeFingerPrint(),
                element.numberWithHardeningFlag,
                versionBytes
            )
        }
        m = BigInteger(1, IL)
        if (keyPair.privateKey.key != BigInteger.ZERO) { // CKDpriv Secp256k1/P256
            k = m.add(keyPair.privateKey.key).mod(curve.n)
            if (m < curveType().toEllipticCurve().n && k != BigInteger.ZERO) {
                return ExtendedKey(
                    PrivateKey(k, curveType()).toECKeyPair(),
                    IR,
                    (depth + 1).toByte(),
                    keyPair.computeFingerPrint(),
                    element.numberWithHardeningFlag,
                    versionBytes
                )
            }
        } else { // CKDpub Secp256k1/P256
            val q = curve.g.mul(m).add(curve.decodePoint(pub)).normalize()
            if (!q.isInfinity()) {
                val point = curve.createPoint(q.x, q.y)

                return ExtendedKey(
                    ECKeyPair(PrivateKey(BigInteger.ZERO, curveType()), point.toPublicKey(curveType())),
                    IR,
                    (depth + 1).toByte(),
                    keyPair.computeFingerPrint(),
                    element.numberWithHardeningFlag,
                    versionBytes
                )
            }
        }
        S = ByteBuffer
            .allocate(IR.size + 5)
            .order(ByteOrder.BIG_ENDIAN)
            .put(1)
            .put(IR)
            .putInt(element.numberWithHardeningFlag)
            .array()
        continue
    }
}
