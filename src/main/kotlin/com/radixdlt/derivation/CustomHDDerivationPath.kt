package com.radixdlt.derivation

import com.radixdlt.bip44.BIP44
import com.radixdlt.bip44.BIP44_PREFIX
import com.radixdlt.derivation.model.CoinType

/**
 * A custom derivation path used to derive keys for whatever purpose. [CAP-26][cap26] states
 * The format is:
 *          `m/44'/1022'`
 * Where `'` denotes hardened path, which is **required** as per [SLIP-10][slip10].
 */
data class CustomHDDerivationPath(
    val bip44: BIP44
) {

    private val coinType: CoinType = CoinType.RadixDlt

    val path: String
        get() = "$BIP44_PREFIX/44'/${coinType.value}'${bip44.customDerivationPath()}"

}
