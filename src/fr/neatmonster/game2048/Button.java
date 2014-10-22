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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class Button extends JButton {
    private Shape shape      = null;
    private float percentage = 0f;

    public Button(final Game2048 game) {
        super("Go");
        setBorder(null);
        setBackground(new Color(0, 0, 0, 0));
        setFont(Game2048.FONT.deriveFont(20f));
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || game.animator.isPlaying())
                    return;
                final String username = game.username.getText();
                if (username.length() < 3 || username.length() > 20) {
                    game.username.blink();
                } else {
                    final String message = game.scoreboard.send(username);
                    if (message == null)
                        game.gameOver.reverse();
                    else
                        game.animator.add(new Error());
                }
            }
        });
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                game.dispatchEvent(e);
            }
        });
    }

    @Override
    protected void paintComponent(final Graphics g) {
        g.setColor(new Color(143, 122, 102, (int) (percentage * 255f)));
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
        super.paintComponent(g);
    }

    @Override
    public boolean contains(final int x, final int y) {
        if (percentage < 1f)
            return false;
        else if (shape == null)
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
        return shape.contains(x, y);
    }

    public void setPercentage(final float percentage) {
        this.percentage = percentage;
        setForeground(new Color(249, 246, 242, (int) (percentage * 255f)));
    }
}
