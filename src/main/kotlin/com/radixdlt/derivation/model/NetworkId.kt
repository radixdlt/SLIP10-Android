package com.radixdlt.derivation.model

/**
 * Full list of networks is documented here -> https://github.com/radixdlt/babylon-node/blob/main/common/src/main/java
 * /com/radixdlt/networks/Network.java
 */
enum class NetworkId(val value: Int) {
    Mainnet(1),
    Stokenet(2),
    Alphanet(10),
    Betanet(11),
}