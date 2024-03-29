package com.radixdlt.derivation

import com.radixdlt.bip44.BIP44_PREFIX
import com.radixdlt.derivation.model.CoinType
import com.radixdlt.derivation.model.EntityType
import com.radixdlt.derivation.model.KeyType
import com.radixdlt.derivation.model.NetworkId

/**
 * The **default** derivation path used to derive `Account` keys for signing off transactions or for signing authentication, at a certain account index (`ENTITY_INDEX`)
 * and **unique per network** (`NETWORK_ID`) as per [CAP-26][cap26].
 *
 * Note that users can choose to use custom derivation path instead of this default
 * one when deriving keys for accounts.
 *
 * The format is:
 *          `m/44'/1022'/<NETWORK_ID>'/525'/<ENTITY_INDEX>'/<KEY_TYPE>'`
 *
 * Where `'` denotes hardened path, which is **required** as per [SLIP-10][slip10].
 * where `525` is ASCII sum of `"ACCOUNT"`, i.e. `"ACCOUNT".map{ $0.asciiValue! }.reduce(0, +)`
 *
 */
data class AccountHDDerivationPath(
    private val networkId: NetworkId,
    private val accountIndex: Int,
    private val keyType: KeyType
) {
    private val coinType: CoinType = CoinType.RadixDlt
    private val entityType: EntityType = EntityType.Account

    val path: String
        get() = "$BIP44_PREFIX/44'/${coinType.value}'/${networkId.value}'/${entityType.value}'/${accountIndex}'/${keyType.value}'"
}