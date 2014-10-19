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

public class Move extends Animation {
    private final Tile      src;
    private final Tile      dst;
    private final TileValue value;

    public Move(final Tile src, final Tile dst) {
        super(50);
        this.src = src;
        this.dst = dst;
        value = src.getValue();
        src.setValue(TileValue.TILE_EMPTY);
        if (dst.getValue() != dst.getFutureValue())
            dst.setValue(TileValue.TILE_EMPTY);
        dst.setFutureValue(value);
    }

    @Override
    public void paint(final Graphics2D g) {
        super.paint(g);
        if (src.getX() == dst.getX())
            value.paint(g, src.getScreenX(), src.getScreenY() + (int) (getPercentage() * (dst.getScreenY() - src.getScreenY())));
        else if (src.getY() == dst.getY())
            value.paint(g, src.getScreenX() + (int) (getPercentage() * (dst.getScreenX() - src.getScreenX())), src.getScreenY());
    }

    @Override
    public void terminate() {
        dst.actualize();
    }
}
