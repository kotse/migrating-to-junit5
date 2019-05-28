# Migrating to JUnit 5 - Hands On Lab

## Important

[JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide) open it, check it out if you are wondering how to do something.

To perform a migration of the sample project just follow the step by step instructions. 

## Step by step guide

Please follow the guide carefully, there are a few traps "hidden"!
 
**1. Clone repo**: [https://github.com/kotse/migrating-to-junit5](https://github.com/kotse/migrating-to-junit5).  
**2. Add project to you IDE**  
**3. Explore**

Explore the project a bit, take a look at the code and the tests, make sure tests run both in IDE and maven (`mvn test`)

Some interesting things to note:
 - it is an e-ticketing system of some kind, there are tickets that are being "stamped" and stamps are stored
 - there are some "slower" integration tests in a separate package (that do not test anything, but just sleep for a few seconds)
 - there are 3 test files testing the different classes - `StamperRepoTest`, `StamperTest` and `TicketTest`

**4. Add JUnit 5 dependency**

Change `pom.xml` - add new JUnit Jupiter API, while you are here, also update the surefire-version to the latest one (2.22.2 at the time of this writing)

```xml
<dependency>
   <groupId>org.junit.jupiter</groupId>
   <artifactId>junit-jupiter-api</artifactId>
   <version>${junit5-version}</version>
   <scope>test</scope>
</dependency>
```

```xml
<maven-surefire-version>2.22.2</maven-surefire-version>
```

**5. Our first JUnit 5 test**

Copy-paste this simple test into your project:

```java
package bgjug.hol.junit5.migration.eticket;

import org.junit.jupiter.api.Test;

class OurFirstJunit5Test {

    @Test
    void aNewTest() {

    }
}
```
Run all tests in your IDE again, see that all tests are now run, including the new one. If you ran them in IDEA, you may notice how tests are grouped in two groups - *JUnit Vintage* and *JUnit Jupiter*.


**6. Run new test with maven**

Execute `mvn test`.

**Observe new test is not being run at all, just the old JUnit 4 tests are visible!**

To fix that, add another test dependency in your pom.xml dependencies - `jupiter-engine`

```xml
<dependency>
   <groupId>org.junit.jupiter</groupId>
   <artifactId>junit-jupiter-engine</artifactId>
   <version>${junit5-version}</version>
   <scope>test</scope>
</dependency>
```

Execute `mvn test`.

**Notice only your new test is run now!**

If you have an issue running tests, not seeing any tests or just the old tests, instead of the new one - make sure surefire version is at least 2.22.1 (you should have changed it to 2.22.2 earlier).

To show all tests, **add vintage engine dependency in your pom.xml, then run tests again**

```xml
<dependency>
   <groupId>org.junit.vintage</groupId>
   <artifactId>junit-vintage-engine</artifactId>
   <version>${junit5-version}</version>
   <scope>test</scope>
</dependency>
```

Congratulations! 

**You have now successfully migrated your maven test build setup to JUnit 5. All tests are run together now - old Junit 4 ones and the new one!**

**7. Migrate a test to JUnit 5**

Go to StamperRepo test. We will migrate it to JUnit 5, so we can demostrate some of the differences between 4 and 5 versions. There will be many breaking changes!


Delete old Junit 4 imports and add new JUnit 5 ones. 

*Careful to delete all old (JUnit 4) imports. If you leave something, the mix of old and new JUnit leads to unpredictable issues*


```java
import org.junit.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
```
Should be:

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
```

Note the changed names for `@Before`, `@After`, `@BeforeClass`, `@AfterClass` and `@Disabled`

Even after changing them, this code will no longer compile, as `@Test` will not accept any parameters now.

This is not valid code anymore:
```java
@Test(expected = DuplicateStampException.class)
```

Change the test to use the new JUnit `assertThrows` instead. [Here is a link to JUnit 5 Javadoc for all new Assertions](https://junit.org/junit5/docs/current/api/org/junit/jupiter/api/Assertions.html)

After you are finished - remove the `@Disabled` from the test.

Run all tests in you IDE or Maven, check they are all still being run.

**8. StamperTest**

Migrate imports like StamperRepo.

Note that **you will need another dependency in your `pom.xml` to get the MockitoExtension that is replacing the MockitoRunner**:

```xml
<dependency>
   <groupId>org.mockito</groupId>
   <artifactId>mockito-junit-jupiter</artifactId>
   <version>${mockito.version}</version>
   <scope>test</scope>
</dependency>
```

This is how it should look like in the end:

```java
@ExtendWith(MockitoExtension.class)
class StamperTest {
```

Experiment with `@Nested` and group validTicket test cases together, place common setup in the `@BeforeEach`.

```java
@Nested
class whenValidTicket {

   @BeforeEach
   void setUp() {
      ...
   }

   @Test
   void shouldStampIt() {
       ...
   }

   @Test
   void shouldStoreIt() {
       ...
   }
}
```

Run the tests in you IDE and observe how tests are grouped.

While it is not a big change in a simple tests like this, this is a powerful feature that may simplify your test setup a lot.

**9. Ticket Test**

Again migrate like the others.

Note some code stopped compiling! There is a breaking change between JUnit 4 and 5. It is possible to have an optional parameter to each assertion method that will display a message when assertion fails. You need to move those as the last parameter of the method instead of the first to make it compile.

Note that the tests are very similar. **Perhaps we can parameterize them?**

Before we change existing ones, lets make a new simpler **ParameterizedTest** first.

**9.1 Test dependency**

There is a dependency you need to add to your pom for this to happen. Check out the JUnit5 User Guide to find the dependency and the the `@ParameterizedTest` and other annotations needed.

**9.2 Write a test for valid and invalid ticket codes**

There is a `TODO` left in the `TicketTest` - a few test cases we have missed to implement!
Let's try to make a test for them.

For that you will need two things:
 - a "test template" method that will receive valid or invalid ticket codes as a parameter
 - a place where we get those (string values) - one of the [Sources of Arguments](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources). Can you guess which one?

 You can start with two tests, one for valid and one for invalid Ticket codes. You just want to make sure a Ticket can be constructed.

 You can use `Assertions.assertThrows` and `Assertions.doesNotThrow()` methods for this purpose.

**9.3 Make one parameterized test for all valid tickets and another for all invalid ones**

For that you will again need two things:
 - a "test template" method that will receive valid or invalid ticket parameters
 - another (static) method, that will provide those parameters and will be called via `@MethodSource`

**9.4 Combine valid and invalid ticket tests in one**

You will need to modify the test template method so it accepts another boolean parameter.

You will also need a new method to provide `Arguments` objects instead of just `LocalDateTime` ones. You can use one of `Arguments` [factory methods to create those](https://junit.org/junit5/docs/current/api/org/junit/jupiter/params/provider/Arguments.html).

**9.5 Example solution**

```java
static Stream<Arguments> ticketsTestDataProvider() {
   return Stream.of(
           Arguments.of("Ticket bought an hour ago is valid", AN_HOUR_AGO, IS_VALID),
           ...
   );
}

@ParameterizedTest(name = "{0}")
@MethodSource(value = "ticketsTestDataProvider")
void ticketAreNotValid(String label, LocalDateTime time, boolean isValid) {
   ...
}
```

Note the parameters passed to the `@ParameterizedTest` annotation. You can customize the name.

**10. Slow Tests**

Migrate the "slow tests" as well.

Integration tests are becoming very slow - simulate this by increasing the Thread.sleep timeout in both `SlowIntegrationTest` and `AnotherSlowIntegrationTest` to 12 seconds.

Notice that when you run it with maven (`maven test`), **tests will run one by one and build will complete in 25-26 seconds, even if you have parallel test execution configured in your pom.xml**:

```xml
<parallel>classes</parallel>
<threadCount>2</threadCount>
```

**Change this to this configuration to enable parallel execution again, as the other option is not yet supported from the JUnit 5 Platform:**

```xml
<forkCount>2</forkCount>
<reuseForks>false</reuseForks>
```

If you run maven build again, you will see the slow tests being run in **parallel** and build will be twice as fast.

**11. Optional**: If you want to continue - migrate slow tests, tag them with the new `@Tag` annotation and configure maven to run all tests with or without the tag
