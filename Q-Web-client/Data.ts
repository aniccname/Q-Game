export{
    parseJPub,
    parseJPlayer,
    parseJTile,
    parseJMap,
    makeCoord,
    makeTile,
    Board,
    Coord,
    Tile,
    PlayerInfo,
    TurnInfo,
    Empty,
    test as localFunc,
}

//#region Client

type Color = "red" | "green" | "blue" | "yellow" | "orange" | "purple"
type Shape = "star" | "8star" | "square" | "circle" | "clover" | "diamond"

interface Tile {
    readonly color: Color,
    readonly shape: Shape
}

//TODO : Decide whether the function should enforce the enumeration of Colors and Shapes. 
const makeTile = (c : Color, s : Shape) => ({color: c, shape : s})

type Empty = "Empty"

type Coord = {
    readonly x: number,
    readonly y: number
}

/**
 * Makes a new Coord with the given x and y value. 
 * @param x The x position of the new coordinate.
 * @param y The y position of the new coordinate. 
 * @returns A new Coord with the given x and y value.
 */
const makeCoord = (x: number, y:number) => ({"x":x, "y":y})

type PlayerInfo = {
    readonly score: number,
    readonly tiles: Tile[]
}

type Board = Map<Coord, Tile>

type TurnInfo = {
    readonly board: Board,
    readonly myInfo: PlayerInfo,
    readonly poolSize: number,
    readonly otherScores: number[]
}

function test() {
    function doesThisBreak() {}
}
//#region JSON Serializers

//INVARIANT: b is not empty
function BoardToJMap (b : Board) : JMap {
    function PlaceInJRow (coord : Coord, t : Tile) : void {
        let jCells = rows.get(coord.x);
        if (jCells === undefined) {
            rows.set(coord.x, [TileToJCell(coord.y, t)]);
        }
        else {
            jCells.push(TileToJCell(coord.y, t));
        }
    }
    
    if (b.size == 0) {
        throw "Cannot convert an empty board to JMap"
    }

    const rows = new Map<number, JCell[]>

    b.forEach((t, c, _) => PlaceInJRow(c, t))
    const jMap : JMap = []
    
    for (let [row, cells] of rows) {
        let jRow : JRow = [row]
        cells.forEach((jc, _) => jRow.push(jc))
        jMap.push(jRow)
    }
    return jMap
}

/**
 * Serializes a Board into a JMap representation of a board.
 * @param b The Board to serialize
 * @returns The JMap representation of a board as a string.
 */
function serializeBoard(b: Board) : string {
    return JSON.stringify(BoardToJMap(b))
}

function TileToJCell (ColumnIndex : number, t : Tile) : JCell{
    return [ColumnIndex, t]
}

function TileToJTile(t : Tile) : JTile {
    return t;
}

/**
 * Serializes a Board into a JTile representation of a Tile
 * @param t The Tile to serialize.
 * @returns The JTile representation of a board as a string.
 */
function serializeTile(t : Tile) : string {
    return JSON.stringify(TileToJTile(t))
}

//#endregion JSON Serializers

//console.log(JSON.stringify({color : "red", shape : "star"}))

//#endregion Client

//#region JSON
//JSON data definitions for conversions.

type JTile = Tile


interface JCoordinate {
    row: number,
    column: number
}

// [ColumnIndex, JTile]
type JCell = [number, JTile]

// [RowIndex, JCell]
type JRow = [number, ...JCell[]]

type JMap = [...JRow[]]

interface JPlayer {
    score: Number,
    name?: String,
    "tile*:": Array<JTile>
}

interface JPub {
    map: JMap,
    "tile*": Number,
    players: [JPlayer, Number, ...Number[]]
}

//Parsing Functionality

/**
 * Parses the given JTile into a Tile.
 * @param text The JTile to parse.
 * @returns a new Tile equivalent to the given JTile.
 */
function parseJTile(text: string | any) : Tile {
    let parsed;
    if (typeof text === "string") {
        try {
            parsed = JSON.parse(text);
        } catch (e) {
            throw Error("Unable to parse given JPub. " + e);
        }
    }
    else {
        parsed = text;
    }
    if (parsed.color !== undefined && parsed.shape !== undefined) {
        return parsed;
    }
    else {
        throw Error("Given JSON is not a JTile")
    }
}

/**
 * Parses the given JMap into a board.
 * @param text The JMap to parse.
 * @returns a new Board equivalent to the given JTile.
 */
function parseJMap(text: string | any) : Board {
    const b : Board = new Map<Coord, Tile>
    const parsed = typeof text === "string" ? JSON.parse(text) : text;
    function parseJRow(parsed: JRow) : void {
        if (!(parsed instanceof Array)) {
            throw new Error("Given JSON value is not a JMap");
        }
        for (let jCell of parsed.slice(1)) {
            parseJCell(parsed[0], jCell)
        }
    }
    function parseJCell(row : number, jCell : any) : void {
        if(!(jCell instanceof Array && typeof jCell[0] === 'number' && typeof jCell[1] === 'object')) {
            throw new Error("Given JSON value is not a JCell");
        }
        b.set(makeCoord(jCell[0], row), parseJTile(jCell[1]))
    }

    for (let jRow of parsed) {
        parseJRow(jRow)
    }
    if (b.size == 0) {
        throw Error("JMap must be non empty!");
    }  
    return b
}

/**
 * Parses the given JPlayer into a board.
 * @param text The JPlayer to parse.
 * @returns a new PlayerInfo equivalent to the given JPlayer.
 */
function parseJPlayer(text: string | any) : PlayerInfo {
    const parsed = typeof text === "string" ? JSON.parse(text) : text;
    if (!(parsed instanceof Object && typeof parsed.score === "number" &&  parsed["tile*"] instanceof Array)) {
        throw new Error("Given JSON value is not a JPlayer!")
    }
    return {
        score: parsed.score,
        tiles: parsed["tile*"]
    }
}

/**
 * Parses the given JPub into a TurnInfo.
 * @param text The JPub to parse.
 * @returns a new TurnInfo equivalent to the given JPub.
 */
function parseJPub(text: string | any) : TurnInfo {
    let parsed;
    if (typeof text === "string") {
        try {
            parsed = JSON.parse(text);
        } catch (e) {
            throw Error("Unable to parse given JPub. " + e)
        }
    }
    else {
        parsed = text
    }
    if (!(parsed instanceof Object && typeof parsed.map === "object" 
    && typeof parsed["tile*"] === "number" && typeof parsed.players === "object" && parsed.players.length > 1)) {
        throw new Error("Given JSON value is not a JPub!")
    }
    return {
        board: parseJMap(parsed.map),
        poolSize: parsed["tile*"],
        myInfo: parseJPlayer(parsed.players[0]),
        otherScores: parsed.players.slice(1)
    }
}

//#endregion JSON