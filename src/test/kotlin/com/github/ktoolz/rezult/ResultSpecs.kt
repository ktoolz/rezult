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
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek

@Suppress("DIVISION_BY_ZERO")
/**
 * Spek tests for [Result].
 *
 * Contains all specifications / Unit tests.
 *
 * @author jean-marc, aurelie, antoine
 */
class ResultSpecs : Spek() { init {

    given("A Success result created by hand") {
        val message = "Never gonna give you up!"
        val success = Result.success(message)
        on("Checking if it actually is a success") {
            val checkSuccess = success.isSuccess()
            it("should return true and assert it is a success") {
                assertThat(checkSuccess).isTrue()
            }
        }
        on("Checking if it is a failure") {
            val checkFailure = success.isFailure()
            it("should return false and assert it isn't a failure") {
                assertThat(checkFailure).isFalse()
            }
        }
        on("Calling the toString method") {
            val stringSuccess = success.toString()
            it("should return a formatted String containing Success and the value in braces") {
                assertThat(stringSuccess).isEqualTo("Success[$message]")
            }
        }
        on("Checking if it contains the value we provided") {
            val containsSuccess = success.contains(message)
            it("should return true and assert the contained value is still the same") {
                assertThat(containsSuccess).isTrue()
            }
        }
        on("Checking if it contains the value we provided using infix operator") {
            val containsSuccess = success contains message
            it("should return true and assert the contained value is still the same") {
                assertThat(containsSuccess).isTrue()
            }
        }
        on("Trying to retrieve the value it contains") {
            val value = success.getOrElse("backup")
            it("should return the actual provided value and not the backup value we specify") {
                assertThat(value).isEqualTo(message)
            }
        }
        on("Trying to retrieve the value it contains using a provider of backup") {
            val value = success.getOrElse { "backup" }
            it("should return the actual provided value and not the backup value we provide") {
                assertThat(value).isEqualTo(message)
            }
        }
        on("Trying to retrieve the value it contains using infix operator") {
            val value = success or "backup"
            it("should return the actual provided value and not the backup value we specify") {
                assertThat(value).isEqualTo(message)
            }
        }
        on("Applying an uppercase operation on the success") {
            val result = success.onSuccess { Result.success(it.toUpperCase()) }
            val resultValue = result.getOrElse("backup")
            it("should return a new result instance containing the previous value in upper case") {
                assertThat(result).isNotEqualTo(success)
                assertThat(resultValue).isEqualTo(message.toUpperCase())
            }
        }
        on("Calling a log operation on the success") {
            var called = false
            val logResult = success.logSuccess { called = true }
            it("should actually call the operation we provide, and return the exact same result we had") {
                assertThat(called).isTrue()
                assertThat(logResult).isEqualTo(success)
            }
        }
        on("Calling an uppercase operation with the success") {
            val result = success.withSuccess { Result.success(toUpperCase()) }
            val resultValue = result.getOrElse("backup")
            it("should return a new result instance containing the previous value in upper case") {
                assertThat(result).isNotEqualTo(success)
                assertThat(resultValue).isEqualTo(message.toUpperCase())
            }
        }
        on("Trying to apply an operation onFailure") {
            val result = success.onFailure { Result.failure(it) }
            it("should return the exact same Result we already had, not modifying anything") {
                assertThat(result).isEqualTo(success)
            }
        }
        on("Trying to apply an operation with the failure") {
            val result = success.withFailure { Result.failure(this) }
            it("should return the exact same Result we already had, not modifying anything") {
                assertThat(result).isEqualTo(success)
                assertThat(result.isSuccess()).isTrue()
            }
        }
        on("Trying to call a log operation on the failure") {
            var called = false
            val logResult = success.logFailure { called = true }
            it("shouldn't call the operation since it's not a success, and return the exact same result we already had") {
                assertThat(called).isFalse()
                assertThat(logResult).isEqualTo(success)
            }
        }
        on("Running a successful validation on the value") {
            val validation = success.validate { this.contentEquals(message) }
            it("should return the exact same object since the validation is ok") {
                assertThat(validation).isEqualTo(success)
            }
        }
        on("Running an unsuccessful validation on the value") {
            val validation = success.validate { !this.contentEquals(message) }
            val validationFailure = validation.isFailure()
            val stringValidation = validation.toString()
            it("should return a new failure object linked to a ResultException saying the validation failed") {
                assertThat(validation).isNotEqualTo(success)
                assertThat(validationFailure).isTrue()
                assertThat(stringValidation).isEqualTo("Failure[${ResultException::class.java}]")
            }
        }
        on ("Trying a successful operation on the result") {
            val trySuccess = success.tryWithSuccess { (this.length > 0).toResult() }
            it("should return the exact same result object we had in a first time") {
                assertThat(trySuccess).isEqualTo(success)
            }
        }
        on ("Trying an unsuccessful operation on the result") {
            val trySuccess = success.tryWithSuccess { (this.length < 0).toResult().validate { this } }
            val isFailure = trySuccess.isFailure()
            it("should return a new failure object") {
                assertThat(trySuccess).isNotEqualTo(success)
                assertThat(isFailure).isTrue()
            }
        }
    }

