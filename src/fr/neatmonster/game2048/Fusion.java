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

public class Fusion extends Animation {
    private final Tile      tile;
    private final TileValue value;

    public Fusion(final Tile tile, final TileValue value) {
        super(100);
        this.tile = tile;
        this.value = value;
    }

    @Override
    public void paint(final Graphics2D g) {
        super.paint(g);
        final int delay = 50;
        if (current < delay)
            return;
        tile.setFutureValue(value);
        final float percentage = (float) (current - delay) / (float) (duration - delay);
        final float size;
        if (percentage < 0.5f)
            size = percentage * 2.4f;
        else
            size = 1f + (1f - percentage) * 0.4f;
        final int width = (int) (size * 105);
        value.paintSize(g, tile.getScreenX(), tile.getScreenY(), width);
    }

    @Override
    public void terminate() {
        tile.setFutureValue(value);
        tile.actualize();
    }
}
