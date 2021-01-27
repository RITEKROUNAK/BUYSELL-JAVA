package com.panaceasoft.psbuyandsell.utils;

import androidx.lifecycle.LiveData;

/**
 * A LiveData class that has {@code null} value.
 */
public class AbsentLiveData<T extends Objects> extends LiveData {

    @SuppressWarnings("unchecked")
    private AbsentLiveData() {

        postValue(null);

    }

    public static <T> LiveData<T> create() {
        //noinspection unchecked
        return new AbsentLiveData();
    }

}

