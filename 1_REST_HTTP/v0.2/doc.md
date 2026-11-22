v0.2 Controller

Second version of Controller does have parsing logic for GET and POST requests.
Also a new connection logic is implemented for different protocols (HTTP 1.0 vs 1.1)

Changes:
- handleClient was separated into handleConnection and handleRequests to separate the logic.
- Server is aware of two HTTP versions and handles them correctly.
- Request parsing was implemented (see below).
- Body handling for POST requests was implemented (via content-length).
- Response building was implemented.

handleConnection structure:
- Creates two data streams for input and output.
- Loops calling headerRequest if keepAlive for HTTP/1.1.
- Otherwise (!keepAlive or HTTP/1.0) closes the socket.

handleRequest structure:
- Reads request line (method, path, and version of HTTP), handles bad responses (!3words).
- Reads request headers into ArrayList.
- Determines keepAlive based on version and "connection" header.
- Ready body - ONLY IF POST.
- Routes the request and calls sendResponse() with correct body.
- Returns keepAlive for where to continue the loop in handleConnection or not.

sendResponse structure:
- Defines response body.
- Sets proper values in it and sends it.

static getHeader:
- Returns header value, if the header is present.

Questions:
1. \r\n oder \n for the line end?

Next steps:
1. switch and cases for if-else logic?
1.1. Further optimization?
1.2. Testing the error handling for every request?
2. Internal data structure to represent a system?
3. Client-side Testing & RTT.
4. Statistical validation.
5. Docker + GitHub.
