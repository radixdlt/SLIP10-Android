package com.radixdlt.crypto.base58

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import com.radixdlt.hex.extensions.hexToByteArray
import com.radixdlt.hex.model.HexString

class Base58Test {

    // Tests from https://github.com/bitcoin/bitcoin/blob/master/src/test/data/base58_encode_decode.json
    val TEST_VECTORS = mapOf(
            "" to "",
            "61" to "2g",
            "626262" to "a3gV",
            "636363" to "aPEr",
            "73696d706c792061206c6f6e6720737472696e67" to "2cFupjhnEsSn59qHXstmK2ffpLv2",
            "00eb15231dfceb60925886b67d065299925915aeb172c06647" to "1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L",
            "516b6fcd0f" to "ABnLTmg",
            "bf4f89001e670274dd" to "3SEo3LWLoPntC",
            "572e4794" to "3EFU7m",
            "ecac89cad93923c02321" to "EJDM8drfXA6uyA",
            "10c8511e" to "Rt5zm",
            "00000000000000000000" to "1111111111"
    )

    @Test
    fun encodingToBase58Works() {
        TEST_VECTORS.forEach {
            assertThat(HexString(it.key).hexToByteArray().encodeToBase58String()).isEqualTo(it.value)
        }
    }

    @Test
    fun decodingFromBase58Works() {
        TEST_VECTORS.forEach {
            assertThat(it.value.decodeBase58()).isEqualTo(HexString(it.key).hexToByteArray())
        }
    }
}