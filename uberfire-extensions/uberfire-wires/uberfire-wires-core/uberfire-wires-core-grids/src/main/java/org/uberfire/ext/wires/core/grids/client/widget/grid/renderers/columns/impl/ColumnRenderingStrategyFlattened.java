/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.List;

import com.ait.lienzo.client.core.shape.BoundingBoxPathClipper;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class ColumnRenderingStrategyFlattened {

    @SuppressWarnings("unused")
    public Group render(final GridColumn<?> column,
                        final GridBodyColumnRenderContext context,
                        final BaseGridRendererHelper rendererHelper,
                        final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final double absoluteGridY = context.getAbsoluteGridY();
        final double absoluteColumnX = context.getAbsoluteColumnX();
        final double clipMinY = context.getClipMinY();
        final double clipMinX = context.getClipMinX();
        final int minVisibleRowIndex = context.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = context.getMaxVisibleRowIndex();
        final List<Double> visibleRowOffsets = renderingInformation.getVisibleRowOffsets();
        final boolean isFloating = context.isFloating();
        final GridData model = context.getModel();
        final Transform transform = context.getTransform();
        final GridRenderer renderer = context.getRenderer();

        final GridRendererTheme theme = renderer.getTheme();
        final Group g = new Group();

        final double columnWidth = column.getWidth();
        final double columnHeight = visibleRowOffsets.get(maxVisibleRowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0) + model.getRow(maxVisibleRowIndex).getHeight();

        //Grid lines - horizontal
        final MultiPath bodyGrid = theme.getBodyGridLine();
        for (int rowIndex = minVisibleRowIndex; rowIndex <= maxVisibleRowIndex; rowIndex++) {
            final double y = visibleRowOffsets.get(rowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0);
            bodyGrid.M(0,
                       y + 0.5).L(columnWidth,
                                  y + 0.5);
        }

        //Grid lines - vertical
        final int columnIndex = model.getColumns().indexOf(column);
        if (columnIndex < model.getColumnCount() - 1) {
            bodyGrid.M(columnWidth + 0.5,
                       0).L(columnWidth + 0.5,
                            columnHeight);
        }

        //Column content
        final Group columnGroup = new Group();
        for (int rowIndex = minVisibleRowIndex; rowIndex <= maxVisibleRowIndex; rowIndex++) {
            final double y = visibleRowOffsets.get(rowIndex - minVisibleRowIndex) - visibleRowOffsets.get(0);
            final GridRow row = model.getRow(rowIndex);
            final double rowHeight = row.getHeight();
            final GridBodyCellRenderContext cellContext = new GridBodyCellRenderContext(absoluteColumnX,
                                                                                        absoluteGridY + renderer.getHeaderHeight() + visibleRowOffsets.get(rowIndex - minVisibleRowIndex),
                                                                                        columnWidth,
                                                                                        rowHeight,
                                                                                        clipMinY,
                                                                                        clipMinX,
                                                                                        rowIndex,
                                                                                        columnIndex,
                                                                                        isFloating,
                                                                                        transform,
                                                                                        renderer);

            //Cell's content
            final GridCell<?> cell = model.getCell(rowIndex,
                                                   columnIndex);
            final Group cc = column.getColumnRenderer().renderCell((GridCell) cell,
                                                                   cellContext);
            if (cc != null) {
                cc.setX(0).setY(y).setListening(false);
                columnGroup.add(cc);
            }
        }

        //Clip Column Group
        final BoundingBox bb = new BoundingBox(0,
                                               0,
                                               columnWidth,
                                               columnHeight);
        final IPathClipper clipper = new BoundingBoxPathClipper(bb);
        columnGroup.setPathClipper(clipper);
        clipper.setActive(true);

        g.add(columnGroup);
        g.add(bodyGrid);

        return g;
    }
}
