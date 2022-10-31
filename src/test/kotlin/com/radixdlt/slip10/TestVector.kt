package com.radixdlt.slip10

import com.google.gson.Gson
import com.radixdlt.bip39.mnemonicToEntropy
import com.radixdlt.bip39.model.MnemonicWords
import com.radixdlt.bip39.toSeed
import com.radixdlt.slip10.model.Seed
import com.radixdlt.bip39.wordlists.WORDLIST_ENGLISH
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.getCompressedPublicKey
import com.radixdlt.crypto.hash.sha256.extensions.toBytes
import com.radixdlt.hex.extensions.toHexString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.security.InvalidParameterException

data class TestCase(val path: String, val childKeys: List<DerivedKey>)

data class DerivedKey(val curve: String, val chainCode: String, val privateKey: String, val publicKey: String,
                      val fingerprint: String, val xpub: String, val xprv: String)

data class TestGroup(val groupId: Int, val seed: String, val mnemonicPhrase: String, val entropy: String,
                     val passphrase: String, val masterKeys: List<DerivedKey>, val testCases: List<TestCase>)

data class Vector(val createdOn: String, val author: String, val info: String, val contact: String,
                  val testGroups: List<TestGroup>)

class TestVector {
    @Test
    fun testVector() {
        // TODO: fix this/change it with getResource
        val fileContent = File("src/test/resources/raw/Slip10TestVectors/slip10_tests_#1000.json").readText()

        val vector = Gson().fromJson(fileContent, Vector::class.java)

        for (testGroup in vector.testGroups) {
            doTest(testGroup)
        }
    }

    private fun doTest(testGroup: TestGroup) {
        val mnemonic = MnemonicWords(testGroup.mnemonicPhrase)
        val entropy = mnemonic.mnemonicToEntropy(WORDLIST_ENGLISH)
        assertThat(testGroup.entropy).isEqualTo(entropy.toHexString())

        val seed = mnemonic.toSeed(testGroup.passphrase)
        assertThat(testGroup.seed).isEqualTo(seed.toHexString())

        doTest(seed, TestCase("m", testGroup.masterKeys))

        for (testCase in testGroup.testCases) {
            doTest(seed, testCase)
        }
    }

    private fun doTest(seed: Seed, testCase: TestCase) {
        val path = testCase.path
        for (derivedKey in testCase.childKeys) {
            doTest(seed, path, derivedKey)
        }
    }

    private fun doTest(seed: Seed, path: String, testKey: DerivedKey) {
        val curveType = testCaseCurveToEllipticCurveType(testKey.curve)
        val derivedKey = seed.toKey(path, curveType)

        assertThat(testKey.privateKey).isEqualTo(derivedKey.keyPair.privateKey.toHexString())
        assertThat(testKey.chainCode).isEqualTo(derivedKey.chainCode.toHexString())

        assertThat(testKey.publicKey).isEqualTo(derivedKey.keyPair.getCompressedPublicKey().toHexString())

        assertThat(testKey.fingerprint).isEqualTo(derivedKey.parentFingerprint.toBytes().toByteArray().toHexString())

        assertThat(testKey.xprv).isEqualTo(derivedKey.xprv())
        assertThat(testKey.xpub).isEqualTo(derivedKey.xpub())
    }

    private fun testCaseCurveToEllipticCurveType(curveName: String): EllipticCurveType {
        if (curveName == "secp256k1") {
            return EllipticCurveType.Secp256k1
        }
        if (curveName == "nist256p1") {
            return EllipticCurveType.P256
        }
        if (curveName == "ed25519") {
            return EllipticCurveType.Ed25519
        }
        throw InvalidParameterException("No curve found for $curveName")
    }
}
