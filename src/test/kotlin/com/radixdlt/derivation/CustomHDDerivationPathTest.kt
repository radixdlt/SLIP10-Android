package com.radixdlt.derivation

import com.radixdlt.bip44.BIP44
import com.radixdlt.bip44.BIP44Element
import com.radixdlt.derivation.model.CoinType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CustomHDDerivationPathTest {

    @Test
    fun `verify custom derivation path for 3 hardened bip44 elements`() {
        val bip44 = BIP44(
            path = listOf(
                BIP44Element(
                    hardened = true,
                    number = 44
                ),
                BIP44Element(
                    hardened = true,
                    number = CoinType.RadixDlt.value
                ),
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
        val customHDDerivationPath = CustomHDDerivationPath(
            bip44 = bip44
        )

        assertEquals(customHDDerivationPath.path, "m/44'/1022'/10'/20'/30'")
    }

    @Test
    fun `verify invalid custom derivation path without radix coin`() {
        val bip44 = BIP44(
            path = listOf(
                BIP44Element(
                    hardened = true,
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
        val customHDDerivationPath = CustomHDDerivationPath(
            bip44 = bip44
        )
        assertThrows(IllegalArgumentException::class.java) {
            customHDDerivationPath.path
        }
    }

    @Test
    fun `verify empty custom derivation path for no bip44 elements`() {
        val bip44 = BIP44(
            path = emptyList()
        )
        val customHDDerivationPath = CustomHDDerivationPath(
            bip44 = bip44
        )

        assertThrows(IllegalArgumentException::class.java) {
            customHDDerivationPath.path
        }
    }
}