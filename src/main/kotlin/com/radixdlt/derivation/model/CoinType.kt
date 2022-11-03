package com.radixdlt.derivation.model

/**
 * Currently we only support Radix coin which is documented here -> https://github.com/satoshilabs/slips/pull/1137
 */
sealed class CoinType(val value: Int) {
    object RadixDlt : CoinType(1022)
}
