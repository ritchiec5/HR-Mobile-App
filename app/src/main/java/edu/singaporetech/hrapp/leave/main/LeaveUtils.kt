@file:JvmName("Utils")
@file:JvmMultifileClass

package edu.singaporetech.hrapp.leave.main

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

/**
 * Class that stores function for leaves
 */
class LeaveUtils private constructor() {

    companion object {

        /**
         * Function that combines 2 date strings together
         * @param from
         * @param to
         * @return String
         */
        @JvmStatic
        fun datesApplied(from: String, to: String) : String {
            return if (from == to) {
                from
            } else {
                "$from - $to"
            }
        }

        /**
         * Function that Calculates day period between date range
         * @param from
         * @param to
         * @param dayType
         * @return Double
         */
        @JvmStatic
        fun getDays(from: String, to: String, dayType: String) : Double {

            var period : Int

            val slicedFromDate = from.replace(".", "")
            val slicedToDate = to.replace(".", "")
            val fromDate = LocalDate.parse(slicedFromDate, DateTimeFormatter.ofPattern("ddMMyyyy"))
            val toDate = LocalDate.parse(slicedToDate, DateTimeFormatter.ofPattern("ddMMyyyy"))

            period = Period.between(fromDate, toDate).days

            if (period == 0 && dayType == "FULL")
                return 1.0
            else if (period == 0 && dayType != "FULL")
                return 0.5
            else if (period != 0 && dayType != "FULL")
                return 0.5 + period
            else
                return 1.0 * (period+1)
        }

        /**
         * Function Calculate the full days rounded up from a date range
         * @param from
         * @param to
         * @param dayType
         * @return Int
         */
        @JvmStatic
        fun getFullDays(from: String, to: String, dayType: String) : Int {

            var period : Int

            val slicedFromDate = from.replace(".", "")
            val slicedToDate = to.replace(".", "")
            val fromDate = LocalDate.parse(slicedFromDate, DateTimeFormatter.ofPattern("ddMMyyyy"))
            val toDate = LocalDate.parse(slicedToDate, DateTimeFormatter.ofPattern("ddMMyyyy"))

            period = Period.between(fromDate, toDate).days

            return if (period == 0)
                1
            else
                1 * (period+1)
        }
    }
}