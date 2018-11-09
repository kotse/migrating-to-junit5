# Migrating to JUnit 5 - Hands On Lab

## Important

[JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide) open it, check it out if you are wondering how to do something.

To perform a migration of the sample project just follow the step by step instructions. 

## Step by step guide

 
**1. Clone repo**: [https://github.com/kotse/migrating-to-junit5](https://github.com/kotse/migrating-to-junit5)
**2. Add project to you IDE**
**3. Expore**

Explore the project a bit, take a look at the code and the tests, make sure tests run both in IDE and maven (`mvn test`)

**4. Add JUnit 5 dependency**

Change `pom.xml` - add new JUnit Jupiter API, while you are here, also update the surefire-version to the latest one

```xml
<dependency>
   <groupId>org.junit.jupiter</groupId>
   <artifactId>junit-jupiter-api</artifactId>
   <version>5.3.1</version>
   <scope>test</scope>
</dependency>
```

**Write a simple JUnit 5 test**, run test in your IDE again, see that all tests are now run, including the new one. You may notice how tests are grouped in two groups - *JUnit Vintage* and *JUnit Jupiter*.


**5. Run new test with maven**

Execute `mvn test`.

**Observe new test is not being run at all, just the old JUnit 4 tests are visible!**

To fix that, add another test dependency in your pom.xml dependencies - `jupiter-engine`

```xml
<dependency>
   <groupId>org.junit.jupiter</groupId>
   <artifactId>junit-jupiter-engine</artifactId>
   <version>5.3.1</version>
   <scope>test</scope>
</dependency>
```

Execute `mvn test`.

Notice only your new test is run now!

If you have an issue running tests, not seeing any tests or just the old tests, instead of the new one - make sure surefire version is at least 2.22.1 (latest version at the time this text is written).

To show all tests, **add vintage engine dependency in your pom.xml, then run tests again**

```xml
<dependency>
   <groupId>org.junit.vintage</groupId>
   <artifactId>junit-vintage-engine</artifactId>
   <version>5.3.1</version>
   <scope>test</scope>
</dependency>
```

Congratulations! **You have now successfully migrated your maven test build setup to JUnit 5. All tests are run together now - old Junit 4 ones and the new one!**

You can remove `junit` and `jupiter-api` dependencies from you `pom.xml` now, as they are pulled by the `junit-jupiter-engine` and `junit-vintage-engine` and there is no need to declare them twice. This is how your dependencies should look like:

```xml
<dependencies>
   <dependency>
       <groupId>org.junit.jupiter</groupId>
       <artifactId>junit-jupiter-engine</artifactId>
       <version>5.3.1</version>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.junit.vintage</groupId>
       <artifactId>junit-vintage-engine</artifactId>
       <version>5.3.1</version>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.mockito</groupId>
       <artifactId>mockito-core</artifactId>
       <version>${mockito.version}</version>
   </dependency>
</dependencies>
```

**6. Migrate a test to JUnit 5**

Go to StamperRepo test. We will migrate it to JUnit 5, so we can demostrate some of the differences between 4 and 5 versions. There will be many breaking changes!


Delete old Junit 4 imports and add new JUnit 5 ones
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

Even after changin them, this code will no longer compile, as `@Test` will not accept any parameters now.

This is not valid code anymore:
```java
@Test(expected = DuplicateStampException.class)
```

Change the test to use the new JUnit `assertThrows` instead.

After you are finished - remove the `@Disabled` from the test

**7. StamperTest**

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

**8. Ticket Test**

Again migrate like the others.

Note that the tests are very similar. **Perhaps we can parameterize them?**

There is a dependency you need to add to your pom for this to happen. Check out the JUnit5 User Guide to find the dependency and the the `@ParameterizedTest` and other annotations needed.

Try to work out how to use `@MethodSource` or one of the other Sources to inject the paramers in a test.

Here is an example solution:

```java
static Stream<Arguments> ticketsTestDataProvider() {
   return Stream.of(
           Arguments.of("Ticket bought an hour ago is valid", AN_HOUR_AGO, IS_VALID),
           Arguments.of("Ticket bought in the future cannot be valid", FUTURE, !IS_VALID),
           Arguments.of("Ticket bought in more than two years ago is not valid anymore", MORE_THEN_TWO_YEARS, !IS_VALID),
           Arguments.of("Ticket exactly two years ago is still (barely) valid", TWO_YEAR_OLD, IS_VALID)
   );
}

@ParameterizedTest(name = "{0}")
@MethodSource(value = "ticketsTestDataProvider")
void ticketAreNotValid(String label, LocalDateTime time, boolean isValid) {
   Ticket ticket = new Ticket("code", time);

   assertEquals(isValid, !ticket.isNotValid(NOW));
}
```


**9. Slow Tests**

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


**10. Optional**: If you want to continue - migrate slow tests, tag them with the new `@Tag` annotation and configure maven to run all tests with or without the tag
