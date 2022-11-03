package com.radixdlt.derivation.model

/**
 * Full list of networks is documented here -> https://github.com/radixdlt/babylon-node/blob/
 * f3e3b262f7b7610dcd3bf42f6e98009c0444c8c4/common/src/main/java/com/radixdlt/networks/Network.java
 */
enum class NetworkId(val value: Int) {
    Mainnet(1),
    Stokenet(2),
    Aplhanet(10),
    Betanet(11),
}