export{
    parseJPub,
    parseJPlayer,
    parseJTile,
    parseJMap,
    makeCoord,
    makeTile,
    descendingOrder,
    serializeTurnAnswer,
    findThisPlayerIndex,
    Board,
    Coord,
    Tile,
    PlayerInfo,
    OpponentInfo,
    TurnInfo,
    Empty,
    Placement,
    TurnAnswer,
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

type Placement = [Coord, Tile]

/**
 * Makes a new Coord with the given x and y value. 
 * @param x The x position of the new coordinate.
 * @param y The y position of the new coordinate. 
 * @returns A new Coord with the given x and y value.
 */
const makeCoord = (x: number, y:number) => ({"x":x, "y":y})

/**
 * Compares the two coordinates in descending order (top to bottom, left to right).
 * @param c1 A coordinate to compare.
 * @param c2 A coordinate to compare.
 */
function descendingOrder(c1 : Coord, c2: Coord) : number{
    const verticalDiff = c1.y - c2.y;
    if (verticalDiff === 0) {
        return c1.x - c2.x
    }
    else return verticalDiff;
}

type PlayerInfo = {
    readonly score: number,
    readonly tiles: Tile[]
}

type OpponentInfo = {
    readonly name: string, 
    readonly score: number, 
    readonly numTiles: number
}

type TurnAnswer = "pass" | "replace" | Placement[]

class Board implements Map<Coord, Tile>{
    private internalMap : Map<String, Tile>;
    private untransformedEntries : [...[Coord, Tile][]];
    size: number;
    constructor (b? : Board) {
        this.internalMap = new Map<String, Tile>();
        this.untransformedEntries = [];
        this.size = 0;
        if (b) {
            for (let [c, v] of b.entries()) {
                this.internalMap.set(this.transform(c), v);
                this.untransformedEntries.push([c, v]);
                this.size += 1;
            }
        }
    }

    public clear(): void {
        this.untransformedEntries =[];
        this.internalMap.clear();
        this.size = 0;
    }
    public delete(key: Coord): boolean {
        if (this.internalMap.has(this.transform(key))) {
            this.internalMap.delete(this.transform(key));
            this.untransformedEntries = this.untransformedEntries.filter(([coord, _]) => (coord !== key));
            this.size -= 1;
            return true;
        }
        return false;
    }
    public forEach(callbackfn: (value: Tile, key: Coord, map: Map<Coord, Tile>) => void, thisArg?: any): void {
        throw new Error("Method not implemented.")
    }
    public has(key: Coord): boolean {
        return this.internalMap.has(this.transform(key));
    }
    public keys(): IterableIterator<Coord> {
        return this.untransformedEntries.map(([coord, _]) => (coord)).values();
    }
    public values(): IterableIterator<Tile> {
        return this.internalMap.values();
    }
    [Symbol.iterator](): IterableIterator<[Coord, Tile]> {
        return this.untransformedEntries.values();
    }
    [Symbol.toStringTag]: string = "Board";
    private transform(c : Coord) : string{
        return "(" + c.x + "," + c.y + ")";
    }
    public get(key: Coord): Tile | undefined {
        return this.internalMap.get(this.transform(key));
    }
    public set(key: Coord, value: Tile): this {
        this.untransformedEntries = this.untransformedEntries.filter(([c, _]) => (c.x != key.x || c.y != key.y));
        this.untransformedEntries.push([key, value]);
        this.internalMap.set(this.transform(key), value);
        this.size = this.internalMap.size;
        return this;
    }
    public entries(): IterableIterator<[Coord, Tile]> {
        return this.untransformedEntries.values();
    }
}

type TurnInfo = {
    readonly global: GlobalInfo,
    readonly player: PlayerInfo
}

type GlobalInfo = {
    readonly board: Board,
    readonly poolSize: number,
    readonly playerOrdering: (OpponentInfo)[],
    readonly activePlayerIndx: number | false
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

function CoordToJCoordinate(c : Coord) : JCoordinate {
    return {row : c.y, column : c.x}
}

/**
 * Serializes a Board into a JTile representation of a Tile
 * @param t The Tile to serialize.
 * @returns The JTile representation of a board as a string.
 */
function serializeTile(t : Tile) : string {
    return JSON.stringify(TileToJTile(t))
}

/**
 * Transforms the Placement into a OnePlacement to send to the server. 
 * @param p the placement to encode
 */
function PlacementToOnePlacement(p : Placement) : OnePlacement {
    const [coord, tile] = p;
    return {coordinate : CoordToJCoordinate(coord), "1tile": TileToJTile(tile)}
}

/**
 * Transforms the TurnAnswer into valid JChoice to send to the server. 
 * @param ta The turn answer to turn into transform into a JChoice
 */
function TurnAnswerToJChoice(ta : TurnAnswer) : JChoice {
    if (ta == "pass" || ta == "replace") {
        return ta;
    } else {
        return ta.map(PlacementToOnePlacement);
    }
}

function serializeTurnAnswer(ta: TurnAnswer) : string {
    return JSON.stringify(TurnAnswerToJChoice(ta));
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
};

interface JOpponent {
    score: Number, 
    name?: String, 
    "tile#": Number
};

interface JPub {
    map: JMap,
    "tile*": Number,
    players: [(JPlayer|JOpponent)[]]
};

type OnePlacement = {coordinate: JCoordinate, "1tile": JTile};
type JPlacements = OnePlacement[];
type JChoice = "pass" | "replace" | JPlacements

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
            throw Error("Unable to parse given Jtile. " + e);
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
    const b : Board = new Board();
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
 * Parses the given JPlayer or JOpponent into a OpponentInfo
 * @param text The JPlayer or JOpponent to parse.
 * @Returns a new JOpponent representing the JPlayer or JOpponent
 */
function parsePlayers(text: string | any) : OpponentInfo {
    let parsed;
    if (typeof text === "string") {
        try {
            parsed = JSON.parse(text);
        } catch (e) {
            throw Error("Unable to parse given JPub. " + e)
        }
    }
    else {
        parsed = text;
    }
    if (!(parsed instanceof Object 
        && typeof parsed["score"] === "number" 
        && typeof parsed["name"] === "string" && 
        (typeof parsed["tile#"] === "number" || typeof parsed["tile*"] === "object"))) {
        throw new Error("Given JSON value is not a JPlayer or JOpponent!")
    }
    return {
        name: parsed["name"],
        score: parsed["score"],
        numTiles: parsed["tile*"] != undefined ? parsed["tile*"].length : parsed["tile#"]
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
    && typeof parsed["tile*"] === "number" && typeof parsed.players === "object" && parsed.players.length >= 1)) {
        throw new Error("Given JSON value " + JSON.stringify(parsed) + "is not a JPub!")
    }
    const player = parsed.players.find((p : any) => p["tile*"] != undefined);
    const order = parsed.players.map(parsePlayers);
    return {
        global: {
            board: parseJMap(parsed.map),
            poolSize: parsed["tile*"],
            playerOrdering: order,
            activePlayerIndx: false
        },
        player: parseJPlayer(player)
    }
}

/**
 * Finds the index of this player.
 * @returns The index of this web program's player. 
 */
function findThisPlayerIndex(text : string | any) {
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
    && typeof parsed["tile*"] === "number" && typeof parsed.players === "object" && parsed.players.length >= 1)) {
        throw new Error("Given JSON value " + JSON.stringify(parsed) + "is not a JPub!")
    }
    return parsed.players.findIndex((p : any) => p["tile*"] != undefined);
}

//#endregion JSON