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

public class GameWon extends Animation {
    private boolean hidden = false;

    public GameWon() {
        super(100);
    }

    public void hide() {
        hidden = true;
    }

    public boolean isVisible() {
        return !hidden;
    }

    @Override
    public void paint(final Graphics2D g) {
        super.paint(g);
        forcePaint(g);
    }

    public void forcePaint(final Graphics2D g) {
        if (!hidden) {
            g.setColor(new Color(237, 194, 46, (int) (getPercentage() * 128f)));
            g.fillRect(22, 136, 495, 495);
            g.setColor(new Color(249, 246, 242, (int) (getPercentage() * 255f)));
            g.setFont(Game2048.FONT.deriveFont(60f));
            g.drawString("You win!", 155, 404);
        }
    }
}
