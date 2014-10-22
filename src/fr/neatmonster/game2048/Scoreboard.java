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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

public class Scoreboard {
    private final ByteArrayOutputStream log = new ByteArrayOutputStream();

    public void logAdd(final Tile tile, final TileValue value) {
        byte add = 0;
        add |= tile.getX();
        add |= tile.getY() << 2;
        add |= value.ordinal() << 4;
        log.write(add);
    }

    public void logMove(final byte move) {
        log.write(move);
    }

    public void logTime(final long time) {
        try {
            log.write((byte) 0);
            final byte[] src = ByteBuffer.allocate(8).putLong(time).array();
            final byte[] dst = new byte[7];
            System.arraycopy(src, 1, dst, 0, 7);
            log.write(dst);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        log.reset();
    }

    public String send(final String username) {
        try {
            String url = "http://2048.neatmonster.fr/submit";
            url += "?username=";
            url += username;
            url += "&log=";
            url += URLEncoder.encode(Base64.encode(log.toByteArray()), "UTF-8");
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine, response = "";
            while ((inputLine = input.readLine()) != null)
                response += inputLine;
            input.close();
            if (response.equals("true"))
                return null;
            return response;
        } catch (final Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
