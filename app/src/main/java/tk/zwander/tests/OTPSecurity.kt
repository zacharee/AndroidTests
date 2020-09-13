package tk.zwander.tests

import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Use this to generate the key to enter the development
 * settings in the "Call and Text on Other Devices" app
 * on Samsung devices.
 *
 * The development settings in this app are protected by
 * a password. Unlike most other Samsung apps like this,
 * the password isn't simply hardcoded. It's still easy
 * to reverse engineer, though.
 *
 * This class is mostly copied from the app itself. The
 * way the password works is pretty weird. First, a
 * random 5-character key is generated. That key is
 * then processed into 6 different 9-10-digit numbers.
 *
 * If the user enters any of those 6 numbers, the
 * development settings will open. Otherwise, a new
 * key is generated.
 *
 * There's a catch, though: each key is only valid
 * in the minute the dialog is opened. Not for a
 * minute after the dialog is open; within the same
 * minute. If you open the dialog at x:yy:30, the
 * password will only be valid for 30 seconds. After that,
 * a new key and password will need to be generated.
 *
 * To enter the development settings, go to the About page
 * and tap 10 times on the app name near the center of the
 * screen.
 */

fun main() {
    //Replace "YOUR_KEY_HERE" with the key
    //displayed in the app.
    generatePasswords("YOUR_KEY_HERE")
}

/**
 * Generate the passwords for a given key.
 * There are 6 passwords that will work for each
 * key. This method will return them all.
 *
 * @param key: the key presented in the password
 * entry dialog.
 *
 * @return the passwords that will work for this
 * key.
 */
fun generatePasswords(key: String): ArrayList<String> {
    val passwords = ArrayList<String>()

    //This 5-0 range is used to generate the 6 passwords,
    //as a sort of "seed."
    for (i in 5 downTo 0) {
        //Process the "seed" minus 1. The actual processed
        //number range is actually based on 4 through -1.
        val processedNumber = processNumber(i - 1)

        //Process the key with the processed seed concatenated
        //to the end of it. This is one of the 6 passwords.
        val processedKey = processKey(key + processedNumber).toString()

        passwords.add(processedKey)
    }

    return passwords
}

/**
 * This is copied from the "Call and Text on Other Devices"
 * app. It's used to compare what the user enters and the
 * processed generated key.
 *
 * It will return "true" if the user input matches one
 * of the 6 possible passwords generated from the key.
 *
 * @param userInput: the password the user entered.
 * @param key: the random key to be processed.
 *
 * @return whether the user's input matches.
 */
fun checkStrings(userInput: String, key: String): Boolean {
    //Check the generatePasswords() method to see
    //how this works.
    for (i in 5 downTo 0) {
        val processedNumber = processNumber(i - 1)
        val processedKey = processKey(key + processedNumber).toString()

        if (userInput.equals(processedKey, ignoreCase = true)) {
            return true
        }
    }

    return false
}

/**
 * This is also copied from the app. It does some sort of
 * weird hashing on a number from 5 down to 0 and returns
 * a representative String.
 *
 * This method is time-based. The value is generated
 * uniquely down to the minute. Each minute, the
 * generated value will change.
 *
 * @param number: the number to process (4 through -1)
 *
 * @return a hash of that number based on the current time.
 */
private fun processNumber(number: Int): String {
    //Retrieve a Calendar instance at the current time
    //based in GMT.
    val instance: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    //The password is a 10-digit string of numbers, with
    //each pair being based on a different calendar unit.
    val format = DecimalFormat("00")

    //Adjust the time based on the provided number.
    //The adjustment will either subtract 0-4 from
    //the minute field, or add 1 to it.
    instance.add(Calendar.MINUTE, number * -1)

    //Build the digit string with some weirdness.
    return format.format(instance.get(Calendar.YEAR) - 2000) +
            format.format(instance.get(Calendar.MONTH) + 1) +
            format.format(instance.get(Calendar.MINUTE)) +
            format.format(instance.get(Calendar.DAY_OF_MONTH)) +
            format.format(instance.get(Calendar.HOUR_OF_DAY))
}

/**
 * Do some weird processing on the provided key.
 *
 * @param key: the key to process.
 *
 * @return the processed result.
 */
private fun processKey(key: String): Int {
    var value = 0
    for (char in key) {
        value += char.toInt() + (value shl 5)
    }
    return if (value < 0) value * -1 else value
}
