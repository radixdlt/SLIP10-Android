package com.radixdlt.slip10

import com.radixdlt.slip10.model.*
import com.radixdlt.crypto.mac.Hmac
import com.radixdlt.crypto.toECKeyPair
import com.radixdlt.model.ECKeyPair
import com.radixdlt.model.PRIVATE_KEY_SIZE
import com.radixdlt.model.PrivateKey
import com.radixdlt.model.PublicKey
import com.radixdlt.crypto.base58.decodeBase58WithChecksum
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.ec.toEllipticCurve
import com.radixdlt.hex.extensions.toNoPrefixHexString
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.InvalidKeyException
import java.security.KeyException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException

fun Seed.toNoPrefixHexString(): String {
    return seed.toNoPrefixHexString()
}

fun Seed.toExtendedKey(publicKeyOnly: Boolean = false, curveType: EllipticCurveType = EllipticCurveType.Secp256k1, testnet: Boolean = false): ExtendedKey {
    try {
        val lr = Hmac.init(curveType.toHMacKey()).generate(seed)
        val l = lr.copyOfRange(0, PRIVATE_KEY_SIZE)
        val r = lr.copyOfRange(PRIVATE_KEY_SIZE, PRIVATE_KEY_SIZE + CHAINCODE_SIZE)
        val m = BigInteger(1, l)
        if (m >= curveType.toEllipticCurve().n) {
            throw KeyException("Master key creation resulted in a key with higher modulus. Suggest deriving the next increment.")
        }
        val keyPair = PrivateKey(l, curveType).toECKeyPair()
        return if (publicKeyOnly) {
            val pubKeyPair = ECKeyPair(PrivateKey(BigInteger.ZERO, curveType), keyPair.publicKey)
            ExtendedKey(pubKeyPair, r, 0, 0, 0, if (testnet) tpub else xpub)
        } else {
            ExtendedKey(keyPair, r, 0, 0, 0, if (testnet) tprv else xprv)
        }
    } catch (e: NoSuchAlgorithmException) {
        throw KeyException(e)
    } catch (e: NoSuchProviderException) {
        throw KeyException(e)
    } catch (e: InvalidKeyException) {
        throw KeyException(e)
    }

}


fun XPriv.toExtendedKey(curveType: EllipticCurveType): ExtendedKey {
    val data = xPriv.decodeBase58WithChecksum()
    if (data.size != EXTENDED_KEY_SIZE) {
        throw KeyException("invalid extended key")
    }

    val buff = ByteBuffer
            .wrap(data)
            .order(ByteOrder.BIG_ENDIAN)

    val versionBytes = ByteArray(4)

    buff.get(versionBytes)

    val hasPrivate = when {
        versionBytes.contentEquals(xprv) || versionBytes.contentEquals(tprv) -> true
        versionBytes.contentEquals(xpub) || versionBytes.contentEquals(tpub) -> false
        else -> throw KeyException("invalid version bytes for an extended key")
    }

    val depth = buff.get()
    val parent = buff.int
    val sequence = buff.int

    val chainCode = ByteArray(PRIVATE_KEY_SIZE)
    buff.get(chainCode)

    val keyPair = if (hasPrivate) {
        buff.get() // ignore the leading 0
        val privateBytes = ByteArray(PRIVATE_KEY_SIZE)
        buff.get(privateBytes)
        PrivateKey(privateBytes, curveType).toECKeyPair()
    } else {
        val compressedPublicBytes = ByteArray(COMPRESSED_PUBLIC_KEY_SIZE)
        buff.get(compressedPublicBytes)
        val uncompressedPublicBytes = curveType.toEllipticCurve().decompressKey(compressedPublicBytes)
        ECKeyPair(
                PrivateKey(BigInteger.ZERO, curveType),
                PublicKey(BigInteger(1, uncompressedPublicBytes), curveType)
        )
    }
    return ExtendedKey(keyPair, chainCode, depth, parent, sequence, versionBytes)
}
