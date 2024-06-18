import {test, expect, jest} from "@jest/globals"
import {makeCoord, makeTile, Coord, Board, Tile, TurnAnswer, Placement} from "../src/Data"

import {BoardLocation, GameBoard, Loc, PlayerTile, PlayerTiles, Scores, expandBoard, Place} from "../src/Game"

test("Empty Board Location", () => {
    const result = BoardLocation({value: "Empty", onClick: () => ("Clicked!")});
    expect(result.props.className).toBe("board-location");
    expect(result.props.onClick()).toBe("Clicked!");
    //TODO: This will need changing in the future when rendering changes.
    expect(result.props.children).toBe("Empty");
});

test("Tile Board Location", () => {
    const result = BoardLocation({value: makeTile("purple", "square"), 
                                onClick: () => ("There's a Tile Here!")});
    expect(result.props.className).toBe("board-location");
    expect(result.props.onClick()).toBe("There's a Tile Here!");
    //TODO: This will need changing in the future when rendering changes.
    expect(result.props.children).toBe("P S");
});

test("Expanding an empty board", () => {
    expect(() => (expandBoard(new Board)))
    .toThrow("Cannot expand an empty board!");
});

test("1 Tile expansion", () =>  {
    const b : Board = new Board();
    b.set(makeCoord(4, 5), makeTile("red", "diamond"));
    expect(b.get(makeCoord(4, 5))).toEqual(makeTile("red", "diamond"))
    const spots = expandBoard(b);
    expect(spots.length).toBe(3);
    expect(spots[0].length).toBe(3);
    expect(spots.map((row) => row.length).reduce((a, b) => (a + b)))
    .toBe(9);
    //console.log(spots);
    expect(spots[0][0].coord).toEqual(makeCoord(3, 4));
    expect(spots[0][0].value).toEqual("Empty");
    expect(spots[0][1].coord).toEqual(makeCoord(4, 4));
    expect(spots[0][2].coord).toEqual(makeCoord(5, 4));
    expect(spots[1][0].coord).toEqual(makeCoord(3, 5));
    expect(spots[1][1].coord).toEqual(makeCoord(4, 5));
    expect(spots[1][1].value).toEqual(makeTile("red", "diamond"));
    expect(spots[1][2].coord).toEqual(makeCoord(5, 5));
    expect(spots[2][0].coord).toEqual(makeCoord(3, 6));
    expect(spots[2][0].value).toEqual("Empty");
    expect(spots[2][1].coord).toEqual(makeCoord(4, 6));
    expect(spots[2][2].coord).toEqual(makeCoord(5, 6));
});

test("multi-tile expansion", () => {
    const b : Board = new Board();
    b.set(makeCoord(-5, 0), makeTile("purple", "clover"));
    b.set(makeCoord(5, 0), makeTile("green", "circle"));
    const spots = expandBoard(b);
    expect(spots.length).toBe(3);
    expect(spots[0].length).toBe(13);
    expect(spots[0][0].coord).toEqual(makeCoord(-6, -1));
    expect(spots[1][0].value).toBe("Empty");
    expect(spots[1][1].value).toEqual(makeTile("purple", "clover"));
    expect(spots[1][2].value).toBe("Empty");
    expect(spots[1][6].coord).toEqual(makeCoord(0, 0));
    expect(spots[1][11].coord).toEqual(makeCoord(5, 0));
    expect(spots[1][11].value).toEqual(makeTile("green", "circle"));
    expect(spots[1][12].value).toBe("Empty");
    expect(spots[2][12].coord).toEqual(makeCoord(6, 1));
});

test("game-board test", () => {
    const b = new Board();
    b.set(makeCoord(-5, 5), makeTile("blue", "circle"));
    const result = GameBoard({board: b, placer: (c) => () => (b.set(c, makeTile("red", "diamond")))})
    expect(result.props.children.length).toBe(3);
    expect(result.props.children[0].props.children.length).toBe(3);
    result.props.children[0].props.children[0].props.onClick();
    expect(b.get(makeCoord(-6, 4))).toEqual(makeTile("red", "diamond"));
    result.props.children[1].props.children[1].props.onClick();
    expect(b.get(makeCoord(-5, 5))).toEqual(makeTile("red", "diamond"));
});

test("Selected Player Tile test", () => {
    let count = 0;
    const result = PlayerTile({value : makeTile("orange", "diamond"), id : 1, active : 1, onClick: () => {count+=1}});
    expect(result.props.className).toBe("player-tile");
    expect(result.props.variant).toBe("outlined");
    expect(result.props.children).toBe("O D");
    expect(count).toBe(0);
    result.props.onClick();
    expect(count).toBe(1);
});

test("Unselected Player Tile test", () => {
    let count = 0;
    const result = PlayerTile({value : makeTile("purple", "square"), id : 1, active : 10, onClick: () => {count+=1}});
    expect(result.props.className).toBe("player-tile");
    expect(result.props.variant).toBe("text");
    expect(result.props.children).toBe("P S");
    expect(count).toBe(0);
    result.props.onClick();
    expect(count).toBe(1);
});

test("PlayerTiles test", () => {
    let activePair  : [Tile, number] = [makeTile("purple", "circle"), 2];
    const result = PlayerTiles({tiles: [makeTile("orange", "diamond"), makeTile("red", "square")], active : 0, 
    selector : (t, key) => () => {activePair = [t, key]}})
    console.log(result.props.children[0]);
    expect(result.props.container).toBe(true);
    expect(result.props.spacing).toBe(0.5);
    expect(result.props.children[0].key).toEqual("0");
    result.props.children[0].props.onClick()
    expect(activePair).toEqual([makeTile("orange", "diamond"), 0]);
    //expect(result.props.children[0].props.variant).toBe("outlined");
    expect(result.props.children[1].key).toEqual("1");
    result.props.children[1].props.onClick()
    expect(activePair).toEqual([makeTile("red", "square"), 1]);
    //expect(result.props.children[0].props.variant).toBe("text");
});

test("Single player scores test", () => {
    const result = Scores({playerScore: 12345, otherScores: []});
    const listItems = result.props.children;
    expect(result.type).toBe("ol");
    expect(listItems.length).toBe(1);
    expect(listItems[0].type).toBe("li");
    expect(listItems[0].props.children).toBe("you: 12345");
});

test("Multi player scores test", () => {
    const result = Scores({playerScore: 2, otherScores: [1, 2, 3]});
    const listItems = result.props.children;
    expect(result.type).toBe("ol");
    expect(listItems.length).toBe(4);
    expect(listItems[0].type).toBe("li");
    expect(listItems[0].props.children).toBe("not you: 3");
    expect(listItems[1].type).toBe("li");
    expect(listItems[1].props.children).toBe("not you: 2");
    expect(listItems[2].type).toBe("li");
    expect(listItems[2].props.children).toBe("you: 2");
    expect(listItems[3].type).toBe("li");
    expect(listItems[3].props.children).toBe("not you: 1");
});

test("Place no placements test", () => {
    let submission : TurnAnswer | "initialized" = "initialized";
    const result = Place({submission: (ans: TurnAnswer) => submission = ans, placements: []});
    result.props.onClick();
    expect(submission).toEqual([]);
    expect(result.props.disabled).toBe(true);
})

test("Place some placements test", () => {
    let submission : TurnAnswer | "initialized" = "initialized";
    let placements : Placement[] = [[makeCoord(3, 4), makeTile("blue", "circle")]];
    const result = Place({submission: (ans: TurnAnswer) => submission = ans, placements: placements});
    result.props.onClick();
    expect(submission).toEqual(placements);
    expect(result.props.disabled).toBe(false)
});