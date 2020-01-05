package com.davenonymous.libnonymous.gui.config;

import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.*;
import com.davenonymous.libnonymous.network.Networking;
import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SettingListEntry extends WidgetListEntry {


    private static Color COLOR_ENABLED = new Color(50, 125, 50);
    private static Color COLOR_DISABLED = new Color(160, 160, 160, 255);
    private static Color COLOR_ERRORED = new Color(150, 50, 50);

    public SettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super();

        WidgetTextBox label = new WidgetTextBox(optionKey, 0xA0FFFFFF);
        label.setDimensions(2, 2, columnWidth, 9);
        this.add(label);

        if(comment == null) {
            comment = "";
        }

        String shortDescription;
        int firstDot = comment.indexOf(".")+1;
        if(firstDot == 0) {
            if(comment.length() > 70) {
                shortDescription = comment.substring(0, 70);
            } else {
                shortDescription = comment;
            }
        } else {
            shortDescription = comment.substring(0, comment.indexOf(".")+1);
        }

        if(shortDescription.length() < comment.length()) {
            shortDescription += " ...";
        }

        int headerHeight = 14;
        int commentHeight = 29;
        if(shortDescription.length() <= 0) {
            commentHeight = 0;
        }
        WidgetTextBox labelComment = new WidgetTextBox(shortDescription, 0xA0CCCCCC);
        labelComment.setDimensions(0, 13, (int)(columnWidth * 0.85f), commentHeight);
        if(comment.length() > 0) {
            labelComment.setTooltipLines(new StringTextComponent(comment));
        }
        this.add(labelComment);

        this.setSize(columnWidth, 45);

        Object uncastVal = value.get();
        if(uncastVal instanceof Boolean) {
            int entryHeight = 5 + headerHeight + commentHeight;
            this.setSize(columnWidth, entryHeight);

            boolean val = (boolean) uncastVal;
            int buttonX = columnWidth-16;
            int buttonY = ((entryHeight-16)/2)-2;

            WidgetFontAwesome toggleButtonOn = new WidgetFontAwesome(FontAwesomeIcons.REGULAR_CheckCircle, WidgetFontAwesome.IconSize.MEDIUM);
            toggleButtonOn.setColor(COLOR_ENABLED);
            toggleButtonOn.setPosition(buttonX, buttonY);
            this.add(toggleButtonOn);

            WidgetFontAwesome toggleButtonOff = new WidgetFontAwesome(FontAwesomeIcons.REGULAR_Circle, WidgetFontAwesome.IconSize.MEDIUM);
            toggleButtonOff.setColor(COLOR_DISABLED);
            toggleButtonOff.setPosition(buttonX, buttonY);
            this.add(toggleButtonOff);

            toggleButtonOff.addListener(MouseClickEvent.class, (event, widget) -> {
                value.set(true);
                saveValueWithRaceConditionRetries(value);

                toggleButtonOn.setVisible(true);
                toggleButtonOff.setVisible(false);

                //Networking.reloadConfigs();

                return WidgetEventResult.HANDLED;
            });

            toggleButtonOn.addListener(MouseClickEvent.class, (event, widget) -> {
                value.set(false);
                saveValueWithRaceConditionRetries(value);
                toggleButtonOn.setVisible(false);
                toggleButtonOff.setVisible(true);

                //Networking.reloadConfigs();
                return WidgetEventResult.HANDLED;
            });

            toggleButtonOn.setVisible(val);
            toggleButtonOff.setVisible(!val);
        } else if(uncastVal instanceof List) {
            this.setSize(columnWidth, 55);

            if(defaultValue == null || !(defaultValue instanceof List) || ((List)defaultValue).size() == 0 ) {
                // TODO: Add message to gui.
                Logz.warn("Config lists without default value are not supported. We don't know the type otherwise!");
                return;
            }

            labelComment.setText(shortDescription + " (separate with commas)");
            Object firstDefaultValue = ((List) defaultValue).get(0);
            if(firstDefaultValue instanceof String) {
                List<String> val = (List<String>) uncastVal;
                StringBuilder sb = new StringBuilder();

                for(String listEntry : val) {
                    sb.append(listEntry);
                    sb.append(",");
                }
                if(sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                WidgetInputField inputField = new WidgetInputField();
                inputField.setValue(sb.toString());
                inputField.setDimensions(5, 33, columnWidth-26, 14);
                this.add(inputField);

                WidgetFontAwesome save = new WidgetFontAwesome(FontAwesomeIcons.SOLID_FileDownload, WidgetFontAwesome.IconSize.MEDIUM);
                save.setColor(COLOR_DISABLED);
                save.setPosition(columnWidth-16, 32);
                save.addListener(MouseEnterEvent.class, (event, widget) -> {
                    save.setColor(COLOR_ENABLED);
                    return WidgetEventResult.CONTINUE_PROCESSING;
                });
                save.addListener(MouseExitEvent.class, (event, widget) -> {
                    save.setColor(COLOR_DISABLED);
                    return WidgetEventResult.CONTINUE_PROCESSING;
                });
                save.addListener(MouseClickEvent.class, (event, widget) -> {
                    String inputText = inputField.getText();
                    if(inputText == null || inputText.length() == 0) {
                        value.set(Collections.EMPTY_LIST);
                    } else {
                        value.set(Arrays.asList(inputText.split(",")));
                    }
                    saveValueWithRaceConditionRetries(value);
                    return WidgetEventResult.CONTINUE_PROCESSING;
                });
                this.add(save);
            }
        } else if(uncastVal instanceof Double) {
            double val = (double) uncastVal;
            this.setSize(columnWidth, 55);

            WidgetInputField inputField = new WidgetInputField();
            inputField.setValue(String.format("%.4f", val));
            inputField.setDimensions(5, 33, columnWidth - 26, 14);
            this.add(inputField);

            WidgetFontAwesome save = new WidgetFontAwesome(FontAwesomeIcons.SOLID_FileDownload, WidgetFontAwesome.IconSize.MEDIUM);
            save.setColor(COLOR_DISABLED);
            save.setPosition(columnWidth - 16, 32);
            save.addListener(MouseEnterEvent.class, (event, widget) -> {
                save.setColor(COLOR_ENABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseExitEvent.class, (event, widget) -> {
                save.setColor(COLOR_DISABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseClickEvent.class, (event, widget) -> {
                String inputText = inputField.getText();
                double inputVal = 0.0d;
                boolean success = true;
                try {
                    inputVal = Double.parseDouble(inputText);
                } catch(NumberFormatException e) {
                    success = false;
                }
                if(success) {
                    value.set(inputVal);
                    saveValueWithRaceConditionRetries(value);
                } else {
                    save.setColor(COLOR_ERRORED);
                }
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            this.add(save);
        } else if(uncastVal instanceof Integer) {
            int val = (int) uncastVal;
            this.setSize(columnWidth, 55);

            WidgetIntegerSelect inputField = new WidgetIntegerSelect(0, Integer.MAX_VALUE, val);
            inputField.setDimensions(5, 33, columnWidth - 26, 14);
            this.add(inputField);

            WidgetFontAwesome save = new WidgetFontAwesome(FontAwesomeIcons.SOLID_FileDownload, WidgetFontAwesome.IconSize.MEDIUM);
            save.setColor(COLOR_DISABLED);
            save.setPosition(columnWidth - 16, 32);
            save.addListener(MouseEnterEvent.class, (event, widget) -> {
                save.setColor(COLOR_ENABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseExitEvent.class, (event, widget) -> {
                save.setColor(COLOR_DISABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseClickEvent.class, (event, widget) -> {
                int inputValue = inputField.getValue();
                value.set(inputValue);
                saveValueWithRaceConditionRetries(value);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            this.add(save);

        } else if(uncastVal instanceof String) {
            String val = (String) uncastVal;
            this.setSize(columnWidth, 55);

            WidgetInputField inputField = new WidgetInputField();
            inputField.setValue(val);
            inputField.setDimensions(5, 33, columnWidth - 26, 14);
            this.add(inputField);

            WidgetFontAwesome save = new WidgetFontAwesome(FontAwesomeIcons.SOLID_FileDownload, WidgetFontAwesome.IconSize.MEDIUM);
            save.setColor(COLOR_DISABLED);
            save.setPosition(columnWidth - 16, 32);
            save.addListener(MouseEnterEvent.class, (event, widget) -> {
                save.setColor(COLOR_ENABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseExitEvent.class, (event, widget) -> {
                save.setColor(COLOR_DISABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseClickEvent.class, (event, widget) -> {
                String inputValue = inputField.getValue();
                value.set(inputValue);
                saveValueWithRaceConditionRetries(value);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            this.add(save);

        } else {
            Logz.warn("Unknown config value type: {}", uncastVal.getClass());
        }

    }

    private void saveValueWithRaceConditionRetries(ForgeConfigSpec.ConfigValue value) {
        value.save();
        value.save();
        value.save();
        value.save();
        value.save();
    }
}