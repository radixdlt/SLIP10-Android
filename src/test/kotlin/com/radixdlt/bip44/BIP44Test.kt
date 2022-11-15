package com.radixdlt.bip44

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BIP44Test {

    @Test
    fun parsingFailsForBadInput() {
        assertThrows(IllegalArgumentException::class.java){
            BIP44("abc")
        }
    }

    @Test
    fun parsingFailsForEmptyInput() {
        assertThrows(IllegalArgumentException::class.java){
            BIP44("")
        }
    }

    @Test
    fun parsingFailsForMissingNumber() {
        assertThrows(IllegalArgumentException::class.java){
            BIP44("m/0/")
        }
    }

    val stringProbes = mapOf(
            "m" to listOf(),
            "m/0" to listOf(BIP44Element(false, 0)),
            "m/0/1" to listOf(BIP44Element(false, 0), BIP44Element(false, 1)),

            "m/44'" to listOf(BIP44Element(true, 44)),
            "m/44'/1" to listOf(BIP44Element(true, 44), BIP44Element(false, 1))
    )


    val intProbes = mapOf(
            "m" to listOf(),
            "m/0" to listOf(0),
            "m/0/1" to listOf(0, 1),

            "m/0'" to listOf(0x80000000.toInt()),
            "m/1'/1" to listOf(0x80000001.toInt(), 1)
    )


    val dirtyStringProbes = mapOf(
            "m/44 ' " to listOf(BIP44Element(true, 44)),
            "m/0 /1 ' " to listOf(BIP44Element(false, 0), BIP44Element(true, 1))
    )

    @Test
    fun fromPathWork() {
        for ((key, value) in (stringProbes + dirtyStringProbes)) {
            assertThat(BIP44(key).path).isEqualTo(value)
        }
    }

    @Test
    fun toStringFromIntoWorks() {
        for ((path, ints) in (intProbes)) {
            assertThat(BIP44(path).path.map { it.numberWithHardeningFlag }).isEqualTo(ints)
        }
    }


    @Test
    fun toStringWorks() {
        for ((key, value) in (stringProbes)) {
            assertThat(key).isEqualTo(BIP44(value).toString())
        }
    }

    @Test
    fun incrementWorks() {
        assertThat(BIP44("m/0/1/2").increment())
                .isEqualTo(BIP44("m/0/1/3"))
    }

    @Test
    fun verifyHardenedDerivationPath() {
        val bip44 = BIP44(
            path = listOf(
                BIP44Element(
                    hardened = true,
                    number = 10
                ),
                BIP44Element(
                    hardened = true,
                    number = 20
                ),
                BIP44Element(
                    hardened = true,
                    number = 30
                )
            )
        )
        assertEquals(bip44.toString(), "m/10'/20'/30'")
    }

    @Test
    fun verifyUnhardenedDerivationPath() {
        val bip44 = BIP44(
            path = listOf(
                BIP44Element(
                    hardened = false,
                    number = 10
                ),
                BIP44Element(
                    hardened = false,
                    number = 20
                ),
                BIP44Element(
                    hardened = false,
                    number = 30
                )
            )
        )
        assertEquals(bip44.toString(), "m/10/20/30")
    }
}