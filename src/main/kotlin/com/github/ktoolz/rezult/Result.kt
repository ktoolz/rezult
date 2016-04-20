/*
 * Rezult - KToolZ
 *
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.ktoolz.rezult

import com.github.ktoolz.rezult.exceptions.ResultException

// ----------------------------------------------------------------
// Property extensions for lambdas to retrieve results
// ----------------------------------------------------------------

/**
 * Property extension on lambdas with no parameters.
 *
 * Provides the same lambda but returning a [Result] of the original return type.
 */
val <T> (() -> T).lambdaresult: () -> Result<T> get() = { Result.of(this) }

/**
 * Property extension on lambdas with no parameters.
 *
 * Provides a [Result] containing the value returned by the lambda execution.
 */
val <T> (() -> T).result: Result<T> get() = Result.of(this)

/**
 * Extension on lambdas with no parameters.
 *
 * Provides a [Result] containing the value returned by the lambda execution.
 */
fun <T> (() -> T).toResult(): Result<T> = Result.of(this)

/**
 * Property extension on lambdas with one parameter.
 *
 * Provides the same lambda but returning a [Result] of the original return type.
 */
val <T, A> ((A) -> T).lambdaresult: (A) -> Result<T> get() = { a -> Result.of({ this(a) }) }

/**
 * Property extension on lambdas with two parameters.
 *
 * Provides the same lambda but returning a [Result] of the original return type.
 */
val <T, A, B> ((A, B) -> T).result: (A, B) -> Result<T> get() = { a, b -> Result.of({ this(a, b) }) }

/**
 * Property extension on lambdas with three parameters.
 *
 * Provides the same lambda but returning a [Result] of the original return type.
 */
val <T, A, B, C> ((A, B, C) -> T).result: (A, B, C) -> Result<T> get() = { a, b, c -> Result.of({ this(a, b, c) }) }

/**
 * Property extension on lambdas with four parameters.
 *
 * Provides the same lambda but returning a [Result] of the original return type.
 */
val <T, A, B, C, D> ((A, B, C, D) -> T).result: (A, B, C, D) -> Result<T> get() = { a, b, c, d ->
    Result.of({ this(a, b, c, d) })
}

// ----------------------------------------------------------------
// Type extensions for creating results easily
// ----------------------------------------------------------------

/**
 * Extension on any type.
 *
 * Provides a success [Result] out of the type.
 *
 * @return a [Result] object (success) linked to this type.
 */
fun <T> T.toResult() = Result.success(this)

/**
 * Extension for [Exception].
 *
 * Provides a failure [Result] out of the exception.
 *
 * @return a [Result] object (failure) linked to this [Exception].
 */
fun <T : Exception> T.toResult() = Result.failure<Any>(this)

/**
 * Extension on any type.
 *
 * Provides a validate method allowing to validate a value from a lambda, and creates a [Result] out of the validation.
 *
 * @param[errorMessage] an error message to be used in the [Result] if the validation fails.
 * @param[validator] a lambda to be applied on the type and returning a [Boolean] value, validating its content.
 *
 * @return a [Result] object containing the result of the validation.
 */
fun <T> T.validate(errorMessage: String = "Validation error", validator: T.() -> Boolean): Result<T> =
        toResult().validate(errorMessage, validator)

// ----------------------------------------------------------------
// Extensions for manipulating Results on few types
// ----------------------------------------------------------------

/**
 * Extension on boolean [Result].
 *
 * Allows to use the `!` operator on boolean [Result] directly. Will keep the [Result] status (failures won't change).
 *
 * @return a [Result] object containing the opposite [Boolean] (if success), or the same failure [Result].
 */
operator fun Result<Boolean>.not() = onSuccess { Result.success(!it) }

/**
 * Extension on boolean [Result].
 *
 * Allows to check if a boolean [Result] is true or not. If it's true, it'll return the same result. Otherwise it'll return a failure.
 *
 * @return the same [Result] object if the boolean is true, a failure otherwise.
 */
