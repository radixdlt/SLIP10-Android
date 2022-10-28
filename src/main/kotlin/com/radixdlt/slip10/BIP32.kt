@file:JvmName("BIP32")

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
import com.radixdlt.model.curveType
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.InvalidKeyException
import java.security.KeyException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException

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
    try {
        require(!(element.hardened && keyPair.privateKey.key == BigInteger.ZERO)) {
            "need private key for private generation using hardened paths"
        }
        val mac = Hmac.init(chainCode)

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
            extended = ByteBuffer
                    .allocate(pub.size + 4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .put(pub)
                    .putInt(element.numberWithHardeningFlag)
                    .array()
        }
        val lr = mac.generate(extended)
        val l = lr.copyOfRange(0, PRIVATE_KEY_SIZE)
        val r = lr.copyOfRange(PRIVATE_KEY_SIZE, PRIVATE_KEY_SIZE + CHAINCODE_SIZE)

        val curveType = keyPair.curveType()
        val curve = keyPair.curveType().toEllipticCurve()

        val m = BigInteger(1, l)
        if (m >= curve.n) {
            throw KeyException("Child key derivation resulted in a key with higher modulus. Suggest deriving the next increment.")
        }

        return if (keyPair.privateKey.key != BigInteger.ZERO) {
            val k = m.add(keyPair.privateKey.key).mod(curve.n)
            if (k == BigInteger.ZERO) {
                throw KeyException("Child key derivation resulted in zeros. Suggest deriving the next increment.")
            }
            ExtendedKey(PrivateKey(k, curveType).toECKeyPair(), r, (depth + 1).toByte(), keyPair.computeFingerPrint(), element.numberWithHardeningFlag, versionBytes)
        } else {
            val q = curve.g.mul(m).add(curve.decodePoint(pub)).normalize()
            if (q.isInfinity()) {
                throw KeyException("Child key derivation resulted in zeros. Suggest deriving the next increment.")
            }
            val point = curve.createPoint(q.x, q.y)

            ExtendedKey(ECKeyPair(PrivateKey(BigInteger.ZERO, curveType), point.toPublicKey(curveType)), r, (depth + 1).toByte(), keyPair.computeFingerPrint(), element.numberWithHardeningFlag, versionBytes)
        }
    } catch (e: NoSuchAlgorithmException) {
        throw KeyException(e)
    } catch (e: NoSuchProviderException) {
        throw KeyException(e)
    } catch (e: InvalidKeyException) {
        throw KeyException(e)
    }

}

