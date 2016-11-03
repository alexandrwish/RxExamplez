package com.magenta.mc.client.android.smoke.workflow;

/**
 * @autor Petr Popov
 * Created 21.05.12 9:36
 */
public interface ActionButtonConfig<J, S> {

    String POSITION_STRETCH = "stretch";
    String POSITION_LEFT = "left";
    String POSITION_CENTER = "center";
    String POSITION_RIGHT = "right";

    WorkflowAction getAction();

    String getText();

    ActionButtonColor getBackground();

    void setBackground(ActionButtonColor color);

    boolean isDisabled();

    boolean isVisible();
}