fun Result<Boolean>.isTrue() = validate { this }

/**
 * Extension on [IntRange] for [Result].
 *
 * Allows to check if a [Result] is contained in a particular range using the `in` operator. Will keep the [Result] status (failures won't change).
 *
 * @param[value] the [Result] we'd like to see in the range.
 *
 * @return true if the [Result] value is contained in the range.
 */
operator fun IntRange.contains(value: Result<Int>) =
        value.validate { this@contains.contains(this) }.isSuccess()

// ----------------------------------------------------------------
// Definition of Result and its operations
// ----------------------------------------------------------------

/**
 * A wrapper to any operation call.
 *
 * Allows to check if a particular call is a [Success] or a [Failure], and chain other operations on this [Result].
 *
 * @author jean-marc, aurelie, antoine
 */
sealed class Result<T> {

    // ----------------------------------------------------------------
    // Basic operations
    // ----------------------------------------------------------------

    /**
     * Defines if the [Result] is a [Success].
     *
     * @return true if the [Result] is a [Success].
     */
    abstract fun isSuccess(): Boolean

    /**
     * Opposite of [isSuccess]. Defines if the [Result] is a [Failure].
     *
     * @return true if the [Result] is a [Failure].
     */
    fun isFailure(): Boolean = !isSuccess()

    // ----------------------------------------------------------------
    // Success operations
    // ----------------------------------------------------------------

    /**
     * Executes an operation on a [Success] result, and returns a new [Result] containing that operation's response.
     *
     * @param[operation] an operation to be applied on the [Result] value, returning a new [Result] (that might have another type).
     *
     * @return the [Result] coming from the operation applied to this [Result] value.
     */
    abstract fun <U> onSuccess(operation: (T) -> Result<U>): Result<U>

    /**
     * Executes an operation on a [Success] result, as if we had a `with (success)`, and returns a new [Result] containing that operation's response.
     *
     * @param[operation] an operation to be applied on the [Result] value, returning a new [Result] (that might have another type).
     *
     * @return the [Result] coming from the operation applied to this [Result] value.
     *
     * @see [onSuccess] - it does the same but is maybe a bit easier to use / more clear to use.
     */
    fun <U> withSuccess(operation: T.() -> Result<U>) = onSuccess(operation)

    /**
     * Executes a log operation (returning nothing) on a [Success] result.
     *
     * @param[operation] an operation to be applied on the [Result] value, returning nothing. Basically to be used for logging purpose or something like this.
     *
     * @return the same [Result] you had in a first time.
     */
    fun logSuccess(operation: T.() -> Unit): Result<T> = withSuccess { this@Result.apply { operation() } }

    /**
     * Tries to execute an operation which returns a new [Result] on a [Success] result.
     * It'll return the same first [Result] object if the operation is ok, but it'll return a failure if the operation returned a failed result.
     *
     * @param[operation] an operation to be applied on the [Result] value, returning a new [Result]. The result of this operation will be used in order to check if we should return the same object or not.
     *
     * @return the same [Result] if the operation is successful, or a failure if the operation result isn't a success.
     */
    fun <U> tryWithSuccess(operation: T.() -> Result<U>): Result<T> = withSuccess {
        val result = operation()
        when (result) {
            is Success -> this@Result
            else -> result as Result<T> // It's a failure so we don't care about the cast since it'll never happen
        }
    }

    // ----------------------------------------------------------------
    // Failure operations
    // ----------------------------------------------------------------

    /**
     * Executes an operation on a [Failure] exception, and returns a new [Result] containing the operation's response.
     *
     * The new [Result] must have the same type as the one you specified previously.
     *
     * @param[operation] an operation to be applied on the [Result] exception, retuning a new [Result] (with the same type).
     *
     * @return the [Result] coming from the operation applied to this [Result] exception.
     */
    abstract fun onFailure(operation: (Exception) -> Result<T>): Result<T>

