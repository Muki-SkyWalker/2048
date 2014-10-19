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
    private       boolean         wasPlaying = false;

    public Animator(final Game2048 game) {
        this.game = game;
    }

    public void paint(final Graphics2D g) {
        synchronized (animations) {
            for (final Animation animation : new ArrayList<Animation>(animations)) {
                animation.paint(g);
                if (animation.hasTerminated())
                    animations.remove(animation);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (isPlaying()) {
                wasPlaying = true;
                game.repaint();
            } else if (wasPlaying) {
                if (game.isBlocked() && game.gameOver == null)
                    add(new GameOver(game));
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

    public void terminate() {
        synchronized (animations) {
            final List<Animation> sortedAnimations = new ArrayList<Animation>(animations);
            Collections.sort(sortedAnimations);
            for (final Animation animation : sortedAnimations)
                if (!(animation instanceof GameOver)) {
                    animation.terminate();
                    animations.remove(animation);
                }
        }
    }
}
