package com.radixdlt.derivation.model

/**
 * Full list of networks is documented here -> https://github.com/radixdlt/babylon-node/blob/
 * f3e3b262f7b7610dcd3bf42f6e98009c0444c8c4/common/src/main/java/com/radixdlt/networks/Network.java
 */
sealed class NetworkId(val value: Int) {
    object Mainnet : NetworkId(1)
    object Stokenet : NetworkId(2)
    object Aplhanet : NetworkId(10)
    object Betanet : NetworkId(11)
}