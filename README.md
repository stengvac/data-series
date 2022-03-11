# Data series app
This app consume measured data and then provide average of values in time frames.

## Build & Run

Build `./gradlew build`

Run `./gradlew bootRun` by default app run at port 8080

## Decisions

### Storage
One of requirements was to use in memory storage. 
To fulfill this requirement I have picked concurrent collections from `java.util.concurrent`. 
My guess was, that write/read operations are more frequent then delete operation so I made some 
optimization for those - meaning effective search for data used to provide statistics.

Of course there are multiple ways for implementation - locks, `synchronized` but this approach seamed best for this case.

### Statistics endpoints response
Statistics endpoints has to return list of avg values computed for buckets of 15 min durations. 

There were multiple possibilities how to return data in case, none sample belong into returned bucket.
So in the end I went for returning avg value zero for those cases and include all buckets meaning none are
not omitted, because of missing data. Reasoning is that this will make life simpler for API consumer.

At end of the day it depends on case by case.

# REST API suggestions
`POST /datapoints` with requirements ASIS should 
return `HTTP 2xx` in all cases instead of `HTTP 400` in case data are already present - idempotence.

Taken from wiki `HTTP 400` is used for `The server cannot or will not process the request due to an apparent client error (e.g., malformed request syntax, size too large, invalid request message framing, or deceptive request routing).`
But unique data condition is rather server error so better go for `HTTP 5xx` could be better or as mentioned before one of
`HTTP 2xx` statuses.


About `GET` endpoints those does not follow restful approach. I would rather go for `/devices/{device}/statistics/avg`
