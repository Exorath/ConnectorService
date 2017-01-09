# ConnectorService
Quickly allows servers to send players to games based on the game type, map and flavor.

##Endpoints

###/info?gameId=asd&flavorId=teams [GET]:
####Gets general information about the running servers of the specified game type
If the information is too outdated, this query will go over all online servers in the database to refetch the data.

**Arguments**:
- gameId (string) [OPTIONAL]: Filter on gameId
- mapId (string)[OPTIONAL]: Filter  on mapId
- flavorId (string)[OPTIONAL]: Filter on flavorId
- minLastUpdate (num)[OPTIONAL]: Requires the data to be at least younger then this time (in UNIX millis), if the current DB data is older, data will be re-fetched. NOT USED FOR NOW.

**Response**: 
```json
{"pc": 24, "sc": 3, "osc": 1, "opc": 16, "lastUpdate": 12342352345}
```
- pc (num): The amount of players currently on the game servers of this gameId
- sc (num): The amount of servers of this game type
- osc(num): The amount of servers of this game type that are open for joining.
- opc(num): The amount of player slots of this game type that are open for joining.
- lastUpdate (num): Last time this record was updated, in UNIX notation

An empty response will be given when there's no info yet.

###/servers/{serverId} [PUT]:
####Updates the server record.
**body**:
```json
{"gameId": "IW", "socket": "IW-s1.dns.com:25565", "joinable": true, "expiry": 1478210318965, "players": ["6939204d-497f-4094-a7da-1a6346aacd9b"], "pc": 1, "mpc": 16}
```


**Arguments**:
- serverId (string): The unique id of this server
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

###/join/{uuid} [PUT]:
####Makes the player join an open instance of the game.
If lobby=*true*, the last game of this player will be updated to the provided gameId.

**body**:
The body acts as a filter for the join
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
- sid (string)[OPTIONAL]: The uniqueId of the server the player is connecting to.
- err (string)[OPTIONAL]: An error string that describes why the player was not connected to the server Only provided when success=*false*.
