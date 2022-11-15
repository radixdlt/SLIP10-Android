package com.radixdlt.derivation

import com.radixdlt.derivation.model.KeyType
import com.radixdlt.derivation.model.NetworkId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IdentityHDDerivationPathTest {

    @Test
    fun `verify identity derivation path for mainnet at index 0 for signing authentication`() {
        val identityHDDerivationPath = IdentityHDDerivationPath(
            networkId = NetworkId.Mainnet,
            identityIndex = 0,
            keyType = KeyType.SignAuth
        )

        assertEquals(identityHDDerivationPath.path, "m/44'/1022'/1'/618'/0'/706'")
    }

    @Test
    fun `verify identity derivation path for alphanet at index 0 for signing authentication`() {
        val identityHDDerivationPath = IdentityHDDerivationPath(
            networkId = NetworkId.Alphanet,
            identityIndex = 0,
            keyType = KeyType.SignAuth
        )

        assertEquals(identityHDDerivationPath.path, "m/44'/1022'/10'/618'/0'/706'")
    }

    @Test
    fun `verify identity derivation path for mainnet at index 1 for signing authentication`() {
        val identityHDDerivationPath = IdentityHDDerivationPath(
            networkId = NetworkId.Mainnet,
            identityIndex = 1,
            keyType = KeyType.SignAuth
        )

        assertEquals(identityHDDerivationPath.path, "m/44'/1022'/1'/618'/1'/706'")
    }

    @Test
    fun `verify identity derivation path for mainnet at index 0 for signing transaction`() {
        val identityHDDerivationPath = IdentityHDDerivationPath(
            networkId = NetworkId.Mainnet,
            identityIndex = 0,
            keyType = KeyType.SignTransaction
        )

        assertEquals(identityHDDerivationPath.path, "m/44'/1022'/1'/618'/0'/1238'")
    }
}