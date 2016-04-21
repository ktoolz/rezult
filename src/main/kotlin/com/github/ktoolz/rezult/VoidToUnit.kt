@file:JvmName("ResultToolbox")
package com.github.ktoolz.rezult

import java.util.function.Consumer


fun <T> toUnit(execute: Consumer<T>): Function1<T, Unit> {
    return { t -> execute.accept(t) }
}