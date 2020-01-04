package com.davenonymous.libnonymous.gui.framework.widgets;


import com.davenonymous.libnonymous.gui.framework.ISelectable;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseScrollEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.config.GuiUtils;

public class WidgetList extends WidgetPanel {
    public int padding = 2;
    public int scrollLines = 1;

    private int lineOffset = 0;
    private int lastVisibleLine = 0;

    protected int selected = -1;


    public WidgetList() {
        super();

        this.addListener(MouseScrollEvent.class, (event, widget) -> {
            if(event.up) {
                this.scrollUp();
            } else {
                this.scrollDown();
            }

            return WidgetEventResult.CONTINUE_PROCESSING;
        });
    }

    @Override
    public void clear() {
        super.clear();
        this.selected = -1;
    }

    public void scrollUp() {
        this.lineOffset = Math.max(0, this.lineOffset - this.scrollLines);
        this.updateWidgets();
    }

    public void scrollDown() {
        if(lastVisibleLine == getTotalLines()-1) {
            return;
        }

        this.lineOffset += this.scrollLines;
        this.updateWidgets();
    }

    public void deselect() {
        this.selected = -1;
    }

    public int getTotalLines() {
        return this.children.size();
    }

    public int getLineHeight(int line) {
        if(line >= this.children.size()) {
            return 0;
        }

        return this.children.get(line).height;
    }

    @Override
    public void draw(Screen screen) {
        int backgroundColor = 0xFF333333;
        int borderColor = 0xFF000000;
        int selectedBackgroundColor = 0xFF555555;

        GuiUtils.drawGradientRect(0, 0, 0, width, height, borderColor, borderColor);
        GuiUtils.drawGradientRect(0, 1, 1, width-1, height-1, backgroundColor, backgroundColor);

        //Logz.info("Rendering lines %d to %d", lineOffset, lastVisibleLine);

        if(selected >= lineOffset && selected <= lastVisibleLine) {
            // We need to high-light a specific line
            int yOffset = 0;
            for(int line = lineOffset; line < selected; line++) {
                Widget widget = this.children.get(line);
                yOffset += widget.height;
            }

            Widget selectedWidget = this.children.get(selected);

            GuiUtils.drawGradientRect(0, 1, yOffset+1, width-1, yOffset+1+selectedWidget.height-1, selectedBackgroundColor, selectedBackgroundColor);
        }


        super.draw(screen);
    }

    /**
     * Deprecated, use addListEntry instead!
     *
     * @param widget
     */
    @Override
    @Deprecated
    public void add(Widget widget) {
        Logz.warn("Calling unused method to add widgets to list! Use WidgetList#addListEntry instead!");
    }

    public <T extends Widget & ISelectable> void addListEntry(T widget) {
        if(widget.height <= 0) {
            Logz.warn("Heightless widget [%s] added to list. This will cause problems.", widget);
        }
        if(widget.height > this.height) {
            Logz.warn("List has an entry larger than the list itself. This will cause problems.", widget);
        }

        final int line = this.children.size();
        widget.addListener(MouseClickEvent.class, (event, clickedWidget) -> {
            if(this.selected == line) {
                this.selected = -1;
                widget.setSelected(false);
            } else {
                if(this.selected != -1) {
                    Widget oldSelection = this.children.get(selected);
                    if(oldSelection instanceof ISelectable) {
                        ((ISelectable) oldSelection).setSelected(false);
                    }
                }

                this.selected = line;
                widget.setSelected(true);
            }

            return WidgetEventResult.CONTINUE_PROCESSING;
        });


        super.add(widget);

        updateWidgets();
    }

    public void updateWidgets() {
        int visibleHeight = padding;
        boolean exceededListHeight = false;
        for(int line = 0; line < this.children.size(); line++) {
            Widget widget = this.children.get(line);

            if(line < lineOffset) {
                // Widget is scrolled past -> hide
                widget.setVisible(false);
                continue;
            }

            if(visibleHeight + widget.height > this.height-padding) {
                // Widget won't fit -> no more widgets from here on out
                exceededListHeight = true;
            }

            if(exceededListHeight) {
                widget.setVisible(false);
                continue;
            }

            if(line == this.selected && widget instanceof ISelectable) {
                ((ISelectable) widget).setSelected(true);
            }

            widget.setVisible(true);
            widget.setY(visibleHeight);
            widget.setX(padding);
            visibleHeight += widget.height;
            lastVisibleLine = line;
        }
    }
}