    given("A Failure result created by hand from a message") {
        val message = "Never gonna let you down!"
        val failure = Result.failure<String>(message)
        on("Checking if it actually is a failure") {
            val checkFailure = failure.isFailure()
            it("should return true and assert it is a failure") {
                assertThat(checkFailure).isTrue()
            }
        }
        on("Checking if it is a success") {
            val checkSuccess = failure.isSuccess()
            it("should return false and assert it isn't a success") {
                assertThat(checkSuccess).isFalse()
            }
        }
        on("Calling the toString method") {
            val stringFailure = failure.toString()
            it("should return a formatted String containing Failure and the class name of the exception in braces") {
                assertThat(stringFailure).isEqualTo("Failure[${ResultException::class.java}]")
            }
        }
        on("Checking if it contains a value") {
            val containsFailure = failure.contains(message)
            it("should return false (whatever we ask) since it's a failure and doesn't contain any value") {
                assertThat(containsFailure).isFalse()
            }
        }
        on("Checking if it contains a value using infix operator") {
            val containsFailure = failure contains message
            it("should return false (whatever we ask) since it's a failure and doesn't contain any value") {
                assertThat(containsFailure).isFalse()
            }
        }
        val backup = "backup"
        on("Trying to retrieve the value it contains") {
            val value = failure.getOrElse(backup)
            it("should return the backup value that we provide since there's no value") {
                assertThat(value).isEqualTo(backup)
            }
        }
        on("Trying to retrieve the value it contains using a provider of backup") {
            val value = failure.getOrElse { backup }
            it("should return the backup value that we provide since there's no value") {
                assertThat(value).isEqualTo(backup)
            }
        }
        on("Trying to retrieve the value it contains using infix operator") {
            val value = failure or backup
            it("should return the backup provided value since there's no value in the result") {
                assertThat(value).isEqualTo(backup)
            }
        }
        on("Trying to apply an uppercase operation on the success") {
            val result = failure.onSuccess { Result.success("Should fail") }
            val resultValue = result.getOrElse(backup)
            it("should return a new failure object but with the same Exception") {
                assertThat(result).isNotEqualTo(failure)
                assertThat(result).isEqualToComparingFieldByField(failure)
                assertThat(resultValue).isEqualTo(backup)
            }
        }
        on("Trying to call a log operation on the success") {
            var called = false
            val logResult = failure.logSuccess { called = true }
            it("should actually not call the operation we provide, and return a new failure object but with the same Exception") {
                assertThat(called).isFalse()
                assertThat(logResult).isNotEqualTo(failure)
                assertThat(logResult).isEqualToComparingFieldByField(failure)
            }
        }
        on("Trying to call an uppercase operation with the success") {
            val result = failure.withSuccess { Result.success("Should fail") }
            val resultValue = result.getOrElse(backup)
            it("should return a new failure object but with the same Exception") {
                assertThat(result).isNotEqualTo(failure)
                assertThat(result).isEqualToComparingFieldByField(failure)
                assertThat(resultValue).isEqualTo(backup)
            }
        }
        on("Applying an operation on failure") {
            val result = failure.onFailure { Result.success(it.message ?: backup) }
            val resultValue = result.getOrElse(backup)
            it("should return a new success result containing the Exception message") {
                assertThat(result).isNotEqualTo(failure)
                assertThat(resultValue).isEqualTo(message)
            }
        }
        on("Applying an operation with the failure") {
            val result = failure.withFailure { Result.success(message) }
            val resultValue = result.getOrElse(backup)
            it("should return a new success result containing the Exception message") {
                assertThat(result).isNotEqualTo(failure)
                assertThat(result.isSuccess()).isTrue()
                assertThat(resultValue).isEqualTo(message)
            }
        }
        on("Calling a log operation on the failure") {
            var called = false
            val logResult = failure.logFailure { called = true }
            it("should call the operation, and return the exact same failure we already had") {
                assertThat(called).isTrue()
                assertThat(logResult).isEqualTo(failure)
            }
        }
        on("Running a successful validation on the failure") {
            val validation = failure.validate { true }
            it("should return a new failure object but with the same Exception, since validations are not there for creating false-positives") {
                assertThat(validation).isNotEqualTo(failure)
                assertThat(validation).isEqualToComparingFieldByField(failure)
            }
        }
        on("Running an unsuccessful validation on the failure") {
            val validation = failure.validate { false }
            it("should return a new failure object but with the same Exception, since validations are not there for creating false-positives") {
                assertThat(validation).isNotEqualTo(failure)
                assertThat(validation).isEqualToComparingFieldByField(failure)
            }
        }
    }

