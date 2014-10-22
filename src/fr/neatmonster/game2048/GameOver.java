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

public class GameOver extends Animation {
    private final Game2048 game;
    private boolean reverse = false;

    public GameOver(final Game2048 game) {
        super(300);
        this.game = game;
    }

    @Override
    public void paint(final Graphics2D g) {
        super.paint(g);
        forcePaint(g);
    }

    public void reverse() {
        reverse = true;
        current = 0;
        duration = 200;
        game.animator.add(this);
    }

    public void forcePaint(final Graphics2D g) {
        if (reverse) {
            if (current < 200) {
                final float percentage = (float) current / 200f;
                g.setColor(new Color(238, 228, 218, 186));
                g.fillRect(22, 136, 495, 495);
                g.setColor(new Color(119, 110, 101, 255));
                g.setFont(Game2048.FONT.deriveFont(60f));
                g.drawString("Game over!", 112, 350 + (int) (percentage * 54f));
                g.setFont(Game2048.FONT.deriveFont(18f));
                g.setColor(new Color(119, 110, 101, 255 - (int) (percentage * 255)));
                g.drawString("Submit your score to the leaderboard", 114, 402 + (int) (percentage * 54f));
                game.username.setBounds(119, 418 + (int) (percentage * 54f), 230, 40);
                game.username.setPercentage(1f - percentage);
                game.validate.setBounds(359, 418 + (int) (percentage * 54f), 62, 40);
                game.validate.setPercentage(1f - percentage);
            } else {
                g.setColor(new Color(238, 228, 218, 186));
                g.fillRect(22, 136, 495, 495);
                g.setColor(new Color(119, 110, 101, 255));
                g.setFont(Game2048.FONT.deriveFont(60f));
                g.drawString("Game over!", 112, 404);
                if (game.getComponents().length > 0) {
                    game.remove(game.username);
                    game.remove(game.validate);
                }
            }
        } else {
            if (current < 100) {
                final float percentage = (float) current / 100f;
                g.setColor(new Color(238, 228, 218, (int) (percentage * 186f)));
                g.fillRect(22, 136, 495, 495);
                g.setColor(new Color(119, 110, 101, (int) (percentage * 255f)));
                g.setFont(Game2048.FONT.deriveFont(60f));
                g.drawString("Game over!", 112, 404);
            } else {
                final float percentage = (float) (current - 100) / 200f;
                g.setColor(new Color(238, 228, 218, 186));
                g.fillRect(22, 136, 495, 495);
                g.setColor(new Color(119, 110, 101, 255));
                g.setFont(Game2048.FONT.deriveFont(60f));
                g.drawString("Game over!", 112, 404 - (int) (percentage * 54f));
                g.setColor(new Color(119, 110, 101, (int) (percentage * 255)));
                g.setFont(Game2048.FONT.deriveFont(18f));
                g.drawString("Submit your score to the leaderboard", 114, 456 - (int) (percentage * 54f));
                if (game.getComponents().length == 0) {
                    game.add(game.username);
                    game.add(game.validate);
                }
                game.username.setBounds(119, 472 - (int) (percentage * 54f), 230, 40);
                game.username.setPercentage(percentage);
                game.validate.setBounds(359, 472 - (int) (percentage * 54f), 62, 40);
                game.validate.setPercentage(percentage);
            }
        }
    }
}
