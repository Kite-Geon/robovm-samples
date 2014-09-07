
package org.robovm.samples.teslameter.views;

import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIView;

public class GraphView extends UIView {
    private int nextIndex;
    private final double[][] history = new double[150][3];

    public GraphView (CGRect bounds) {
        super(bounds);
        setBackgroundColor(UIColor.colorWhite());
    }

    public void updateHistory (double x, double y, double z) {
        // Add to history.
        history[nextIndex][0] = x;
        history[nextIndex][1] = y;
        history[nextIndex][2] = z;

        // Advance the index counter.
        nextIndex = (nextIndex + 1) % 150;

        // Mark itself as needing to be redrawn.
        setNeedsDisplay();
    }

    private void drawGraph (CGContext context, CGRect bounds) {
        double value, temp;

        // Save any previous graphics state settings before setting the color and line width for the current draw.
        context.saveGState();
        context.setLineWidth(1.0);

        // Draw the intermediate lines
        context.setGrayStrokeColor(0.6, 1.0);
        context.beginPath();
        for (value = -5 + 1.0; value <= 5 - 1.0; value += 1.0) {
            if (value == 0.0) {
                continue;
            }
            temp = 0.5 + Math.round(bounds.origin().y() + bounds.size().height() / 2 + value / (2 * 5) * bounds.size().height());
            context.moveToPoint(bounds.origin().x(), temp);
            context.addLineToPoint(bounds.origin().x() + bounds.size().width(), temp);
        }
        context.strokePath();

        // Draw the center line
        context.setGrayStrokeColor(0.25, 1.0);
        context.beginPath();
        temp = 0.5 + Math.round(bounds.origin().y() + bounds.size().height() / 2);
        context.moveToPoint(bounds.origin().x(), temp);
        context.addLineToPoint(bounds.origin().x() + bounds.size().width(), temp);
        context.strokePath();

        // Restore previous graphics state.
        context.restoreGState();
    }

    private void drawHistory (int axis, int index, CGContext context, CGRect bounds) {
        double value;

        context.beginPath();
        for (int counter = 0; counter < 150; ++counter) {
            // UIView referential has the Y axis going down, so we need to draw upside-down.
            value = history[(index + counter) % 150][axis] / -128;
            if (counter > 0) {
                context.addLineToPoint(bounds.origin().x() + (float)counter / (float)(150 - 1) * bounds.size().width(), bounds
                    .origin().y() + bounds.size().height() / 2 + value * bounds.size().height() / 2);
            } else {
                context.moveToPoint(bounds.origin().x() + (float)counter / (float)(150 - 1) * bounds.size().width(), bounds
                    .origin().y() + bounds.size().height() / 2 + value * bounds.size().height() / 2);
            }
        }
        // Save any previous graphics state settings before setting the color and line width for the current draw.
        context.saveGState();
        context.setRGBStrokeColor((axis == 0 ? 1.0 : 0.0), (axis == 1 ? 1.0 : 0.0), (axis == 2 ? 1.0 : 0.0), 1.0);
        context.setLineWidth(2.0);
        context.strokePath();
        // Restore previous graphics state.
        context.restoreGState();
    }

    @Override
    public void draw (CGRect rect) {
        int index = nextIndex;

        CGContext context = UIGraphics.getCurrentContext();
        CGRect bounds = new CGRect(0, 0, getBounds().size().width(), getBounds().size().height());

        // create the graph
        drawGraph(context, bounds);

        // plot x,y,z with anti-aliasing turned off
        context.setAllowsAntialiasing(false);
        for (int i = 0; i < 3; ++i) {
            drawHistory(i, index, context, bounds);
        }
        context.setAllowsAntialiasing(true);
    }
}
