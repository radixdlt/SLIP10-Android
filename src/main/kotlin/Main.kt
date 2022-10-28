import com.radixdlt.slip10.toNoPrefixHexString
import com.radixdlt.slip10.toKey
import com.radixdlt.bip39.mnemonicToEntropy
import com.radixdlt.bip39.model.MnemonicWords
import com.radixdlt.bip39.toSeed
import com.radixdlt.bip39.wordlists.WORDLIST_ENGLISH
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.getCompressedPublicKey
import com.radixdlt.crypto.hash.sha256.extensions.toBytes
import com.radixdlt.extensions.toHexStringNoPrefix
import com.radixdlt.hex.extensions.toNoPrefixHexString

fun main(args: Array<String>) {
    val mnemonic = MnemonicWords("noodle question hungry sail type offer grocery clay nation hello mixture forum")
    val passphrase = "What"

    val entropy = mnemonic.mnemonicToEntropy(WORDLIST_ENGLISH) // use mnemonic.validate(WORDLIST_ENGLISH)
    println("Entropy: ${entropy.toNoPrefixHexString()}")

    val seed = mnemonic.toSeed(passphrase)
    println("Seed: ${seed.toNoPrefixHexString()}")

    val derivedKey = seed.toKey("m", EllipticCurveType.Secp256k1)

    println("PrivateKey: ${derivedKey.keyPair.privateKey.key.toHexStringNoPrefix()}")
    println("PublicKey: ${derivedKey.keyPair.getCompressedPublicKey().toNoPrefixHexString()}")

    println("Chaincode: ${derivedKey.chainCode.toNoPrefixHexString()}")
    println("Fingerprint: ${derivedKey.parentFingerprint.toBytes().toByteArray().toNoPrefixHexString()}")

    val obtainedPub = derivedKey.serialize(true)
    println("xPub: $obtainedPub")

    val obtainedPrv = derivedKey.serialize()
    println("xPriv: $obtainedPrv")
}
