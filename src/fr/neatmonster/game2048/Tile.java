/*
 * This file is part of 2048, licensed under the MIT License (MIT).
 *
 * Copyright (c) NeatMonster <neatmonster@hotmail.fr>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.neatmonster.game2048;

import java.awt.*;

class Tile {
    private final int       x;
    private final int       y;
    private       TileValue value;
    private       TileValue futureValue;

    public Tile(final int x, final int y) {
        this.x = x;
        this.y = y;
        value = futureValue = TileValue.TILE_EMPTY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScreenX() {
        return 15 + 120 * x;
    }

    public int getScreenY() {
        return 15 + 120 * y;
    }

    public TileValue getValue() {
        return value;
    }

    public void setValue(final TileValue value) {
        this.value = futureValue = value;
    }

    public TileValue getFutureValue() {
        return futureValue;
    }

    public void setFutureValue(final TileValue value) {
        futureValue = value;
    }

    public void actualize() {
        value = futureValue;
    }

    public void paint(final Graphics2D g) {
        value.paint(g, getScreenX(), getScreenY());
    }
}
