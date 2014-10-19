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

public enum TileValue {
    TILE_EMPTY(1, 0xeee4da, 0xeee4da, 0f, 0),
    TILE_2(2, 0xeee4da, 0x776e65, 55f, 3),
    TILE_4(4, 0xede0c8, 0x776e65, 55f, 1),
    TILE_8(8, 0xf2b179, 0xf9f6f2, 55f, 2),
    TILE_16(16, 0xf59563, 0xf9f6f2, 55f, 6),
    TILE_32(32, 0xf67c5f, 0xf9f6f2, 55f, 2),
    TILE_64(64, 0xf65e3b, 0xf9f6f2, 55f, 3),
    TILE_128(128, 0xedcf72, 0xf9f6f2, 45f, 5),
    TILE_256(256, 0xedcc61, 0xf9f6f2, 45f, 2),
    TILE_512(512, 0xedc850, 0xf9f6f2, 45f, 1),
    TILE_1024(1024, 0xedc53f, 0xf9f6f2, 35f, 4),
    TILE_2048(2048, 0xedc22e, 0xf9f6f2, 35f, 1),
    TILE_4096(4096, 0x3c3a32, 0xf9f6f2, 30f, 1),
    TILE_8192(8192, 0x3c3a32, 0xf9f6f2, 30f, 1),
    TILE_16384(16384, 0x3c3a32, 0xf9f6f2, 30f, 3),
    TILE_32768(32768, 0x3c3a32, 0xf9f6f2, 30f, 0);
    private final int       value;
    private final TileColor background;
    private final TileColor foreground;
    private final float     fontSize;
    private final int       offset;

    private TileValue(final int value, final int background, final int foreground, final float fontSize, final int offset) {
        this.value = value;
        this.background = new TileColor(background);
        this.foreground = new TileColor(foreground);
        this.fontSize = fontSize;
        this.offset = offset;
    }

    public void paint(final Graphics2D g, final int x, final int y) {
        paint(g, x, y, this == TileValue.TILE_EMPTY ? 90 : 255, 105);
    }

    public void paintAlpha(final Graphics2D g, final int x, final int y, final int alpha) {
        paint(g, x, y, alpha, 105);
    }

    public void paintSize(final Graphics2D g, final int x, final int y, final int size) {
        paint(g, x, y, 255, size);
    }

    void paint(final Graphics2D g, final int x, final int y, final int alpha, final int size) {
        g.setColor(background.derive(alpha));
        g.fillRoundRect(x - (size - 105) / 2, y - (size - 105) / 2, size, size, 6, 6);
        if (value > 1) {
            g.setColor(foreground.derive(alpha));
            final Font font = Game2048.FONT.deriveFont((float) size / 105f * fontSize);
            g.setFont(font);
            final Rectangle2D bounds = font.createGlyphVector(g.getFontMetrics(font).getFontRenderContext(), "" + value).getVisualBounds();
            g.drawString("" + value, x - offset + (105 - (int) bounds.getWidth()) / 2, y + (int) bounds.getHeight() + (105 - (int) bounds.getHeight()) / 2);
        }
    }

    public TileValue next() {
        return TileValue.valueOf("TILE_" + 2 * value);
    }

    public class TileColor {
        private final Color color;

        public TileColor(final int color) {
            this.color = new Color(color);
        }

        public Color derive(final int alpha) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }
    }
}
