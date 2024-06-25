import { test, expect, jest} from "@jest/globals"
import { match, validPlacement, validPlacements} from "../src/BackEnd"
import { Board, makeCoord, makeTile, serializeTurnAnswer } from "../src/Data"

test("match same tile", () => {
    expect(match(makeTile("blue", "8star"), makeTile("blue", "8star"))).toBe(true);
});

test("match same shape", () => {
    expect(match(makeTile("red", "circle"), makeTile("orange", "circle"))).toBe(true);
});

test("match same color", () => {
    expect(match(makeTile("purple", "8star"), makeTile("purple", "square"))).toBe(true);
})

test("match different color", () => {
    expect(match(makeTile("red", "square"), makeTile("green", "circle"))).toBe("red square does not match green circle.");
})

test("One tile left", () => {
    const b : Board = new Board();
    b.set(makeCoord(-1, 0), makeTile("blue", "circle"));
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "circle"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "square"), b))
    .toBe("green square does not match blue circle.");
    expect(validPlacement(makeCoord(0, 0), makeTile("blue", "square"), b)).toBe(true);
})

test("One tile right", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "clover"));
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "diamond"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("orange", "diamond"), b))
    .toBe("orange diamond does not match purple clover.");
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "clover"), b)).toBe(true);
})

test("Surrounded by tiles horizontally", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "circle"));
    b.set(makeCoord(-1, 0), makeTile("red", "circle"));
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "circle"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "circle"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "diamond"), b))
        .toBe("purple diamond does not match red circle.");
    expect(validPlacement(makeCoord(0, 0), makeTile("red", "diamond"), b))
        .toBe("red diamond does not match purple circle.");
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "diamond"), b))
        .toBe("yellow diamond does not match red circle.");
})

test("One tile above", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, -1), makeTile("yellow", "square"));
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "square"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "circle"), b))
    .toBe("purple circle does not match yellow square.");
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "circle"), b)).toBe(true);
})

test("One tile below", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, 1), makeTile("orange", "star"));
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "star"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "diamond"), b)).toBe("green diamond does not match orange star.");
    expect(validPlacement(makeCoord(0, 0), makeTile("orange", "diamond"), b)).toBe(true);
})

test("Surrounded by tiles vertically", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, -1), makeTile("yellow", "8star"));
    b.set(makeCoord(0, 1), makeTile("yellow", "star"));
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "clover"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "star"), b)).toBe("purple star does not match yellow 8star.");
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "clover"), b)).toBe("purple clover does not match yellow 8star.");
})

test("No tiles", () => {
    const b : Board = new Board();
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "diamond"), b))
        .toBe("The tile must be placed adjacent to at least 1 tile.");
    expect(validPlacement(makeCoord(1, 2), makeTile("yellow", "clover"), b))
        .toBe("The tile must be placed adjacent to at least 1 tile.");
})

test("Tiles match whole line", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, 0), makeTile("blue", "circle"));
    b.set(makeCoord(-1, 0), makeTile("green", "circle"));
    expect(validPlacement(makeCoord(1, 0), makeTile("blue", "8star"), b)).toBe("blue 8star does not match green circle.");
    expect(validPlacement(makeCoord(1, 0), makeTile("orange", "circle"), b)).toBe(true);
})

test("serializeTurnAnswer", () => {
    const result = '[{"coordinate": {"row": 4, "column": -1}, ' 
        + '"1tile": {"color": "green", "shape": "star"}}]';
    expect(serializeTurnAnswer("pass")).toBe('"pass"');
    expect(serializeTurnAnswer("replace")).toBe('"replace"');
    //JSON equality to see if they both parse to the same object
    expect(JSON.parse(serializeTurnAnswer([[makeCoord(-1, 4), makeTile("green", "star")]])))
    .toEqual(JSON.parse(result));
})

//Tis case should never happen
test("NoPlacements", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "clover"));
    expect(validPlacements([], b)).toBe(true);
})

test("OnePlacement", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "clover"));
    expect(validPlacements([[makeCoord(0, 0), makeTile("blue", "clover")]], b)).toBe(true);
    expect(validPlacements([[makeCoord(0, 0), makeTile("blue", "square")]], b))
        .toBe("blue square does not match purple clover.");
})

test("ManyPlacements", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "clover"));
    expect(validPlacements([[makeCoord(0, 0), makeTile("blue", "clover")],
                            [makeCoord(-1, 0), makeTile("blue", "clover")]], b)).toBe(true);
    expect(validPlacements([[makeCoord(0, 0), makeTile("blue", "clover")],
                            [makeCoord(0, 1), makeTile("blue", "clover")]], b)).toBe(true);
    expect(validPlacements([[makeCoord(1, 1), makeTile("blue", "clover")],
                            [makeCoord(0, 0), makeTile("blue", "clover")]], b)).toEqual("The placements are not in a line.");
    expect(validPlacements([[makeCoord(0, 0), makeTile("purple", "clover")],
                            [makeCoord(-1, 0), makeTile("blue", "clover")]], b)).toBe(true);
})