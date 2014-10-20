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
import java.awt.geom.Rectangle2D;

public class Score extends Animation {
    private final Game2048 game;
    private final int      value;

    public Score(final Game2048 game, final int value) {
        super(300);
        this.game = game;
        this.value = value;
    }

    @Override
    public void paint(final Graphics2D g) {
        super.paint(g);
        g.setColor(new Color(119, 110, 101, (int) ((1f - getPercentage()) * 230f)));
        final Font font = Game2048.FONT.deriveFont(40f);
        g.setFont(font);
        final Rectangle2D bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), "+" + value).getVisualBounds();
        g.drawString("+" + value, game.scoreX - 10 - (int) bounds.getWidth(), 98 - (int) (getPercentage() * 150f));
    }
}
