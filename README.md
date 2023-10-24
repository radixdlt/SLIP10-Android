# SLIP10-Android

This is an implementation of [`SLIP-10` Universal private key derivation from master private key](https://github.com/satoshilabs/slips/blob/master/slip-0010.md)
along with [`BIP-39`](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki). 
Supports both public and private unhardened derivation for Secp256k1 and Nist256p1 and private hardened derivation for Ed25519, Secp256k1 and Nist256p1.

# Usage

### Working with mnemonics
```kotlin
val mnemonic = MnemonicWords("noodle question hungry sail type offer grocery clay nation hello mixture forum")
val passphrase = "What"

// if you need to get the entropy back
val entropy = mnemonic.mnemonicToEntropy(WORDLIST_ENGLISH) // throws if mnemonic is invalid

/*
Included word lists: WORDLIST_CHINESE_SIMPLIFIED, WORDLIST_CHINESE_TRADITIONAL,
WORDLIST_ENGLISH, WORDLIST_FRENCH, WORDLIST_ITALIAN, WORDLIST_JAPANESE, WORDLIST_KOREAN,
WORDLIST_SPANISH
 */

// when you want a new wallet, after generating the entropy you can get the mnemonic
val mnemonic = entropyToMnemonic(entropy, WORDLIST_ENGLISH)

// to validate a mnemonic against a word list
if (!mnemonic.validate(WORDLIST_ENGLISH)) {
    // display error
}

// generate the seed
val seed = mnemonic.toSeed(passphrase)

// use .toHexString() to get a human-readable format
println("Seed: ${seed.toHexString()}")
```

### Derivation

```kotlin

import java.security.spec.EllipticCurve

var path = "m/0H/1H" // use path "m" for master key. H is interchangeable with ' for hardened paths

// to generate the derived key
// other [`EllipticCurveType`] are [`Secp256k1`] and [`P256`] (for Nist256p1/Secp256r1)
val derivedKey = seed.toKey(path, EllipticCurveType.Ed25519)

// derivedKey.keyPair.privateKey.key to get the raw private key
println("PrivateKey: ${derivedKey.keyPair.privateKey.key.toHexString()}")
// derivedKey.keyPair.getCompressedPublicKey() to get the raw public key
println("PublicKey: ${derivedKey.keyPair.getCompressedPublicKey().toHexString()}")

// obtaining extended private and public keys
println("xPriv: ${derivedKey.xprv()}")
println("xPub: ${derivedKey.xpub()}")

// in case you need chain code or fingerprint
println("Chaincode: ${derivedKey.chainCode.toHexString()}")
println("Fingerprint: ${derivedKey.parentFingerprint.toBytes().toByteArray().toHexString()}")

// to load an extended key from xprv/xpub string format:
val xprv =
    XPriv("xprv9wq9mYG394K9ZQ59TPhbtmvnTwyRTsTh4fMJBqdMdFDeC7gMSaDspbtorrCETDDt3a87QEFFfF73qrss8i7Tc5xJJqCopzVZ3q3DmNMpeQA")

// get the [`ExtendedKey`]
val loadedDerivedKey = xprv.toExtendedKey(EllipticCurveType.Ed25519)
```

## License

The SLIP10 Android code is released under the [Apache 2.0 license](./LICENSE). Binaries are licensed under the [Radix Generic EULA](https://www.radixdlt.com/terms/genericEULA).
