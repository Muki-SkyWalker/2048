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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Animator extends Thread {
    private final Game2048 game;
    private final List<Animation> animations = new ArrayList<Animation>();
    private       long            current    = 0L;
    private       boolean         freeze     = false;
    private       long            start      = 0L;
    private       boolean         wasPlaying = false;

    public Animator(final Game2048 game) {
        this.game = game;
    }

    public void reset() {
        freeze = false;
        start = current = System.currentTimeMillis();
        game.scoreboard.logTime(start);
    }

    public long elapsed() {
        return (freeze ? current : System.currentTimeMillis()) - start;
    }

    public void paint(final Graphics2D g) {
        synchronized (animations) {
            for (final Animation animation : new ArrayList<Animation>(animations)) {
                if (!animation.isSpecial()) {
                    animation.paint(g);
                    if (animation.hasTerminated())
                        animations.remove(animation);
                }
            }
        }
    }

    public void specialPaint(final Graphics2D g) {
        synchronized (animations) {
            for (final Animation animation : new ArrayList<Animation>(animations)) {
                if (animation.isSpecial()) {
                    animation.paint(g);
                    if (animation.hasTerminated())
                        animations.remove(animation);
                }
            }
        }
    }

    public void unfreeze() {
        freeze = false;
        start += System.currentTimeMillis() - current;
        game.scoreboard.logTime(System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (true) {
            if (!freeze && System.currentTimeMillis() - current >= 1000L) {
                game.repaint();
                current += 1000L;
            }
            if (isPlaying() || game.username.isBlinking()) {
                wasPlaying = true;
                game.repaint();
            } else if (wasPlaying) {
                if (game.gameOver == null && game.isBlocked()) {
                    game.gameOver = new GameOver(game);
                    add(game.gameOver);
                    current = System.currentTimeMillis();
                    freeze = true;
                    game.scoreboard.logTime(current);
                } else if (game.gameWon == null && game.hasWon()) {
                    game.gameWon = new GameWon();
                    add(game.gameWon);
                    current = System.currentTimeMillis();
                    freeze = true;
                    game.scoreboard.logTime(current);
                }
                wasPlaying = false;
            }
            try {
                Thread.sleep(1L);
            } catch (final InterruptedException ignored) {
            }
        }
    }

    public boolean isPlaying() {
        synchronized (animations) {
            return !animations.isEmpty();
        }
    }

    public void add(final Animation animation) {
        synchronized (animations) {
            animations.add(animation);
        }
    }

    public void terminate(final boolean force) {
        synchronized (animations) {
            final List<Animation> sortedAnimations = new ArrayList<Animation>(animations);
            Collections.sort(sortedAnimations);
            for (final Animation animation : sortedAnimations)
                if (!(animation instanceof GameOver)) {
                    animation.terminate();
                    animations.remove(animation);
                }
            if (force)
                animations.clear();
        }
    }
}
