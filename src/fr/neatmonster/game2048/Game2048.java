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
    private static final Color  LOGO_COLOR      = new Color(0x776e65);
    public static Font FONT;

    static {
        try {
            FONT = Font.createFont(Font.TRUETYPE_FONT, Game2048.class.getResourceAsStream("/ClearSans-Bold.ttf"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public final  TextField  username   = new TextField(this);
    public final  Button     validate   = new Button(this);
    public final  Scoreboard scoreboard = new Scoreboard();
    public final  Animator   animator   = new Animator(this);
    private final Tile[]     board      = new Tile[16];
    public        GameOver   gameOver   = null;
    public        GameWon    gameWon    = null;
    public        int        scoreX     = 0;
    private       int        best       = 0;
    private       int        moves      = 0;
    private       int        score      = 0;

    public Game2048() {
        setLayout(null);
        animator.start();
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 4; y++)
                board[x + y * 4] = new Tile(x, y);
        reset();
        setFocusable(true);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                Game2048.this.requestFocus();
            }
        });
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                final int key = e.getKeyCode();
                if (animator.isPlaying() && gameOver == null && (gameWon == null || gameWon.hasTerminated())
                        && (key == 27 || key >= 37 && key <= 40))
                    animator.terminate(false);
                else if (animator.isPlaying())
                    return;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    reset();
                if (gameOver != null || isBlocked())
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
                if (gameWon != null && gameWon.isVisible()) {
                    animator.unfreeze();
                    gameWon.hide();
                }
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
        frame.setIconImage(new ImageIcon(Game2048.class.getResource("/icon.png")).getImage());
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void reset() {
        if (score > best)
            best = score;
        moves = score = 0;
        gameOver = null;
        remove(username);
        remove(validate);
        scoreboard.reset();
        gameWon = null;
        animator.reset();
        animator.terminate(true);
        for (final Tile tile : board)
            tile.setValue(TileValue.TILE_EMPTY);
        insertRandomTile(0);
        insertRandomTile(0);
        repaint();
    }

    Tile getTile(final int x, final int y) {
        return board[x + y * 4];
    }

    public boolean hasWon() {
        for (final Tile tile : board)
            if (tile.getValue() == TileValue.TILE_2048)
                return true;
        return false;
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
        scoreboard.logMove((byte) 1);
        int scoreInc = 0, yLimit, delay = 0;
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
                    scoreInc += getTile(x, yEmpty - 1).getFutureValue().next().getValue();
                    if (score > best)
                        best = score;
                    delay = 100;
                    yLimit = yEmpty;
                } else if (yEmpty < y) {
                    animator.add(new Move(getTile(x, y), getTile(x, yEmpty)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        if (scoreInc > 0)
            animator.add(new Score(this, scoreInc));
        return delay;
    }

    int moveDown() {
        scoreboard.logMove((byte) 2);
        int scoreInc = 0, yLimit, delay = 0;
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
                    scoreInc += getTile(x, yEmpty + 1).getFutureValue().next().getValue();
                    if (score > best)
                        best = score;
                    delay = 100;
                    yLimit = yEmpty;
                } else if (yEmpty > y) {
                    animator.add(new Move(getTile(x, y), getTile(x, yEmpty)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        if (scoreInc > 0)
            animator.add(new Score(this, scoreInc));
        return delay;
    }

    int moveLeft() {
        scoreboard.logMove((byte) 3);
        int scoreInc = 0, xLimit, delay = 0;
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
                    scoreInc += getTile(xEmpty - 1, y).getFutureValue().next().getValue();
                    if (score > best)
                        best = score;
                    delay = 100;
                    xLimit = xEmpty;
                } else if (xEmpty < x) {
                    animator.add(new Move(getTile(x, y), getTile(xEmpty, y)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        if (scoreInc > 0)
            animator.add(new Score(this, scoreInc));
        return delay;
    }

    int moveRight() {
        scoreboard.logMove((byte) 4);
        int scoreInc = 0, xLimit, delay = 0;
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
                    scoreInc += getTile(xEmpty + 1, y).getFutureValue().next().getValue();
                    if (score > best)
                        best = score;
                    delay = 100;
                    xLimit = xEmpty;
                } else if (xEmpty > x) {
                    animator.add(new Move(getTile(x, y), getTile(xEmpty, y)));
                    delay = delay < 50 ? 50 : delay;
                }
            }
        }
        if (scoreInc > 0)
            animator.add(new Score(this, scoreInc));
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
            scoreboard.logAdd(tile, value);
        }
    }

    @Override
    public void paint(final Graphics g_) {
        super.paint(g_);
        final Graphics2D g = (Graphics2D) g_;
        animator.specialPaint(g);
    }


    @Override
    public void paintComponent(final Graphics g_) {
        super.paintComponent(g_);
        final Graphics2D g = (Graphics2D) g_;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, 539, 691);
        g.setColor(LOGO_COLOR);
        Font font = FONT.deriveFont(80f);
        g.setFont(font);
        g.drawString("2048", 22, 95);
        final String best = Integer.toString(this.best);
        font = FONT.deriveFont(this.best > 99999 ? 28f : this.best > 9999 ? 34f : 40f);
        Rectangle2D bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), best).getVisualBounds();
        final int bestWidth = Math.max(85, 32 + (int) bounds.getWidth());
        g.setColor(GRID_BACKGROUND);
        g.fillRoundRect(517 - bestWidth, 22, bestWidth, 92, 6, 6);
        g.setColor(HEADER_COLOR);
        font = FONT.deriveFont(23f);
        g.setFont(font);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), "BEST").getVisualBounds();
        g.drawString("BEST", 516 - (int) bounds.getWidth() - (int) ((bestWidth - bounds.getWidth()) / 2f), 54);
        g.setColor(TEXT_COLOR);
        font = FONT.deriveFont(this.best > 99999 ? 28f : this.best > 9999 ? 34f : 40f);
        g.setFont(font);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), best).getVisualBounds();
        int offset = best.startsWith("1") ? 4 : 0;
        g.drawString(best, 515 - offset - (int) bounds.getWidth() - (int) ((bestWidth - bounds.getWidth()) / 2f),
                98 - (this.best > 99999 ? 4 : this.best > 9999 ? 2 : 0));
        final String score = Integer.toString(this.score);
        font = FONT.deriveFont(this.score > 99999 ? 28f : this.score > 9999 ? 34f : 40f);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), score).getVisualBounds();
        final int scoreWidth = Math.max(102, 32 + (int) bounds.getWidth());
        g.setColor(GRID_BACKGROUND);
        g.fillRoundRect(517 - bestWidth - 16 - scoreWidth, 22, scoreWidth, 92, 6, 6);
        g.setColor(HEADER_COLOR);
        font = FONT.deriveFont(23f);
        g.setFont(font);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), "SCORE").getVisualBounds();
        g.drawString("SCORE", 516 - bestWidth - 16 - (int) bounds.getWidth() - (int) ((scoreWidth - bounds.getWidth()) / 2f), 54);
        g.setColor(TEXT_COLOR);
        font = FONT.deriveFont(this.score > 99999 ? 28f : this.score > 9999 ? 34f : 40f);
        g.setFont(font);
        bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), score).getVisualBounds();
        offset = score.startsWith("1") ? 4 : 0;
        scoreX = 515 - bestWidth - 16 - offset - (int) ((scoreWidth - bounds.getWidth()) / 2f);
        g.drawString(score, scoreX - (int) bounds.getWidth(), 98 - (this.score > 99999 ? 4 : this.score > 9999 ? 2 : 0));
        g.setColor(GRID_BACKGROUND);
        g.fillRoundRect(22, 136, 495, 495, 6, 6);
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
        if (gameOver != null && gameOver.hasTerminated())
            gameOver.forcePaint(g);
        else if (gameWon != null && gameWon.hasTerminated())
            gameWon.forcePaint(g);
    }
}
