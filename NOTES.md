## GitHub Repo Browser 

![](https://github.com/antoniosj/blog-examples/blob/master/github-images/app.png?raw=true)



### Requirements

1. You need to create a firebase account and add `google-services.json` to your project `app/`
2. If you want to run Instrumented tests using Google Test Lab you will need to create a Google Cloud Account (service) and follow these [steps](https://circleci.com/docs/2.0/google-auth/) to authorize the user runs tests on firebase. If you want to know more how to run tests using CircleCI + FTL [here](https://circleci.com/docs/2.0/language-android/#testing-with-firebase-test-lab) is the docs. 

#### Bugs fixed

**Orientation bug:** App was recreating the entire list after a orientation change on List Screen. 

**Intent URL:** App was crashing when the user tried to open the repo website. 

**Layout Cut**: Layout on Detail Screen on landscape orientation was hiding the image and button.

**Wrong info:** Name info displayed on Detail and List screen was wrong.

**Detail Screen bug:** Changing orientation to landscape on Detail Screen was returning to the List Screen because of lifecycle problem.

#### Testing

A few tests were made. 

**Unit tests:** Tested the View Model using **Mockito and JUnit**

**Android UI**: Tested the app using **UIAutomator, Espresso and MockWebserver**. 

*Obs: Tests including api calls and device rotation and they're running using locally and in Firebase Test Lab* 

#### Build and Deploy

Project is on [GitHub](https://github.com/antoniosj/android-releng) and CI chosen was **Circle CI** 

#### Static Analysis

**Lint (debug flavor) was used:** `lint.xml` - reports are under `reports/lint-results-debug.html`. If running locally reports will be under `build/reports/lint-results-debug-xml`

#### Challenges

Some of the things I don't know and learnt (that worth mention) from some place are referenced here:

* CLEARTEXT error running api calls on testing: [Solution](http://www.douevencode.com/articles/2018-07/cleartext-communication-not-permitted/)
* EspressoIdlingResource Idle. Faced some problems with tests: [Solution](https://medium.com/androiddevelopers/android-testing-with-espressos-idling-resources-and-testing-fidelity-8b8647ed57f4)



Everything works fine locally, but in the pipeline lots of problems happen. To name a few:

1 - CI environment sometimes cause tests errors

2 - CircleCI emulator crashing when I needed to run UI tests

3 - Google cloud docs for any other than Jenkins are incomplete. I felt lack of information regarding account permissions.

4 - Sometimes a UI tests from the battery tests were failing because they couldn't find the view element but in general it works.