    given("A result computed directly by the framework on a successful operation") {
        fun successful() = "Never gonna run around!"
        val result = Result.of { successful() }
        on("Checking if it is a success") {
            val checkResult = result.isSuccess()
            it("should return true and assert the result is a success") {
                assertThat(checkResult).isTrue()
            }
        }
    }

    given("A result computed directly by the framework on a failed operation") {
        fun failed(i: Int) = i / 0
        val result = Result.of { failed(5) }
        on("Checking if it is a success") {
            val checkResult = result.isSuccess()
            it("should return false and assert the result is a failure") {
                assertThat(checkResult).isFalse()
            }
        }
        on("Checking the cause of the failure") {
            var exception = Exception()
            result.logFailure { exception = this }
            it("should assert the Exception is a ") {
                assertThat(exception).isInstanceOf(ArithmeticException::class.java)
            }
        }
    }

    given("A result computed by a chain operations") {
        val backup = "backup"
        fun failed() = Result.of { "${1 / 0}" }
        fun success1() = Result.of { "NEVER" }
        fun success2() = Result.of { "GONNA" }
        fun success3() = Result.of { "GIVE" }
        fun success4() = Result.of { "YOU" }
        fun success5() = Result.of { "UP" }
        on("Chaining only successful operations") {
            val result = Result.chain(::success1, ::success2, ::success3, ::success4, ::success5)
            val valueResult = result or backup
            it("should return only the first successful result, aka success1 in our test") {
                assertThat(result).isEqualToComparingFieldByField(success1())
                assertThat(valueResult).isEqualTo("NEVER")
            }
        }
        on("Chaining only failed operations") {
            val result = Result.chain(::failed, ::failed, ::failed, ::failed)
            val resultFailure = result.isFailure()
            var exception = Exception()
            result.logFailure { exception = this }
            it("should return a new failure with ResultException") {
                assertThat(resultFailure).isTrue()
                assertThat(exception).isInstanceOf(ResultException::class.java)
            }
        }
        on("Chaining failed operations then a successful one") {
            val result = Result.chain(::failed, ::failed, ::failed, ::success1, ::failed)
            val valueResult = result or backup
            it("should return the successful result") {
                assertThat(result).isEqualToComparingFieldByField(success1())
                assertThat(valueResult).isEqualTo("NEVER")
            }
        }
        on("Chaining operations after a successful one") {
            var called = false
            fun failed2() = Result.of {
                called = true
                "${1 / 0}"
            }

            fun success2() = Result.of {
                called = true
                "GGWP for the Rick Roll"
            }

            val result = Result.chain(::success1, ::success2, ::failed2)
            it("should return the first success and shouldn't call then next operations") {
                assertThat(result).isEqualToComparingFieldByField(success1())
                assertThat(called).isFalse()
            }
        }
    }

