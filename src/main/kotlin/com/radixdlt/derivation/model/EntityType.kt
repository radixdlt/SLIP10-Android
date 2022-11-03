package com.radixdlt.derivation.model

sealed class EntityType(val value: Int) {
    object Account : EntityType(525)
    object Identity : EntityType(618)
}
