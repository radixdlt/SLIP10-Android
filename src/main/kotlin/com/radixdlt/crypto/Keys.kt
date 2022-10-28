package com.radixdlt.crypto

import com.radixdlt.extensions.toBytesPadded
import com.radixdlt.model.ECKeyPair
import com.radixdlt.model.PUBLIC_KEY_SIZE
import com.radixdlt.crypto.ec.toEllipticCurve


fun ECKeyPair.getCompressedPublicKey(): ByteArray {
    //add the uncompressed prefix
    val ret = publicKey.key.toBytesPadded(PUBLIC_KEY_SIZE + 1)
    ret[0] = 4
    val point = publicKey.curveType.toEllipticCurve().decodePoint(ret)
    return point.encoded(true)
}

