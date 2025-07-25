package com.dag.a1inch

fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> =
    mapNotNull { (k, v) -> v?.let { k to it } }.toMap()