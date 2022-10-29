package com.thebombzen.jxlatte.frame.lfglobal;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.thebombzen.jxlatte.InvalidBitstreamException;
import com.thebombzen.jxlatte.MathHelper;
import com.thebombzen.jxlatte.entropy.EntropyStream;
import com.thebombzen.jxlatte.frame.BlendingInfo;
import com.thebombzen.jxlatte.io.Bitreader;


public class Patch {
    public final int width;
    public final int height;
    public final int ref;
    public final int x0, y0;
    public final Point[] positions;
    public final BlendingInfo[] blendingInfos;

    public Patch(EntropyStream stream, Bitreader reader, int extraChannelCount, int alphaChannelCount) throws IOException {
        this.ref = stream.readSymbol(reader, 1);
        this.x0 = stream.readSymbol(reader, 3);
        this.y0 = stream.readSymbol(reader, 3);
        this.width = 1 + stream.readSymbol(reader, 2);
        this.height = 1 + stream.readSymbol(reader, 2);
        int count = 1 + stream.readSymbol(reader, 7);
        if (count <= 0)
            throw new InvalidBitstreamException("That's a lot of patches!");
        positions = new Point[count];
        blendingInfos = new BlendingInfo[count + 1];
        positions[0] = new Point();
        positions[0].x = stream.readSymbol(reader, 4);
        positions[0].y = stream.readSymbol(reader, 4);
        for (int j = 1; j < count; j++) {
            int x = stream.readSymbol(reader, 5);
            int y = stream.readSymbol(reader, 5);
            x = MathHelper.unpackSigned(x) + positions[j - 1].x;
            y = MathHelper.unpackSigned(y) + positions[j - 1].y;
            positions[j] = new Point(x, y);
        }
        for (int j = 0; j < count; j++) {
            int mode = stream.readSymbol(reader, 5);
            int alpha = 0;
            boolean clamp = false;
            if (mode >= 8)
                throw new InvalidBitstreamException("Illegal blending mode in patch");
            if (mode > 3 && alphaChannelCount > 1) {
                alpha = stream.readSymbol(reader, 8);
                if (alpha > extraChannelCount)
                    throw new InvalidBitstreamException("Alpha out of bounds");

            }
            if (mode > 2)
                clamp = stream.readSymbol(reader, 9) != 0;
            blendingInfos[j] = new BlendingInfo(mode, alpha, clamp, 0);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(positions);
        result = prime * result + Arrays.hashCode(blendingInfos);
        result = prime * result + Objects.hash(width, height, ref, x0, y0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Patch other = (Patch) obj;
        return width == other.width && height == other.height && ref == other.ref && x0 == other.x0 && y0 == other.y0
                && Arrays.equals(positions, other.positions) && Arrays.equals(blendingInfos, other.blendingInfos);
    }

}