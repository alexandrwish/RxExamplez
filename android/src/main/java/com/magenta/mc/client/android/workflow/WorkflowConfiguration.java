package com.magenta.mc.client.android.workflow;

import android.view.View;
import android.widget.Button;

import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.ui.theme.Theme;
import com.magenta.mc.client.android.ui.theme.ThemeManageable;
import com.magenta.mc.client.android.ui.theme.ThemeManager;
import com.magenta.mc.client.android.util.AndroidResourceManager;
import com.magenta.mc.client.android.util.ResourceManager;

public abstract class WorkflowConfiguration<TASK, SUBTASK> implements ThemeManageable {

    private ThemeManager themeManager;

    public void apply(TASK task, SUBTASK subtask, Button... buttons) {
        ActionButtonConfig[] configs = getConfigsForEntity(task, subtask);
        int visible = 0;
        for (int i = 0; i < 3; i++) {
            visible += configs[i].isVisible() ? 1 : 0;
        }
        for (int i = 0; i < 3; i++) {
            buttons[i].setVisibility(configs[i].isVisible() ? View.VISIBLE : View.GONE);
            if (configs[i].isVisible()) {
                String positions = null;
                switch (visible) {
                    case 1:
                        positions = ActionButtonConfig.POSITION_STRETCH;
                        break;
                    case 2:
                        positions = i == 0 ? ActionButtonConfig.POSITION_LEFT : ActionButtonConfig.POSITION_RIGHT;
                        break;
                    case 3:
                        if (i == 0) {
                            positions = ActionButtonConfig.POSITION_LEFT;
                        } else if (i == 1) {
                            positions = ActionButtonConfig.POSITION_CENTER;
                        } else if (i == 2) {
                            positions = ActionButtonConfig.POSITION_RIGHT;
                        }
                }
                int backgroundId;
                if (!ActionButtonConfig.POSITION_STRETCH.equals(positions)) {
                    String color = configs[i].getBackground().toString().toLowerCase();
                    String background = String.format("mc_action_button_%s_%s_bg", positions, color);
                    backgroundId = ((AndroidResourceManager) ResourceManager.getInstance()).getIdForResource(background, "drawable");
                } else { //todo add support for colored screen-width buttons
                    if (configs[i].getBackground().toString().equalsIgnoreCase("pink")) {
                        backgroundId = R.drawable.mc_button_pink_bg;
                    } else {
                        backgroundId = getDefaultButtonBackground();
                    }
                }
                buttons[i].setBackgroundResource(backgroundId);
                buttons[i].setText(configs[i].getText());
                buttons[i].setEnabled(true);
                final WorkflowAction action = configs[i].getAction();
                if (action != null) {
                    buttons[i].setOnClickListener(new View.OnClickListener() {

                        private long lastClick;

                        public void onClick(View view) {
                            long now = System.currentTimeMillis();
                            if (now - lastClick < 500) {
                                return;
                            }
                            lastClick = now;
                            view.setEnabled(false);
                            action.onClick(view);
                        }
                    });
                }
            }
        }
    }

    private int getDefaultButtonBackground() {
        Theme currentTheme = getCurrentTheme();
        int backgroundId;
        switch (currentTheme) {
            case NIGHT:
                backgroundId = R.drawable.mc_button_default_bg;
                break;
            case DAY:
                backgroundId = R.drawable.mc_button_light_bg;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return backgroundId;
    }

    private Theme getCurrentTheme() {
        ThemeManager themeManager = getThemeManager();
        return themeManager != null ? themeManager.getCurrentTheme() : ThemeManager.DEFAULT_THEME;
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public abstract ActionButtonConfig[] getConfigsForEntity(TASK task, SUBTASK subtask);
}