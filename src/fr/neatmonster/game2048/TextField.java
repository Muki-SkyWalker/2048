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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.regex.Pattern;

public class TextField extends JTextField implements FocusListener {
    private boolean hint       = true;
    private Shape   shape      = null;
    private float   percentage = 0f;
    private int     blink      = 0;

    public TextField(final Game2048 game) {
        setBackground(new Color(0, 0, 0, 0));
        setCaretColor(new Color(0x776e65));
        setFont(Game2048.FONT.deriveFont(18f));
        setSelectedTextColor(new Color(0x776e65));
        addFocusListener(this);
        focusLost(null);
        ((AbstractDocument) getDocument()).setDocumentFilter(new UsernameFilter());
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                game.dispatchEvent(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    game.validate.dispatchEvent(new MouseEvent(game.validate, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                            MouseEvent.BUTTON1_MASK, 10, 10, 1, false, MouseEvent.BUTTON1));
                    TextField.this.requestFocus();
                }
            }
        });
    }

    @Override
    protected void paintComponent(final Graphics g) {
        g.setColor(new Color(252, 251, 249, (int) (percentage * 255f)));
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
        if (isBlinking()) {
            final float percentage = blink < 25 ? 1f - (float) blink / 25f : (float) (blink - 25) / 25f;
            g.setColor(new Color(255, 0, 0, (int) (percentage * 255)));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            blink--;
        }
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(final Graphics g) {
    }

    @Override
    public boolean contains(final int x, final int y) {
        if (percentage < 1f)
            return false;
        else if (shape == null)
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
        return shape.contains(x, y);
    }

    @Override
    public void focusGained(final FocusEvent e) {
        if (getText().isEmpty()) {
            setForeground(new Color(0x776e65));
            setText("");
            hint = false;
        }
    }

    @Override
    public void focusLost(final FocusEvent e) {
        if (getText().isEmpty()) {
            setForeground(new Color(0x9d948c));
            setText("Your username");
            hint = true;
        }
    }

    @Override
    public String getText() {
        return hint ? "" : super.getText();
    }

    public void setPercentage(final float percentage) {
        this.percentage = percentage;
        final Color c = getForeground();
        setForeground(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (percentage * 255f)));
    }

    public boolean isBlinking() {
        return blink > 0;
    }

    public void blink() {
        blink = 50;
    }

    class UsernameFilter extends DocumentFilter {
        private final Pattern pattern = Pattern.compile("[-\\w]*");

        @Override
        public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) throws BadLocationException {
            final StringBuilder builder = new StringBuilder();
            builder.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            builder.insert(offset, string);
            if (isValid(builder.toString()))
                fb.insertString(offset, string, attr);
        }

        @Override
        public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
            final StringBuilder builder = new StringBuilder();
            builder.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            builder.replace(offset, offset + length, text);
            if (isValid(builder.toString()) || text.equals("Your username"))
                fb.replace(offset, length, text, attrs);
        }

        public boolean isValid(final String s) {
            return pattern.matcher(s).matches();
        }
    }
}
