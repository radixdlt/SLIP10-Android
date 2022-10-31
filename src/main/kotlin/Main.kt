import com.radixdlt.slip10.toHexString
import com.radixdlt.slip10.toKey
import com.radixdlt.bip39.mnemonicToEntropy
import com.radixdlt.bip39.model.MnemonicWords
import com.radixdlt.bip39.toSeed
import com.radixdlt.bip39.wordlists.WORDLIST_ENGLISH
import com.radixdlt.crypto.ec.EllipticCurveType
import com.radixdlt.crypto.getCompressedPublicKey
import com.radixdlt.crypto.hash.sha256.extensions.toBytes
import com.radixdlt.extensions.toHexString
import com.radixdlt.hex.extensions.toHexString
import com.radixdlt.slip10.model.XPriv
import com.radixdlt.slip10.toExtendedKey

fun main() {
    val mnemonic = MnemonicWords("noodle question hungry sail type offer grocery clay nation hello mixture forum")
    val passphrase = "What"

    val entropy = mnemonic.mnemonicToEntropy(WORDLIST_ENGLISH) // use mnemonic.validate(WORDLIST_ENGLISH)
    println("Entropy: ${entropy.toHexString()}")

    val seed = mnemonic.toSeed(passphrase)
    println("Seed: ${seed.toHexString()}")

    var derivationPath = "m/0H/1H"
    val derivedKey = seed.toKey(derivationPath, EllipticCurveType.Ed25519)

    println("PrivateKey: ${derivedKey.keyPair.privateKey.key.toHexString()}")
    println("PublicKey: ${derivedKey.keyPair.getCompressedPublicKey().toHexString()}")

    println("Chaincode: ${derivedKey.chainCode.toHexString()}")
    println("Fingerprint: ${derivedKey.parentFingerprint.toBytes().toByteArray().toHexString()}")


    val obtainedPrv = derivedKey.xprv()
    println("xPriv: $obtainedPrv")

    val obtainedPub = derivedKey.xpub()
    println("xPub: $obtainedPub")

    val xprv = XPriv("xprv9wq9mYG394K9ZQ59TPhbtmvnTwyRTsTh4fMJBqdMdFDeC7gMSaDspbtorrCETDDt3a87QEFFfF73qrss8i7Tc5xJJqCopzVZ3q3DmNMpeQA")
    val loadedDerivedKey = xprv.toExtendedKey(EllipticCurveType.Ed25519)

    println("Loaded private key: ${loadedDerivedKey.keyPair.privateKey.key.toHexString()}")
    println("Loaded public key: ${loadedDerivedKey.keyPair.getCompressedPublicKey().toHexString()}")
}
