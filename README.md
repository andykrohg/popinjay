# Popinjay
## AI Wings Run Scheduler

This project contains a [Quarkus](https://quarkus.io/) application
with [OptaPlanner](https://www.optaplanner.org/)'s constraint solving Artificial Intelligence (AI)
integrated with a database and exposed through a REST API.

This web application optimizes a wings schedule for students and mentors.
It assigns `Timeslot` and `Mentor` values to `MentorAssignment` instances automatically
by using AI to adhere to hard and soft scheduling constraints, such as:

* *Calendar conflict*: Mentor (or the student presenter) is unavailable for the requested timeslot (Google calendar integration).
* *Mentor conflict*: A mentor cannot have multiple assignments in the same timeslot.
* *Self assignment conflict*: A student cannot be assigned to the panel of their own wings run.
* *Mentor pairing constraint*:  Prefer to pair mentors with their mentees.
* *Max assignments*: Prefer mentors not to have more than [5] assignments per week.
* *Mentor affinity*: Prefer assignments to more actively engaged mentors.

## Google Calendar Integration
The calendar conflict constraint requires read-only access to your Google calendar. The mentors you add as `ProblemFacts` should be named according to their email address. At the moment, `GoogleCalendarIntegration.fetchSchedules()` suffixes the provided username with `@redhat.com`.

Upon starting a solver, the `GoogleCalendarIntegration` class presents a credential to the Google Oauth server, which will open an authorization page in your browser where you'll need to grant access. It expects to find this credential in `src/main/resources/credentials.json`, which can be obtained here: https://console.cloud.google.com/apis/credentials. If you have sufficient privileges for your cloud account, you can utilize a Service Account. I don't, so I created an OAuth Client ID.


## Run the application with live coding

1. Start the application:

    ```
    ./mvnw quarkus:dev
    ```

2. Visit http://localhost:8080 in your browser.
3. Click on the _Solve_ button.

Now try live coding:

1. Make some changes in the source code
2. Refresh your browser (F5).

Those changes are immediately applied.

## Package and run the application

When you're done iterating in `quarkus:dev` mode, run the application as a conventional jar file.

1. Compile it:

    ```
    ./mvnw package
    ```

2. run it:

    ```
    java -jar ./target/popinjay-1.0-SNAPSHOT-runner.jar
    ```

Look at how fast it boots!

## Run a native executable

You can also create a native executable from this application without making any
source code changes. A native executable removes the dependency on the JVM:
everything needed to run the application on the target platform is included in
the executable, allowing the application to run with minimal resource overhead.

Because the quarkus H2 extension does not support compiling the embedded database engine into native images,
you need to run the H2 server locally first:

 1. Download the [H2 engine](http://www.h2database.com/html/download.html) (Platform-independent zip)
 
 2. Unzip it.

 3. Start H2 server with the option `-ifNotExists` (this is not recommended in production but saves you from creating the database manually)

    ```shell script
    cd h2/bin && java -cp h2*.jar org.h2.tools.Server -ifNotExists
    ```

The database connection in configured in the `application.properties` file,
specifically with the `%prod.quarkus.datasource.*` properties.


Compiling a native executable takes a bit of time,
 as GraalVM performs additional steps to remove unnecessary codepaths.
 Use `-Dnative` to compile a native executable:

```
./mvnw package -Dnative
```
After getting a cup of coffee, run this binary directly:

```
./target/popinjay-1.0-SNAPSHOT-runner
```
