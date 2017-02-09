package com.magenta.mc.client.android.workflow;

public interface ActionButtonConfig {

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