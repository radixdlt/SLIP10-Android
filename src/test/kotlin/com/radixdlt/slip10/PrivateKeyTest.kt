package com.radixdlt.slip10

import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.model.PRIVATE_KEY_SIZE
import com.radixdlt.model.PrivateKey
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `when byte array is 32 bytes but has only 1, verify the same is returned from keyByteArray`() {
        val byteArray =
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
        val privateKey = PrivateKey(byteArray, EllipticCurveType.Ed25519)
        assertTrue(privateKey.keyByteArray().contentEquals(byteArray))
    }

    @Test
    fun `when byte array is only one byte (1), verify that output is also one but has 32 bytes length`() {
        val byteArray =
            byteArrayOf(1)
        val outputByteArray =
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
        val privateKey = PrivateKey(byteArray, EllipticCurveType.Ed25519)
        assertTrue(privateKey.keyByteArray().contentEquals(outputByteArray))
    }
}