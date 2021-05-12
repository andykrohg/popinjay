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

## Use this to schedule *your* wings runs!

1. Clone this repository, and `cd` into it:

    ```
    git clone https://github.com/andykrohg/popinjay.git
    cd popinjay
    ```
2. Create an **Oauth Client** for Red Hat's GSuite:
    1. Visit https://console.cloud.google.com/apis/credentials, and login with your Red Hat google account.
    2. Click the Project dropdown in the top left, and click **NEW PROJECT** in the top-right of the modal that pops up. Name the project anything you like (e.g. "Popinjay-akrohg").
    3. Click the hamburger in the top left and visit **APIs & Services -> Credentials**
    4. Click **CREATE CREDENTIALS** and choose type **Oauth client**
    5. Use **Applictation type: Desktop app**, and use any name you like.
    6. Open the newly created client and click **DOWNLOAD JSON**. Move/rename the resultant file to `popinjay/src/main/resources/credentials.json`
3. Run the app using the maven wrapper:

    ```
    ./mvnw quarkus:dev
    ```

4. Visit http://localhost:8080 in your browser. You'll see a schedule beginning the first Monday following the current date.
5. Modify the week's **Timeslots**, **Mentors**, and **Wings Runs** as you see fit. Be sure to use mentor's **user ID** from [Rover](https://rover.redhat.com/people) so that calendar's are accessed correctly.
6. Click **Solve**. The app will open a new tab requesting write access to your Calendar. This is required for sending meeting invites for the generated schedule.
7. After granting permissions, go back to your other tab. If it displays an error, click **Solve** again. The solver will run for 60 seconds, attempting to build the best-scoring solution.
8. If you're satisfied with the output, click **Send Meeting Invites** and confirm to send out meeting invites for the generated schedule.
