package com.studio.faisal.uangkas.helper

import java.util.*

/**
 * Created by Faisal on 22/04/2019.
 */
object CurrentDate {
    var calendar = Calendar.getInstance()
    @JvmField
    var year = calendar[Calendar.YEAR]
    @JvmField
    var month = calendar[Calendar.MONTH]
    @JvmField
    var day = calendar[Calendar.DAY_OF_MONTH] // current day
}