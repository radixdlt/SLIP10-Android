package com.radixdlt.derivation

import com.radixdlt.derivation.model.KeyType
import com.radixdlt.derivation.model.NetworkId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AccountHDDerivationPathTest {

    @Test
    fun `verify account derivation path for mainnet at index 0 for signing authentication`() {
        val accountHDDerivationPath = AccountHDDerivationPath(
            networkId = NetworkId.Mainnet,
            accountIndex = 0,
            keyType = KeyType.SignAuth
        )

        assertEquals(accountHDDerivationPath.path, "m/44'/1022'/1'/525'/0'/706'")
    }

    @Test
    fun `verify account derivation path for betanet at index 0 for signing authentication`() {
        val accountHDDerivationPath = AccountHDDerivationPath(
            networkId = NetworkId.Adapanet,
            accountIndex = 0,
            keyType = KeyType.SignAuth
        )

        assertEquals(accountHDDerivationPath.path, "m/44'/1022'/10'/525'/0'/706'")
    }

    @Test
    fun `verify account derivation path for betanet at index 1 for signing authentication`() {
        val accountHDDerivationPath = AccountHDDerivationPath(
            networkId = NetworkId.Adapanet,
            accountIndex = 1,
            keyType = KeyType.SignAuth
        )

        assertEquals(accountHDDerivationPath.path, "m/44'/1022'/10'/525'/1'/706'")
    }

    @Test
    fun `verify account derivation path for betanet at index 1 for signing transaction`() {
        val accountHDDerivationPath = AccountHDDerivationPath(
            networkId = NetworkId.Adapanet,
            accountIndex = 1,
            keyType = KeyType.SignTransaction
        )

        assertEquals(accountHDDerivationPath.path, "m/44'/1022'/10'/525'/1'/1238'")
    }
}