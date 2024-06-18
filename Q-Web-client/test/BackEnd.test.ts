import { test, expect, jest} from "@jest/globals"
import { match, validPlacement} from "../src/BackEnd"
import { Board, makeCoord, makeTile } from "../src/Data"

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
    expect(match(makeTile("red", "square"), makeTile("green", "circle"))).toBe(false);
})

test("One tile left", () => {
    const b : Board = new Board();
    b.set(makeCoord(-1, 0), makeTile("blue", "circle"));
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "circle"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "square"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("blue", "square"), b)).toBe(true);
})

test("One tile right", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "clover"));
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "diamond"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("orange", "diamond"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "clover"), b)).toBe(true);
})

test("Surrounded by tiles horizontally", () => {
    const b : Board = new Board();
    b.set(makeCoord(1, 0), makeTile("purple", "circle"));
    b.set(makeCoord(-1, 0), makeTile("red", "circle"));
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "circle"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "circle"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "diamond"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("red", "diamond"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "diamond"), b)).toBe(false);
})

test("One tile above", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, -1), makeTile("yellow", "square"));
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "square"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "circle"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "circle"), b)).toBe(true);
})

test("One tile below", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, 1), makeTile("orange", "star"));
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "star"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("green", "diamond"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("orange", "diamond"), b)).toBe(true);
})

test("Surrounded by tiles vertically", () => {
    const b : Board = new Board();
    b.set(makeCoord(0, -1), makeTile("yellow", "8star"));
    b.set(makeCoord(0, 1), makeTile("yellow", "star"));
    expect(validPlacement(makeCoord(0, 0), makeTile("yellow", "clover"), b)).toBe(true);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "star"), b)).toBe(false);
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "clover"), b)).toBe(false);
})

test("No tiles", () => {
    const b : Board = new Board();
    expect(validPlacement(makeCoord(0, 0), makeTile("purple", "diamond"), b)).toBe(false);
    expect(validPlacement(makeCoord(1, 2), makeTile("yellow", "clover"), b)).toBe(false);
})