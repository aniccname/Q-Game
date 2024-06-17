## Protocol ##
This document is the specification of the protocol for messages between the client and server, based off of the specifications 
from CS4500 (with some minor alterations to make the communications more useful). 

#### ERRORS ####
Errors are specified with the JSON form of 
`["ERROR", message]` where 
    . message is the contents of the error message. 