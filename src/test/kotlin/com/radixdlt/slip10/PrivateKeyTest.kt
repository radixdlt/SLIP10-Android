package com.radixdlt.slip10

import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.model.PRIVATE_KEY_SIZE
import com.radixdlt.model.PrivateKey
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class PrivateKeyTest {

    @Test
    fun `when leading byte is zero, verify key byte array has correct size and zero is not cut off`() {
        val bytes = ByteArray(PRIVATE_KEY_SIZE)
        Random.nextBytes(bytes, 1, bytes.size)
        val privateKey = PrivateKey(bytes, EllipticCurveType.Ed25519)
        assertEquals(privateKey.keyByteArray().size, PRIVATE_KEY_SIZE)
    }

    @Test
    fun `when only last byte is not zero, verify key byte array has correct size and number is retained`() {
        val bytes = ByteArray(PRIVATE_KEY_SIZE)
        Random.nextBytes(bytes, bytes.size-1, bytes.size)
        val privateKey = PrivateKey(bytes, EllipticCurveType.Ed25519)
        assertEquals(privateKey.keyByteArray().size, PRIVATE_KEY_SIZE)
    }

    @Test
    fun `when byte array is 33 bytes but has leading 0, verify zero is cut off and private key is exactly 32 bytes`() {
        val bytes = ByteArray(PRIVATE_KEY_SIZE+1)
        Random.nextBytes(bytes, 1, bytes.size)
        val privateKey = PrivateKey(bytes, EllipticCurveType.Ed25519)
        assertEquals(privateKey.keyByteArray().size, PRIVATE_KEY_SIZE)
    }
}