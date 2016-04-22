# rezult

[![Kotlin](https://img.shields.io/badge/kotlin-1.0.1-blue.svg)](http://kotlinlang.org) [ ![Download](https://api.bintray.com/packages/ktoolz/maven/rezult/images/download.svg) ](https://bintray.com/ktoolz/maven/rezult/_latestVersion) [![Build Status](https://travis-ci.org/ktoolz/rezult.svg?branch=master)](https://travis-ci.org/ktoolz/rezult)

Just a simple framework for wrapping return types into a single object: `Result`.

## Inspiration

We got inspiration for that framework from:

* the [Try](http://www.javaslang.io/javaslang-docs/#_try) feature from [javaslang](http://www.javaslang.io/),
* the [Result](https://github.com/kittinunf/Result) Kotlin framework produced by [@kittinunf](https://github.com/kittinunf),

## Target

The goal of that framework is to use a single output for each and every method we write: `Result`.

That wrapper will allow to indicate either the response of a method is a success or a failure, and will allow to act on that result.

Also, our main goal for that framework is for it to be the easiest possible to use.

## Download

Add this repository in your Maven pom.xml:

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>central</id>
        <name>bintray</name>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
```

And then this dependency:

```xml
<dependency>
    <groupId>com.github.ktoolz</groupId>
    <artifactId>rezult</artifactId>
    <version>1.2.0</version>
</dependency>
```

## TL;DR

`Result` allows you to wrap any code execution in order to catch the Exceptions you might encounter and retrieve a Failure instead:

### In Kotlin

```kotlin
    // Computing a Result and chaining operations:
    val res = Result.of { operation() }
        .logSuccess { println("It Worked!") }
        .logFailure { println("It Failed!") }
        .validate { this.length > 0 }
        .logFailure { println("Size isn't good.") }
        .withSuccess { register(this) }
        .onFailure { rollback(it) }

    // Checking a Result in your code
    when  {
        res.isSuccess() -> println("Success!")
        res.isFailure() -> println("Failure!")
        res.contains("Hello") -> println("Correct content!")
    }

    // Retrieving the value of a Result
    var value = res or "Default value"
    // You can also write it:
    value = res.getOrElse { "Default value" }
```

### In Java

```java
        // Computing a Result and chaining operations:
        Result<String> res = Result.of(() -> operation())
                                   .logSuccess($ -> System.out.println("It Worked!"))
                                   .logFailure($ -> System.out.println("It Failed!"))
                                   .validate(s -> s.length() > 0)
                                   .logFailure($ -> System.out.println("Size isn't good."))
                                   .withSuccess(s -> register(s))
                                   .onFailure(e -> rollback(e));

        // Checking a Result in your code
        if(res.isSuccess()) {
            System.out.println("Success!");
        } else if(res.isFailure()) {
            System.out.println("Failure!");
        } else if(res.contains("Hello")) {
            System.out.println("Correct content!")
        }

        // Retrieving the value of a Result
        String value = res.or("Default value");
        // You can also write it:
        value = res.getOrElse("Default value");
```

## Examples

Want to see rezult in action? Just have a look at our [Spek tests](https://github.com/ktoolz/rezult/blob/master/src/test/kotlin/com/github/ktoolz/rezult/ResultSpecs.kt) to get examples of all the features included in rezult.

## License

Rezult is released under the [MIT](http://opensource.org/licenses/MIT) license.
>The MIT License (MIT)

>Copyright (c) 2016

>Permission is hereby granted, free of charge, to any person obtaining a copy
>of this software and associated documentation files (the "Software"), to deal
>in the Software without restriction, including without limitation the rights
>to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
>copies of the Software, and to permit persons to whom the Software is
>furnished to do so, subject to the following conditions:

>The above copyright notice and this permission notice shall be included in
>all copies or substantial portions of the Software.

>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
>IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
>FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
>AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
>LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
>OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
>THE SOFTWARE.
