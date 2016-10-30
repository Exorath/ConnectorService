# ConnectorService
Quickly allows servers to send players to games based on the game type, map and flavor.

##Endpoints

###/servers/find/{gameId} [GET]:
####Gets general information about the running servers of the specified game type
**Arguments**:
- gameId (string): The id of the game

**Response**: {pc: 24, sc: 3, osc: 1}
- pc (int): The amount of players currently on the game servers of this gameId
- sc (int): The amount of servers of this game type
- osc(int): The amount of servers of this game type that are open for joining.
- err (string)[OPTIONAL]: Error message when the get failed

###/servers/{serverUuid}?gameId={gameId}&mapId={mapId}&flavorId={f;avorId}&ttl={ttl}&pc={pc}&mpc={mpc}&joinable={joinable}&address={address} [PUT]:
####Updates the server record.
**Arguments**:
- serverUuid (string): The unique id of this server
- gameId (string): The gameId of the server instance
- mapId (string): The mapId of the server instance
- flavorId (string): The flavorId of the server instance
- ttl (int): Time to live of this record in seconds
- pc (int): Amount of players currently on this server
- mpc (int): Maximum amount of players allowed on this server
- address (string): Address formated as ip:port
- joinable (boolean): Whether or not this server can be joined

**Response**: {success: true}
- success (boolean): Whether or not the record was updated successfully 
- err (string)[OPTIONAL]: Error message only responded when the update was not successful.
- err (string)[OPTIONAL]: Error message when the get failed

###/servers/{gameId}/player/{uuid}?mapId=1234&flavorId=1234 [POST]:
####Makes the player join an open instance of the game.
**Arguments**:
- gameId (string): The id of the game
- uuid (string): the player's uuid
- mapId (string)[OPTIONAL]: The mapId of the server that the player should join
- flavorId (string)[OPTIONAL]: The flavorId is a requirement put on the flavor of the game, this is optional to specify stuff like "teams", "doubles", "solo"...

**Response**: {"success": true,"sid": "2f132baf-f714-4a04-b58d-e012ea80a703"}
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
