# siketi

**Siketi** means "success" and is a learning project for web services. The project implements a simple task management services (backend).

## Endpoints
It provides the following endpoints:
* POST    /tasks      - creates a new task with name and description on body section
* PUT     /tasks/id   - updates the task with the specified id. Details in the body
* GET     /tasks/id   - retrieves the task with the specified id
* GET     /tasks      - returns all tasks
* DELETE  /tasks/id   - removes the specified task
* DELETE  /tasks      - Wipes out all tasks




## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2019 FIXME
