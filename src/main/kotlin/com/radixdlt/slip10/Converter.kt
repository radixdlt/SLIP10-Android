package com.radixdlt.slip10

import com.radixdlt.slip10.model.*
import com.radixdlt.crypto.mac.Hmac
import com.radixdlt.crypto.toECKeyPair
import com.radixdlt.crypto.base58.decodeBase58WithChecksum
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.ec.toEllipticCurve
import com.radixdlt.hex.extensions.toHexString
import com.radixdlt.model.*
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.KeyException

fun Seed.toNoPrefixHexString(): String {
    return seed.toHexString()
}

fun Seed.toExtendedKey(publicKeyOnly: Boolean = false, curveType: EllipticCurveType = EllipticCurveType.Secp256k1, testnet: Boolean = false): ExtendedKey {
    var S = seed
    var IL: ByteArray
    var IR: ByteArray
    while (true) {
        val I = Hmac.init(curveType.toHMacKey()).generate(S)
        IL = I.copyOfRange(0, PRIVATE_KEY_SIZE)
        IR = I.copyOfRange(PRIVATE_KEY_SIZE, PRIVATE_KEY_SIZE + CHAINCODE_SIZE)
        val m = BigInteger(1, IL)
        if (curveType != EllipticCurveType.Ed25519 && (m >= curveType.toEllipticCurve().n || m == BigInteger.ZERO)) {
            S = I
            continue
        }
        break
    }
    val keyPair = PrivateKey(IL, curveType).toECKeyPair()
    return if (publicKeyOnly) {
        val pubKeyPair = ECKeyPair(PrivateKey(BigInteger.ZERO, curveType), keyPair.publicKey)
        ExtendedKey(pubKeyPair, IR, 0, 0, 0, if (testnet) tpub else xpub)
    } else {
        ExtendedKey(keyPair, IR, 0, 0, 0, if (testnet) tprv else xprv)
    }
}

fun ExtendedKey.curveType(): EllipticCurveType {
    return keyPair.curveType()
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
