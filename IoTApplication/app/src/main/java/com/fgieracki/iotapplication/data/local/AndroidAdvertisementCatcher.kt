package com.fgieracki.iotapplication.data.local

import com.juul.kable.AndroidAdvertisement

object AndroidAdvertisementCatcher {
    private var advertisement: AndroidAdvertisement? = null
    fun getAdvertisement(): AndroidAdvertisement {
        return advertisement!!
    }

    fun setAdvertisement(advertisement: AndroidAdvertisement) {
        this.advertisement = advertisement
    }

    fun destroyAdvertisement(){
        this.advertisement = null
    }
}