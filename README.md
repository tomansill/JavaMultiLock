# MultiLock

A wrapper library that wraps multiple `Lock`s into one `Lock` for ease of use.

By Tom Ansill

## Motivation

I've been working with locks. 
Sometimes I've found myself working with multiple locks and I've learned that it's not quite easy to handle them properly. 
I wanted a library that allows me to easily handle infintely many locks as needed. 
So I made a library that combines all locks into one lock so it can be handled easily.

## Prerequisites

* Java 8 or better
* Maven
* [My Java Validation library](https://github.com/tomansill/JavaValidation)
* [My AutoLock library](https://github.com/tomansill/AutoLock)

## Download

**No Maven Repository available yet ):**

For now, you need to build and install it on your machine.

```bash
$ git clone https://github.com/tomansill/JavaMultiLock
$ cd JavaMultiLock
$ mvn install
```

Then include the dependency in your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.ansill.lock</groupId>
    <artifactId>MultiLock</artifactId>
    <version>0.1.0</version>
</dependency>
```

## How to use

It's pretty simple, you initialize `MultiLock` with one or more locks. 

```
MultiLock lock = new MultiLock(lock1, lock2, lock3);

try{
    lock.lock();
    
    // Do stuff
    
}finally{
    lock.unlock();
}
```

`MultiLock` implements `AutoLock`'s methods so you can also use locks in Try-with-resources for easy unlocking and cleaning up.

# Known Issues

* None so far but this is an alpha library, so there may be bugs present. Beware!