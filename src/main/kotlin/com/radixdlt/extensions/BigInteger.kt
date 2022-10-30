package com.radixdlt.extensions

import com.radixdlt.hex.extensions.clean0xPrefix
import com.radixdlt.hex.extensions.has0xPrefix
import com.radixdlt.hex.model.HexString
import java.math.BigInteger

fun BigInteger.toBytesPadded(length: Int): ByteArray {
    val result = ByteArray(length)
    val bytes = toByteArray()

    val offset = if (bytes[0].toInt() == 0) 1 else 0

    if (bytes.size - offset > length) {
        throw RuntimeException("Input is too large to put in byte array of size $length")
    }

    val destOffset = length - bytes.size + offset
    return bytes.copyInto(result, destinationOffset = destOffset, startIndex = offset)
}

fun BigInteger.toHexString(): String = toString(16)

fun HexString.hexToBigInteger() = BigInteger(clean0xPrefix().string, 16)

fun HexString.maybeHexToBigInteger() = if (has0xPrefix()) {
    BigInteger(clean0xPrefix().string, 16)
} else {
    BigInteger(string)
}

fun ByteArray.toBigInteger(offset: Int, length: Int) = BigInteger(1, copyOfRange(offset, offset + length))
fun ByteArray.toBigInteger() = BigInteger(1, this)
