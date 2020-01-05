package com.davenonymous.libnonymous.gui.config;

import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.WidgetScreen;
import com.davenonymous.libnonymous.gui.framework.event.ListSelectionEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetList;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.utils.Logz;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WidgetGuiConfig extends WidgetScreen {
    Screen parent;
    List<ForgeConfigSpec> specs;

    private static Field FIELD_SPEC_COMMENTS;
    private static Field FIELD_SPEC_CHILD_CONFIG;
    private static Method METHOD_SPEC_GET_NIO_PATH;

    static {
        try {
            FIELD_SPEC_COMMENTS = ForgeConfigSpec.class.getDeclaredField("levelComments");
            FIELD_SPEC_COMMENTS.setAccessible(true);

            FIELD_SPEC_CHILD_CONFIG = ForgeConfigSpec.class.getDeclaredField("childConfig");
            FIELD_SPEC_CHILD_CONFIG.setAccessible(true);

            Class ACFC = Class.forName("com.electronwill.nightconfig.core.file.AutosaveCommentedFileConfig");
            METHOD_SPEC_GET_NIO_PATH = ACFC.getMethod("getNioPath");
            METHOD_SPEC_GET_NIO_PATH.setAccessible(true);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static String getFilenameFromSpec(ForgeConfigSpec spec) {
        Object childConfig = getAutosaveCommentedFileConfigFromSpec(spec);
        if(childConfig == null) {
            return null;
        }

        Path path = null;
        try {
            path = (Path) METHOD_SPEC_GET_NIO_PATH.invoke(childConfig);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if(path == null) {
            return null;
        }

        return path.getFileName().toString();
    }

    private static Object getAutosaveCommentedFileConfigFromSpec(ForgeConfigSpec spec) {
        try {
            return FIELD_SPEC_CHILD_CONFIG.get(spec);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Map<List<String>, String> getSpecComments(ForgeConfigSpec spec) {
        try {
            return (Map<List<String>, String>) FIELD_SPEC_COMMENTS.get(spec);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public WidgetGuiConfig(Screen parent, List<ForgeConfigSpec> specs) {
        super(new TranslationTextComponent("libnonymous.config.title"));
        this.parent = parent;
        this.specs = specs;
    }

    public WidgetGuiConfig(Screen parent, ForgeConfigSpec ... specs) {
        super(new TranslationTextComponent("libnonymous.config.title"));
        this.parent = parent;
        this.specs = Arrays.asList(specs);
    }

    public WidgetGuiConfig addSpec(ForgeConfigSpec spec) {
        this.specs.add(spec);
        return this;
    }

    private String getCategoryComment(ForgeConfigSpec spec, String category) {
        Map<List<String>, String> comments = getSpecComments(spec);
        for(Map.Entry<List<String>, String> entry : comments.entrySet()) {
            List<String> tags = entry.getKey();
            if(tags.contains(category)) {
                return entry.getValue();
            }
        }

        return "";
    }

    @Override
    protected GUI createGUI() {
        // Best resolution guessing begins:
        int desiredHeight = (int) (Minecraft.getInstance().mainWindow.getHeight() / Minecraft.getInstance().mainWindow.getGuiScaleFactor());
        int desiredWidth = (int) (Minecraft.getInstance().mainWindow.getWidth() / Minecraft.getInstance().mainWindow.getGuiScaleFactor());
        GUI gui = new GUI(0, 0, desiredWidth, desiredHeight);

        int columnWidths = (desiredWidth - 20) / 3;

        WidgetTextBox labelSpecs = new WidgetTextBox("Config", 0xC0000000);
        labelSpecs.setDimensions(6, 6, columnWidths, 9);
        gui.add(labelSpecs);

        WidgetList specList = new WidgetList();
        specList.setDimensions(5, 16, columnWidths, desiredHeight-21);
        specList.addListener(ListSelectionEvent.class, (event, widget) -> {
            specList.setSize(columnWidths, event.selectedEntry == -1 ? desiredHeight-21 : (desiredHeight/2)-21);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });
        gui.add(specList);


        for(ForgeConfigSpec spec : specs) {
            UnmodifiableConfig config = spec.getSpec();
            String filename = getFilenameFromSpec(spec);
            if(filename == null) {
                continue;
            }

            SpecListEntry specListEntry = new SpecListEntry(filename, columnWidths);
            specList.addListEntry(specListEntry);
            //Logz.info("Found spec: {}", filename);

            CategoriesPanel categoriesPanel = new CategoriesPanel(columnWidths, desiredHeight);
            specListEntry.bindTo(categoriesPanel);
            gui.add(categoriesPanel);

            specList.addListener(ListSelectionEvent.class, (event, widget) -> {
                categoriesPanel.categoryList.deselect();
                return WidgetEventResult.CONTINUE_PROCESSING;
            });

            for(Map.Entry<String, Object> categoryEntry : config.valueMap().entrySet()) {
                if (!(categoryEntry.getValue() instanceof UnmodifiableConfig)) {
                    continue;
                }

                String category = categoryEntry.getKey();
                String categoryComment = getCategoryComment(spec, category);

                UnmodifiableConfig categoryOptions = (UnmodifiableConfig) categoryEntry.getValue();

                CategoryListEntry categoryListEntry = new CategoryListEntry(category, columnWidths);
                if(categoryComment.length() > 0) {
                    categoryListEntry.setTooltipLines(new StringTextComponent(categoryComment));
                }
                categoriesPanel.categoryList.addListEntry(categoryListEntry);

                SettingsPanel settingsPanel = new SettingsPanel(columnWidths, desiredHeight);
                categoryListEntry.bindTo(settingsPanel);
                gui.add(settingsPanel);

                for(Map.Entry<String, Object> optionEntry : categoryOptions.valueMap().entrySet()) {
                    if(!(optionEntry.getValue() instanceof  ForgeConfigSpec.ValueSpec)) {
                        continue;
                    }
                    ForgeConfigSpec.ValueSpec valueSpec = (ForgeConfigSpec.ValueSpec) optionEntry.getValue();
                    String optionKey = optionEntry.getKey();
                    String comment = valueSpec.getComment();

                    if(!(spec.getValues().get(Arrays.asList(category, optionKey)) instanceof ForgeConfigSpec.ConfigValue)) {
                        continue;
                    }

                    ForgeConfigSpec.ConfigValue value = spec.getValues().get(Arrays.asList(category, optionKey));
                    SettingListEntry settingListEntry = new SettingListEntry(optionKey, comment, value, valueSpec.getDefault(), (columnWidths*2)-10);
                    settingsPanel.settingsList.addListEntry(settingListEntry);
                    //Logz.info("-> key={}, comment={}, value={}", optionKey, comment, value.get());
                }

            }
        }

        return gui;
    }
}
