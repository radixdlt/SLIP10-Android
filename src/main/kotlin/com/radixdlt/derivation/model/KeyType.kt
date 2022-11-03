package com.radixdlt.derivation.model

sealed class KeyType(val value: Int) {
    // Key to be used for signing transactions.
    object SignTransaction : KeyType(1238)

    // Key to be used for signing authentication.
    object SignAuth : KeyType(706)
}