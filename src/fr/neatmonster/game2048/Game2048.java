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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048 extends JPanel {
    private static final Random random          = new Random();
    private static final Color  BACKGROUND      = new Color(0xfaf8ef);
    private static final Color  GRID_BACKGROUND = new Color(0xbbada0);
    private static final Color  FOOTER_COLOR    = new Color(0xbbac99);
    private static final Color  HEADER_COLOR    = new Color(0xf9f6f2);
    private static final Color  TEXT_COLOR      = new Color(0xeee4da);
    public static Font FONT;

    static {
        try {
            FONT = Font.createFont(Font.TRUETYPE_FONT, Game2048.class.getResourceAsStream("/ClearSans-Bold.ttf"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private final Animator animator;
    private final Tile[]   board    = new Tile[16];
    public        GameOver gameOver = null;
    private       int      moves    = 0;
    private       int      score    = 0;

    public Game2048() {
        animator = new Animator(this);
        animator.start();
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                board[x + y * 4] = new Tile(x, y);
        reset();
        setFocusable(true);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                final int key = e.getKeyCode();
                if (animator.isPlaying() && gameOver == null && (key == 27 || key >= 37 && key <= 40))
                    animator.terminate();
                else if (animator.isPlaying())
                    return;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    reset();
                if (gameOver != null)
                    return;
                final int delay;
                if (e.getKeyCode() == KeyEvent.VK_UP)
                    delay = moveUp();
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    delay = moveDown();
                else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    delay = moveLeft();
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    delay = moveRight();
                else
                    return;
                moves++;
                if (delay > 0)
                    insertRandomTile(delay);
            }
        });
    }

    public static void main(final String[] args) {
        final Game2048 game = new Game2048();
        game.setPreferredSize(new Dimension(539, 691));
        final JFrame frame = new JFrame();
        frame.setTitle("2048");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void reset() {
        moves = score = 0;
        gameOver = null;
        animator.reset();
        for (final Tile tile : board)
            tile.setValue(TileValue.TILE_EMPTY);
        insertRandomTile(0);
        insertRandomTile(0);
        repaint();
    }

    Tile getTile(final int x, final int y) {
        return board[x + y * 4];
    }

    public boolean isBlocked() {
        if (emptyTiles().size() > 0)
            return false;
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 4; y++)
                if (getTile(x, y).getValue() == getTile(x + 1, y).getValue())
                    return false;
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 3; y++)
                if (getTile(x, y).getValue() == getTile(x, y + 1).getValue())
                    return false;
        return true;
    }

    int moveUp() {
        int yLimit, delay = 0;
        for (int x = 0; x < 4; x++) {
            yLimit = 0;
            for (int y = 1; y < 4; y++) {
                final Tile tile = getTile(x, y);
                if (tile.getValue() == TileValue.TILE_EMPTY)
                    continue;
                int yEmpty = y;
                while (yEmpty > yLimit && getTile(x, yEmpty - 1).getFutureValue() == TileValue.TILE_EMPTY)
                    yEmpty--;
                if (yEmpty > yLimit && getTile(x, yEmpty - 1).getFutureValue() == tile.getValue()) {
                    animator.add(new Move(tile, getTile(x, yEmpty - 1)));
                    animator.add(new Fusion(getTile(x, yEmpty - 1), getTile(x, yEmpty - 1).getFutureValue().next()));
                    score += getTile(x, yEmpty - 1).getFutureValue().next().getValue();
                    delay = 100;
                    yLimit++;
                } else if (yEmpty < y) {
                    animator.add(new Move(getTile(x, y), getTile(x, yEmpty)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        return delay;
    }

    int moveDown() {
        int yLimit, delay = 0;
        for (int x = 0; x < 4; x++) {
            yLimit = 3;
            for (int y = 2; y >= 0; y--) {
                final Tile tile = getTile(x, y);
                if (tile.getValue() == TileValue.TILE_EMPTY)
                    continue;
                int yEmpty = y;
                while (yEmpty < yLimit && getTile(x, yEmpty + 1).getFutureValue() == TileValue.TILE_EMPTY)
                    yEmpty++;
                if (yEmpty < yLimit && getTile(x, yEmpty + 1).getFutureValue() == tile.getValue()) {
                    animator.add(new Move(tile, getTile(x, yEmpty + 1)));
                    animator.add(new Fusion(getTile(x, yEmpty + 1), getTile(x, yEmpty + 1).getFutureValue().next()));
                    score += getTile(x, yEmpty + 1).getFutureValue().next().getValue();
                    delay = 100;
                    yLimit--;
                } else if (yEmpty > y) {
                    animator.add(new Move(getTile(x, y), getTile(x, yEmpty)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        return delay;
    }

    int moveLeft() {
        int xLimit, delay = 0;
        for (int y = 0; y < 4; y++) {
            xLimit = 0;
            for (int x = 1; x < 4; x++) {
                final Tile tile = getTile(x, y);
                if (tile.getValue() == TileValue.TILE_EMPTY)
                    continue;
                int xEmpty = x;
                while (xEmpty > xLimit && getTile(xEmpty - 1, y).getFutureValue() == TileValue.TILE_EMPTY)
                    xEmpty--;
                if (xEmpty > xLimit && getTile(xEmpty - 1, y).getFutureValue() == tile.getValue()) {
                    animator.add(new Move(tile, getTile(xEmpty - 1, y)));
                    animator.add(new Fusion(getTile(xEmpty - 1, y), getTile(xEmpty - 1, y).getFutureValue().next()));
                    score += getTile(xEmpty - 1, y).getFutureValue().next().getValue();
                    delay = 100;
                    xLimit++;
                } else if (xEmpty < x) {
                    animator.add(new Move(getTile(x, y), getTile(xEmpty, y)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        return delay;
    }

    int moveRight() {
        int xLimit, delay = 0;
        for (int y = 0; y < 4; y++) {
            xLimit = 3;
            for (int x = 2; x >= 0; x--) {
                final Tile tile = getTile(x, y);
                if (tile.getValue() == TileValue.TILE_EMPTY)
                    continue;
                int xEmpty = x;
                while (xEmpty < xLimit && getTile(xEmpty + 1, y).getFutureValue() == TileValue.TILE_EMPTY)
                    xEmpty++;
                if (xEmpty < xLimit && getTile(xEmpty + 1, y).getFutureValue() == tile.getValue()) {
                    animator.add(new Move(tile, getTile(xEmpty + 1, y)));
                    animator.add(new Fusion(getTile(xEmpty + 1, y), getTile(xEmpty + 1, y).getFutureValue().next()));
                    score += getTile(xEmpty + 1, y).getFutureValue().next().getValue();
                    delay = 100;
                    xLimit--;
                } else if (xEmpty > x) {
                    animator.add(new Move(getTile(x, y), getTile(xEmpty, y)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        return delay;
    }

    List<Tile> emptyTiles() {
        final List<Tile> tiles = new ArrayList<Tile>();
        for (final Tile tile : board)
            if (tile.getFutureValue() == TileValue.TILE_EMPTY)
                tiles.add(tile);
        return tiles;
    }

    void insertRandomTile(final int delay) {
        final List<Tile> tiles = emptyTiles();
        if (tiles.size() > 0) {
            final Tile tile = tiles.get(random.nextInt(tiles.size()));
            final TileValue value = random.nextFloat() < 0.9f ? TileValue.TILE_2 : TileValue.TILE_4;
            animator.add(new Appear(tile, value, delay));
        }
    }

    @Override
    public void paint(final Graphics g_) {
        super.paint(g_);
        final Graphics2D g = (Graphics2D) g_;
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, 539, 691);
        final String score = Integer.toString(this.score);
        Font font = FONT.deriveFont(40f);
        Rectangle2D bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), score).getVisualBounds();
        final int width = Math.max(102, 32 + (int) bounds.getWidth());
        g.setColor(GRID_BACKGROUND);
        g.fillRoundRect(517 - width, 22, width, 92, 6, 6);
        g.setColor(GRID_BACKGROUND);
        g.fillRoundRect(22, 136, 495, 495, 6, 6);
        g.setColor(HEADER_COLOR);
        font = FONT.deriveFont(23f);
        g.setFont(font);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), "SCORE").getVisualBounds();
        g.drawString("SCORE", 516 - (int) bounds.getWidth() - (int) ((width - bounds.getWidth()) / 2f), 54);
        g.setColor(TEXT_COLOR);
        font = FONT.deriveFont(40f);
        g.setFont(font);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), score).getVisualBounds();
        final int offset = score.startsWith("1") ? 4 : 0;
        g.drawString(score, 515 - offset - (int) bounds.getWidth() - (int) ((width - bounds.getWidth()) / 2f), 98);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        for (final Tile tile : board)
            tile.paint(g);
        animator.paint(g);
        g.setColor(FOOTER_COLOR);
        font = FONT.deriveFont(23f);
        g.setFont(font);
        g.drawString(moves + " moves", 22, 669);
        final long millis = animator.elapsed();
        final int minutes = (int) (millis / 60000L);
        final int seconds = (int) ((millis - minutes * 60000L) / 1000L);
        final String time = String.format(minutes + ":%02d", seconds);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), time).getVisualBounds();
        g.drawString(time, 515 - (int) bounds.getWidth(), 669);
        if (gameOver != null)
            gameOver.forcePaint(g);
    }
}
