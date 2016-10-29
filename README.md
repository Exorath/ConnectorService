# ConnectorService
Quickly allows servers to send players to games based on the game type, map and flavor.

##Endpoints

###/servers/{gameId}/ [GET]:
####Gets general information about the running servers of the specified game type
**Arguments**:
- gameId (string): The id of the game
**Response**: {pc: 24, sc: 2}
- pc (int): The amount of players currently playing
- sc (int): The amount of servers of this game type
- err (string)[OPTIONAL]: Error message when the get failed

###/servers/{serverUuid}/ [PUT]:
####Updates the server record.
**Arguments**:
- serverUuid (string): The unique id of this server
- gameId (string): The gameId of the server instance
- mapId (string): The mapId of the server instance
- flavorId (string): The flavorId of the server instance
- ttl (number): Time to live of this record
**Response**: {pc: 24, sc: 2}
- pc (int): The amount of players currently playing
- sc (int): The amount of servers of this game type
- err (string)[OPTIONAL]: Error message when the get failed

###/servers/{gameId}/player/{uuid} [POST]:
####Makes the player join an open instance of the game.
**Arguments**:
- gameId (string): The id of the game
- uuid (string): the player's uuid
- mapId (string)[OPTIONAL]: The mapId of the server that the player should join
- flavorId (string)[OPTIONAL]: The flavorId is a requirement put on the flavor of the game, this is optional to specify stuff like "teams", "doubles", "solo"...

**Response**: {"success": false,"sid": "2f132baf-f714-4a04-b58d-e012ea80a703"}
- success (boolean): Whether or not the player will be teleported to a server.
- sid (string)[OPTIONAL]: The server id the player is connecting too.
- err (string)[OPTIONAL]: An error string that describes why the player was not connected to the server Only provided when success=*false*.

###/servers/update/ [POST]:
####Clears all old servers and updates the total player counts.
Will be called every few minutes by a timer on Lambda(/Azure functions). 
Will start the purge and player count update but not wait for a response + will not be informed of errors.
**Arguments**:
/
**Response**:
/
