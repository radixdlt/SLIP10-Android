package com.radixdlt.derivation

import com.radixdlt.bip44.BIP44
import com.radixdlt.bip44.BIP44Element
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CustomHDDerivationPathTest {

    @Test
    fun `verify custom derivation path for 3 hardened bip44 elements`() {
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
        val customHDDerivationPath = CustomHDDerivationPath(
            bip44 = bip44
        )

        assertEquals(customHDDerivationPath.path, "m/44'/1022'/10'/20'/30'")
    }

    @Test
    fun `verify custom derivation path for 1 hardened bip44 elements out of 3`() {
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

        assertEquals(customHDDerivationPath.path, "m/44'/1022'/10'/20/30")
    }

    @Test
    fun `verify custom derivation path for no bip44 elements`() {
        val bip44 = BIP44(
            path = emptyList()
        )
        val customHDDerivationPath = CustomHDDerivationPath(
            bip44 = bip44
        )

        assertEquals(customHDDerivationPath.path, "m/44'/1022'")
    }
}