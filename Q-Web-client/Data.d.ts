export { parseJPub, parseJPlayer, parseJTile, makeCoord, makeTile, Board, Coord, Tile };
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
type Coord = {
    readonly x: number;
    readonly y: number;
};
declare const makeCoord: (x1: number, y1: number) => {
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
declare function parseJTile(text: string): Tile;
declare function parseJPlayer(text: string | any): PlayerInfo;
declare function parseJPub(text: string | any): TurnInfo;