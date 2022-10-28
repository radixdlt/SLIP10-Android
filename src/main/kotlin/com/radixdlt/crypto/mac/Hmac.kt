package com.radixdlt.crypto.mac

import com.radixdlt.crypto.hash.DigestParams
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Hmac {
    companion object {
        private lateinit var mac: Mac

        fun init(key: ByteArray, digestParams: DigestParams = DigestParams.Sha512): Companion {
            val version = digestParams.toHmacVersion()
            mac = Mac.getInstance(version)
            val keySpec = SecretKeySpec(key, version)
            mac.init(keySpec)
            return this
        }

        fun generate(data: ByteArray): ByteArray =
            mac.doFinal(data)

        private fun DigestParams.toHmacVersion() = when(this) {
            DigestParams.Sha512 -> "HmacSHA512"
            DigestParams.Sha256 -> "HmacSHA256"
        }
    }
}