    given("A result of an integer") {
        val result = Result.of { 10 }
        on("Checking if the successful result is part of a range actually containing it") {
            val contained = result in (0..20)
            it("should return true and assert that the result is contained in the range") {
                assertThat(contained).isTrue()
            }
        }
        on("Checking if the successful result is part of a range actually not containing it") {
            val contained = result in (15..20)
            it ("should return false and assert that the result isn't contained in the range") {
                assertThat(contained).isFalse()
            }
        }
        val failed = Result.of { 5 / 0 }
        on("Checking if the failed result is part of a range") {
            val contained = failed in (0..20)
            it("should return false since the result is a failure") {
                assertThat(contained).isFalse()
            }
        }
    }

    given("A result of a Boolean") {
        val result = Result.of { true }
        on("Checking the negation of the result") {
            val negation = !result
            val negationSuccess = negation.isSuccess()
            val valueNegation = negation or true
            it("should return a Success result, containing the opposite boolean value") {
                assertThat(negationSuccess).isTrue()
                assertThat(valueNegation).isFalse()
            }
        }
        val failed: Result<Boolean> = Result.failure("NOPE")
        on("Checking the negation of a failed result") {
            val negation = !failed
            val negationFailure = negation.isFailure()
            it("should return a failure result, since we already had a failure") {
                assertThat(negationFailure).isTrue()
            }
        }
        on("Checking if the true boolean result is true") {
            val trueCheck = result.isTrue()
            it("should return the exact same result we already have") {
                assertThat(result).isEqualTo(trueCheck)
            }
        }
        val falseResult = false.toResult()
        on("Checking if a false boolean result is true") {
            val falseCheck = falseResult.isTrue()
            val isFailure = falseCheck.isFailure()
            it("should return a failure object") {
                assertThat(falseCheck).isNotEqualTo(result)
                assertThat(isFailure).isTrue()
            }
        }
    }

    given("An exception object, whatever it is") {
        val exception = Exception("HELLO")
        on("Calling the Result builder on it") {
            val result = exception.toResult()
            val resultFailure = result.isFailure()
            var e = Exception()
            result.logFailure { e = this }
            it("should create a failure result containing that exception") {
                assertThat(resultFailure).isTrue()
                assertThat(e).isEqualTo(exception)
            }
        }
    }

    given("An object, whatever it is except an exception") {
        val obj = "FROM THE OTHER SIDE"
        on ("Calling the Result builder on it") {
            val result = obj.toResult()
            val resultSuccess = result.isSuccess()
            val value = result or ""
            it("should create a success object containing the value of the first object") {
                assertThat(resultSuccess).isTrue()
                assertThat(value).isEqualTo(obj)
            }
        }
        on("Calling a successful validation on that object") {
            val result = obj.validate { true }
            val resultSuccess = result.isSuccess()
            val value = result or ""
            it("should create a result success object containing the value of the first object") {
                assertThat(resultSuccess).isTrue()
                assertThat(value).isEqualTo(obj)
            }
        }
        on("Calling a failed validation on that object") {
            val result = obj.validate { false }
            val resultFailure = result.isFailure()
            var exception = Exception()
            result.logFailure { exception = this }
            it("should create a failure result containing a ResultException") {
                assertThat(resultFailure).isTrue()
                assertThat(exception).isInstanceOf(ResultException::class.java)
            }
        }
    }

}
}

