# code-generation-spring-configuration

Jackson support module for Binis CodeGen Library.

### Description

Spring Boot Configuration module to usage of objects generated by Binis CodeGen library upon deserialization.

### Web Requests

**code-generation-spring-configuration** and **code-generation-jackson** modules enable the usage of prototypes as web request objects simple and easy like this  

```java
@CodeRequest
public interface TestRequestPrototype {

    String name();
    String value();
    
}
```
and then you can simply use it.
```java
    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result test(@RequestBody TestRequest request){
        ...
    }
```

### Bean Validation

If we add **code-generation-validation** module into the mix and extend the example above with some validations and sanitizations.
```java
@CodeRequest
public interface TestRequestPrototype {

    @ValidateNull
    @SanitizeTrim    
    @ValidateLength(min = 5, minMessage = "Name must be longer than 5 characters!")
    String name();

    @ValidateNull
    @SanitizeTrim
    String value();

    @ValidateLength(3)
    @ValidateRegEx(expression = "\\d+")
    String numbers();

    @JsonIgnore //Note: all other annotations works as well
    Long other();
}
```
upon calling our web method all of the fields validations and sanitizations will be performed automatically. If one or more validation fails, an exception (**ValidationFormException**) is raised.
Inside the exception class you will find list of all the fields which validations failed along with list of all validations that were not satisfied for each field. Here is how the exception looks like
in the evaluator

![evaluator](/images/evaluator.png "IntelliJ Evaluator")

For usage examples please see [https://github.com/binis2/code-generation]    
For full project examples please see [https://github.com/binis2/code-generation-examples] 

### Maven Dependency
```xml
    <dependency>
        <groupId>dev.binis</groupId>
        <artifactId>code-generator-spring-configuration</artifactId>
        <version>1.1.2</version>
    </dependency>
```

### Other modules of the suite

Core - [https://github.com/binis2/code-generation-core]   
Generation Module - [https://github.com/binis2/code-generation]   
Spring Extension - [https://github.com/binis2/code-generation-spring]   
Tests mocking suite - [https://github.com/binis2/code-generation-test]   
Annotation processor - [https://github.com/binis2/code-generation-annotation]   
Validation and Sanitization extension - [https://github.com/binis2/code-generation-validation]    
Jackson support - [https://github.com/binis2/code-generation-jackson]   
Projections support - [https://github.com/binis2/code-generation-projection]   
Hibernate support - [https://github.com/binis2/code-generation-hibernate]      