    /**
     * Executes an operation on a [Failure] exception, as if we had a `with (failure)`, and returns a new [Result] containing that operation's response.
     *
     * @param[operation] an operation to be applied on the [Result] exception, returning a new [Result] (with the same type).
     *
     * @return the [Result] coming from the operation applied to this [Result] exception.
     */
    fun withFailure(operation: Exception.() -> Result<T>) = onFailure(operation)

    /**
     * Executes a log operation (returning nothing) on a [Failure] exception.
     *
     * @param[operation] an operation to be applied on the [Result] exception, returning nothing. Basically to be used for logging purpose or something like this.
     *
     * @return the same [Result] you had in a first time.
     */
    fun logFailure(operation: Exception.() -> Unit): Result<T> = withFailure { this@Result.apply { operation() } }

    // ----------------------------------------------------------------
    // Common operations
    // ----------------------------------------------------------------

    /**
     * Validates a [Result] using a simple lambda expression, and turn the [Result] in a failure if the [Result] value isn't matching a particular validation.
     *
     * Cannot be used for *false positive* where you'd like to turn an [Exception] into a [Success].
     *
     * @param[errorMessage] an error message you want to specify if the validation fails - that will be used in the created failure.
     * @param[validator] an operation to be applied on the [Result] value validating its content that will return a [Boolean].
     *
     * @return the same [Result] if the validation was ok, or a new [Result] failure if the validation was not ok.
     */
    fun validate(errorMessage: String = "Validation error", validator: T.() -> Boolean): Result<T> =
            withSuccess {
                if ( this.validator()) this@Result
                else failure(ResultException(errorMessage))
            }

    /**
     * Retrieves a [Result] value (if the [Result] is a success) or will call the provided [backup] operation to retrieve a backup value.
     *
     * @param[backup] an operation allowing to retrieve a backup value in case the [Result] is a [Failure].
     *
     * @return the [Result] value if the result is a [Success], otherwise the result of the [backup] operation.
     */
    abstract fun getOrElse(backup: () -> T): T

    /**
     * Retrieves a [Result] value (if the [Result] is a success) or will return the provided [backupValue].
     *
     * @param[backupValue] a backup value to be returned in case the [Result] is a [Failure].
     *
     * @return the [Result] value if the result is a [Success], otherwise the provided [backupValue].
     *
     * @see [getOrElse] - same operation using a provider.
     */
    fun getOrElse(backupValue: T) = getOrElse({ backupValue })

    /**
     * Calls [getOrElse] in a better looking way.
     *
     * Basically just calls [getOrElse].
     *
     * @param[backup] a backup value to be returned in case the [Result] is a [Failure].
     *
     * @return the [Result] value if the result is a [Success], otherwise the provided [backup].
     *
     * @see [getOrElse] - operation which is actually called.
     */
    infix fun or(backup: T) = getOrElse(backup)

    /**
     * Checks if a [Result] value contains a provided [value]. Will obviously be false if the [Result] is a [Failure].
     *
     * @param[value] the value you'd like to find in the [Result].
     *
     * @return true if the [Result] is a [Success] and actually contains the [value], false otherwise.
     */
    abstract operator infix fun contains(value: T): Boolean

    // ----------------------------------------------------------------
    // Companion object
    // ----------------------------------------------------------------

