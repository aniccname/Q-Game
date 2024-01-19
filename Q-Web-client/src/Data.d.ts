export { parseJPub, parseJPlayer, parseJTile, parseJMap, makeCoord, makeTile, Board, Coord, Tile, PlayerInfo, TurnInfo, Empty, test as localFunc, };
type Color = "red" | "green" | "blue" | "yellow" | "orange" | "purple";
type Shape = "star" | "8star" | "square" | "circle" | "clover" | "diamond";
interface Tile {
    readonly color: Color;
    readonly shape: Shape;
}
declare const makeTile: (c: Color, s: Shape) => {
    color: Color;
    shape: Shape;
};
type Empty = "Empty";
type Coord = {
    readonly x: number;
    readonly y: number;
};
/**
 * Makes a new Coord with the given x and y value.
 * @param x The x position of the new coordinate.
 * @param y The y position of the new coordinate.
 * @returns A new Coord with the given x and y value.
 */
declare const makeCoord: (x: number, y: number) => {
    x: number;
    y: number;
};
type PlayerInfo = {
    readonly score: number;
    readonly tiles: Tile[];
};
type Board = Map<Coord, Tile>;
type TurnInfo = {
    readonly board: Board;
    readonly myInfo: PlayerInfo;
    readonly poolSize: number;
    readonly otherScores: number[];
};
declare function test(): void;
/**
 * Parses the given JTile into a Tile.
 * @param text The JTile to parse.
 * @returns a new Tile equivalent to the given JTile.
 */
declare function parseJTile(text: string | any): Tile;
/**
 * Parses the given JMap into a board.
 * @param text The JMap to parse.
 * @returns a new Board equivalent to the given JTile.
 */
declare function parseJMap(text: string | any): Board;
/**
 * Parses the given JPlayer into a board.
 * @param text The JPlayer to parse.
 * @returns a new PlayerInfo equivalent to the given JPlayer.
 */
declare function parseJPlayer(text: string | any): PlayerInfo;
/**
 * Parses the given JPub into a TurnInfo.
 * @param text The JPub to parse.
 * @returns a new TurnInfo equivalent to the given JPub.
 */
declare function parseJPub(text: string | any): TurnInfo;
