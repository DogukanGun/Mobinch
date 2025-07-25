package com.dag.mobinchapp.base

import kotlinx.coroutines.flow.Flow

interface BaseUseCase<T,V> {
    fun execute(params:T? = null):Flow<V>
}