    /**
     * Just a simple companion object for [Result]
     */
    companion object {

        // ----------------------------------------------------------------
        // Helpers for creating Result objects
        // ----------------------------------------------------------------

        /**
         * Creates a [Success] result from a value.
         *
         * @param[value] the value to be used for creating the [Success] object.
         *
         * @return a [Success] object containing the provided [value].
         */
        fun <T> success(value: T): Result<T> = Success(value)

        /**
         * Creates a [Failure] result from an [Exception].
         *
         * @param[exception] the [Exception] to be used for creating the [Failure] object.
         *
         * @return a [Failure] object containing the provided [exception].
         */
        fun <T> failure(exception: Exception): Result<T> = Failure(exception)

        /**
         * Creates a [Failure] result from a message. It'll internally wrap that message in a [ResultException].
         *
         * @param[message] the failure message to be used for creating the [ResultException].
         *
         * @return a [Failure] object containing a [ResultException] using the provided [message].
         */
        fun <T> failure(message: String): Result<T> = Failure(ResultException(message))

        // ----------------------------------------------------------------
        // Methods for executing operations and creating results
        // ----------------------------------------------------------------

        /**
         * Executes the provided [operation] and computes a [Result] object out of it.
         *
         * It basically catches [Exception] while executing the operation, and returns a [Failure] if there's an [Exception].
         *
         * @param[operation] the operation to be executed and which will lead to a [Result] object creation.
         *
         * @return a [Success] object containing the result of the [operation] if there's no [Exception] raised. Otherwise it'll return a [Failure] containing that [Exception].
         */
        fun <T> of(operation: () -> T): Result<T> {
            try {
                return Success(operation())
            } catch(e: Exception) {
                return Failure(e)
            }
        }

        /**
         * Executes all the provided [actions] and returns the first [Success] result if there's one. Otherwise, it'll just return a [Failure].
         *
         * @param[actions] a list of operations to be executed till one is a [Success].
         *
         * @return the first [Success] coming for the [actions] execution, a [Failure] if none of the [actions] creates a [Success].
         */
        fun <T> chain(vararg actions: () -> Result<T>): Result<T> {
            actions.forEach { action ->
                val result = action()
                if (result.isSuccess()) return result
            }
            return failure("All failed")
        }

    }

    // ----------------------------------------------------------------
    // Implementation of Success object
    // ----------------------------------------------------------------

    /**
     * A wrapper for any successful operation execution, containing the final result of that execution.
     *
     * @property[success] the actual value of the [Result].
     *
     * @see [Result].
     */
    private class Success<T>(val success: T) : Result<T>() {

        /**
         * @see [Result.isSuccess].
         */
        override fun isSuccess() = true

        /**
         * @see [Result.onSuccess].
         */
        override fun <U> onSuccess(operation: (T) -> Result<U>): Result<U> =
                try {
                    operation(success)
                } catch (e: Exception) {
                    failure(e)
                }

        /**
         * @see [Result.onFailure].
         */
        override fun onFailure(operation: (Exception) -> Result<T>): Result<T> = this

        /**
         * @see [Result.getOrElse].
         */
        override fun getOrElse(backup: () -> T): T = success

        /**
         * @see [Result.contains].
         */
        override fun contains(value: T): Boolean = success == value

        /**
         * Returns the value of the success in a formatted [String].
         *
         * @return the value of the [Result] in a formatted [String].
         */
        override fun toString(): String = "Success[$success]"
    }

    // ----------------------------------------------------------------
    // Implementation of Failure object
    // ----------------------------------------------------------------

    /**
     * A wrapper for any failed operation execution, containing the [Exception] which caused the failure of that execution.
     *
     * @property[failure] the [Exception] that caused the failure of the operation execution.
     *
     * @see [Result].
     */
    private class Failure<T>(val failure: Exception) : Result<T>() {

        /**
         * @see [Result.isSuccess].
         */
        override fun isSuccess(): Boolean = false

        /**
         * @see [Result.onSuccess].
         */
        override fun <U> onSuccess(operation: (T) -> Result<U>): Result<U> = Failure(failure)

        /**
         * @see [Result.onFailure].
         */
        override fun onFailure(operation: (Exception) -> Result<T>): Result<T> =
                try {
                    operation(failure)
                } catch (e: Exception) {
                    failure(e)
                }

        /**
         * @see [Result.getOrElse].
         */
        override fun getOrElse(backup: () -> T): T = backup()

        /**
         * @see [Result.contains].
         */
        override fun contains(value: T): Boolean = false

        /**
         * Returns the name of the [Exception] in a formatted [String].
         *
         * @return the name of the [Exception] in a formatted [String].
         */
        override fun toString(): String = "Failure[${failure.javaClass}]"
    }

}
