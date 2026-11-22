v0.1 Controller

First version of Controller does only have TCP sockets and a very simplified parsing logic.
Server is a Controller itself which handles all the logic, and client is only a testing tool to check the logic via requests, measure RTT and debug the code.

As example we've taken the lessons ones. In TCP example from the lesson the server modifies the sentence to upper case, but firstly we will just give it out to test.

Server-Side:
- Create a new connection point at port 1243 (for all client connections).
- Wait for the connection in an endless loop.
- If the client tries to connect, create a new socket only for this client.
- Handle all the requests from the client.
--- Create two data streams for requests and responses.
--- Wait for the request and read by getting one.
--- Parse it and generate a response (very simplified).
--- Write and send the response to the client.
--- Close the connection with client.

Client-side:
- Try to connect to the server via IP-address and port, creating a new socket for it.
- Create two data streams for requests and responses (separated from server!).
- Write and send a request to the server.
- Wait for the response and read it (give it to the console for debugging purposes).

Connection logic:
main () {
    accept() {
        while connection is open
        handleClient() -> close()
    }
}

Notes:
- This version only parses one line requests and can give out only one line responses, which means, that it is not the real HTTP yet.
- This version uses a one-request-per-connection model, which means, we close the connection after each request and wait for new one.
- For now the error handling is only done via printStackTrace, but later we will implement the proper logging.
- Only one client can properly communicate with the controller as there is no concurrency logic.

Questions:
1. What convention  for multiple line request parsing should we use - \n for new line, special separator or content length (like in HTTP)?
2. Can we simplify HTTP request bodies (f.e. don't use host and user-agent)?
