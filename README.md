# Q Game
A Quirkle "clone" developed mostly in the Fall 2023 Software Development course.

The "Q" folder was all done as part of the class, and 3 other individuals contributed to the codebase. I contributed along with my partner from November to the end of the class in December. 

The "Q-web-client" folder is a web client which allows for human players to play the game. It is written by me with no partner.

The "docs" folder contains the built website. It's only there due to limitations of github pages. 

## How to play
The web client is hosted using github pages at [https://aniccname.github.io/Q-Game/](https://aniccname.github.io/Q-Game/). 
Since Github Pages uses https, mixed content rules means that communication with the server *must* use wss instead of ws. 
As such, whoever is hosting the server has to supply a certificate when starting the server. The web client has a 
troubleshooting section for connecting to a server with a self-signed certificate. 

## How to host
The Server that is compatible with the WebClient is at Q/WebSocketServer/server. The server goes through 1 or more waiting 
periods. If 4 players join during a waiting period, the game immedietly starts. If a waiting period ends with 2 or more 
players connected, the game starts. Otherwise, if a waiting period ends with less than 2 players, the server proceeds to the next waiting period if one exists, if not, the server closes. 
The arguments are:  
   * The port the server will be hosted on.  
   * The number of waiting periods the server will try before closing.   
   * The length of each waiting period in seconds.   
   * The maximum allotted time per player turn in seconds. The player will be removed from the game if they do not submit their answer in time.   

The server will then try to read from System.in a path and password to a keystore holding a certificate. If the path or password is incorrect, the server will start without wss support. 

## Serving the web client locally
If you do not have (or are unwilling to make) a certificate for wss support, the WebClient must be served locally. To 
do so, clone the repo and nagivate to Q-Web-client/ and run the npm start command to setup a development server (works just 
fine), or run npm run build then serve -s build. This will host the website locally through http, which allows you to bypass
all the extra security. 
