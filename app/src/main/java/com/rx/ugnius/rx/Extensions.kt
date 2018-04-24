package com.rx.ugnius.rx

fun <T> List<T?>.secondOrNull(): T? = if (size > 1) get(1) else null