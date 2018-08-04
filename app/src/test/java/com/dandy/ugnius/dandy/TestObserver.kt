package com.dandy.ugnius.dandy

import android.arch.lifecycle.Observer

class TestObserver<T> : Observer<T> {

    var observedValue: T? = null

    override fun onChanged(value: T?) {
        observedValue = value
    }
}