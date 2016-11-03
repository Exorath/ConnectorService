# ConnectorService
Quickly allows servers to send players to games based on the game type, map and flavor.

##Endpoints

###/servers/find/{gameId} [GET]:
####Gets general information about the running servers of the specified game type
**Arguments**:
- gameId (string): The id of the game

**Response**: 
```json
{"pc": 24, "sc": 3, "osc": 1, "opc": 16}
```
- pc (int): The amount of players currently on the game servers of this gameId
- sc (int): The amount of servers of this game type
- osc(int): The amount of servers of this game type that are open for joining.
- opc(int): The amount of player slots of this game type that are open for joining.

###/servers/{serverUuid} [PUT]:
####Updates the server record.
**body**:
```json
{"gameId": "IW", "socket": "IW-s1.dns.com:25565", "joinable": true, "expiry": 1478210318965, "players": ["6939204d-497f-4094-a7da-1a6346aacd9b"], "pc": 1, "mpc": 16}
```


**Arguments**:
- serverUuid (string): The unique id of this server
- gameId (string): The gameId of the server instance
- socket (string): Socket in address:port format
- joinable (boolean): Whether or not this server can be joined
- mapId (string)[OPTIONAL]: The mapId of the server instance
- flavorId (string)[OPTIONAL]: The flavorId of the server instance
- expiry (long): Time when this server is considered "offline", UNIX millis timestamp.
- players (string array): Array of players uuid's
- pc (int): Amount of players currently on this server
- mpc (int)[OPTIONAL]: Maximum amount of players allowed on this server, defaults to -1 (no cap)
- lobby (boolean)[OPTIONAL]: Whether or not this is a lobby. Defaults to false

**Response**: 
```json
{"success": true}
```
- success (boolean): Whether or not the record was updated successfully 
- err (string)[OPTIONAL]: Error message only responded when the update was not successful.

###/servers/{gameId}/player/{uuid} [POST]:
####Makes the player join an open instance of the game.
If lobby=*true*, the last game of this player will be updated to the provided gameId.

**body**:
```json
{"lobby": true, "mapId": "treeLand", "flavorId": "teams"}
```
**Arguments**:
- gameId (string): The id of the game
- uuid (string): the player's uuid
- lobby (boolean)[OPTIONAL]: Whether or not to join a gameId lobby (In this case the mapId and flavorId will be ignored)
- mapId (string)[OPTIONAL]: The mapId of the server that the player should join
- flavorId (string)[OPTIONAL]: The flavorId is a requirement put on the flavor of the game, this is optional to specify stuff like "teams", "doubles", "solo"...

**Response**: 
```json
{"success": true,"sid": "2f132baf-f714-4a04-b58d-e012ea80a703"}
```
- success (boolean): Whether or not the player will be teleported to a server.
- sid (string)[OPTIONAL]: The server id the player is connecting too.
- err (string)[OPTIONAL]: An error string that describes why the player was not connected to the server Only provided when success=*false*.

###/lastgame/player/{uuid} [GET]:
####Gets the id of the last game this player has played.
**Arguments**:
- uuid (string): The player to fetch the last game type from

**Response**: 
```json
{"gameId":"IW"}
```
- gameId (string)[OPTIONAL]: The id of the last game this player has played, not provided if there was none or an error occured.
- err (string)[OPTIONAL]: Error message when an error occurs.

###/lastgame/player/{uuid} [POST]:
####Makes the player join an open lobby instance of the last lobby the player joined (defaults to an environment defined gameId).
**Arguments**:
- uuid (string): the player's uuid

**Response**: 
```json
{"success": true,"suuid": "2f132baf-f714-4a04-b58d-e012ea80a703"}
```
- success (boolean): Whether or not the player will be teleported to a server.
- suuid (string)[OPTIONAL]: The uniqueId of the server the player is connecting to.
- err (string)[OPTIONAL]: An error string that describes why the player was not connected to the server Only provided when success=*false*.

###/servers/update/ [POST]:
####Clears all offline servers and updates the total player counts
Will be called every few minutes by a timer on Lambda(/Azure functions). 
Will start the purge and player count update.
The execution of the functionality is asynchronous to the call (The response indicates that the removal is scheduled, but not necessarily completed).
**Arguments**:
/
**Response**:
/
