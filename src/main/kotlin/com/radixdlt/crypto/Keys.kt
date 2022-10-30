package com.radixdlt.crypto

import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.extensions.toBytesPadded
import com.radixdlt.model.ECKeyPair
import com.radixdlt.model.PUBLIC_KEY_SIZE
import com.radixdlt.crypto.ec.toEllipticCurve
import com.radixdlt.model.PRIVATE_KEY_SIZE
import org.bouncycastle.math.ec.rfc8032.Ed25519.generatePublicKey


fun ECKeyPair.getCompressedPublicKey(): ByteArray {
    if (publicKey.curveType == EllipticCurveType.Ed25519) {
        val buffer = ByteArray(33)
        generatePublicKey(privateKey.key.toBytesPadded(PRIVATE_KEY_SIZE), 0, buffer, 1)
        return buffer
    }
    //add the uncompressed prefix
    val ret = publicKey.key.toBytesPadded(PUBLIC_KEY_SIZE + 1)
    ret[0] = 4
    val point = publicKey.curveType.toEllipticCurve().decodePoint(ret)
    return point.encoded(true)
}

