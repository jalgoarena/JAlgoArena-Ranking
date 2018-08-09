# JAlgoArena Ranking [![Build Status](https://travis-ci.org/jalgoarena/JAlgoArena-Ranking.svg?branch=master)](https://travis-ci.org/jalgoarena/JAlgoArena-Ranking) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Ranking/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Ranking) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-ranking.svg)]()

JAlgoArena Ranking is service dedicated for collecting accepted users submissions with best results and exposing that data together with calculating ranking for all problems as well as for particular problem.

- [Introduction](#introduction)
- [API](#api)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Ranking is responsible for calculating ranking and exposing it via REST API

![Component Diagram](https://github.com/spolnik/JAlgoArena-Ranking/raw/master/design/component_diagram.png)

# REST API

| Endpoint | Description |
| ---- | --------------- |
| [GET /ranking] | Get general ranking list |
| [GET /ranking/:problemId] | Get ranking for particular problem |
| [GET /solved-ratio] | Get all problems solved by users ratio |

## API

#### Get individual user ranking

  _Returns list of users sorted by their score for individual ranking_

|URL|Method|
|---|------|
|_/ranking_|`GET`|

* **Success Response:**

  _Sorted list of users based on their score_

  * **Code:** 200 <br />
    **Content:** `[{"hacker":"mikolaj19","score":78.0,"solvedProblems":["sum-lists","string-rotation","2-sum","word-ladder"],"region":"Kraków","team":"Team B"},{"hacker":"mikolaj16,...},...]`

* **Sample Call:**

  ```bash
  curl http://localhost:5006/ranking
  ```

#### Get problem user ranking

  _Returns list of users sorted by their score for problem ranking_

|URL|Method|
|---|------|
|_/ranking/problem/:problemId_|`GET`|

* **Success Response:**

  _Sorted list of users based on their score_

  * **Code:** 200 <br />
    **Content:** `[{"hacker":"julia73","score":11.0,"elapsedTime":0.008186},{"hacker":"madzia70","score":10.0,",...},...]`

* **Sample Call:**

  ```bash
  curl http://localhost:5006/ranking/problem/fib
  ```
  
#### Get stats of problems solved by users ratio

  _Returns stats of amount of users solutions per problem_

|URL|Method|
|---|------|
|_/solved-ratio_|`GET`|

* **Success Response:**

  _Solved problems ratio_

  * **Code:** 200 <br />
    **Content:** `[{"problemId":"2-sum","solutionsCount":12},{"problemId":"fib","solutionsCount":11},...]`

* **Sample Call:**

  ```bash
  curl http://localhost:5006/solved-ratio
  ```

## Running locally

There are two ways to run it - from sources or from binaries.

### Running from binaries
- go to [releases page](https://github.com/spolnik/JAlgoArena-Ranking/releases) and download last app package (JAlgoArena-Ranking-[version_number].zip)
- after unpacking it, go to folder and run `./run.sh` (to make it runnable, invoke command `chmod +x run.sh`)
- you can modify port in run.sh script, depending on your infrastructure settings. The script itself can be found in here: [run.sh](run.sh)

### Running from sources
- run `git clone https://github.com/spolnik/JAlgoArena-Ranking` to clone locally the sources
- now, you can build project with command `./gradlew clean stage` which will create runnable jar package with app sources. Next, run `java -jar build/libs/jalgoarena-auth-*.jar` which will start application
- there is second way to run app with gradle. Instead of running above, you can just run `./gradlew clean bootRun`

## Notes
- [Travis Builds](https://travis-ci.org/spolnik)

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/JAlgoArena_Logo.png)
