package com.magenta.mc.client.android.smoke.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;

import com.magenta.mc.client.android.smoke.R;
import com.magenta.mc.client.android.smoke.workflow.WorkflowConfiguration;
import com.magenta.mc.client.android.ui.TaskListObserver;
import com.magenta.mc.client.android.ui.table_info.DetailsViewGroupController;
import com.magenta.mc.client.android.ui.table_info.renderers.DetailRenderer;

/**

 */

/**
 * This is superclass for activity with workflow(change status) buttons in the middle of the screen and info above and below.
 * <p/>
 * Can be used in apps without subtasks, just use SubtaskActivity<TaskClass, TaskClass>
 *
 * @param <TASK>    Task class(Job in Echo, Run in Maxoptra)
 * @param <SUBTASK> Subtask class(Stop in Echo, Job in Maxoptra)
 * @author Petr Popov
 *         Created 23.05.12 15:51
 */
public abstract class SubtaskActivity<TASK, SUBTASK> extends SmokeActivity implements TaskListObserver<TASK> {

    protected WorkflowConfiguration<TASK, SUBTASK> workflowConfiguration;
    private TableLayout topDetailsTable;
    private TableLayout bottomDetailsTable;
    private Button button1;
    private Button button2;
    private Button button3;
    private DetailsViewGroupController topDetailsController;
    private DetailsViewGroupController bottomDetailsController;

    protected abstract WorkflowConfiguration getWorkflowConfiguration();

    @Override
    public void initActivity(Bundle savedInstanceState) {
        setContentView(R.layout.screen_subtask);
        topDetailsTable = (TableLayout) findViewById(R.id.subtask_top_details);
        bottomDetailsTable = (TableLayout) findViewById(R.id.subtask_bottom_details);
        button1 = (Button) findViewById(R.id.subtask_button1);
        button2 = (Button) findViewById(R.id.subtask_button2);
        button3 = (Button) findViewById(R.id.subtask_button3);

        workflowConfiguration = getWorkflowConfiguration();

        topDetailsController = new DetailsViewGroupController(topDetailsTable, getTopDetailsRenderers());
        bottomDetailsController = new DetailsViewGroupController(bottomDetailsTable, getBottomDetailsRenderers());

        setTaskAndSubtask();
        registerTaskListObserver();
    }

    @Override
    protected void onDestroy() {
        unregisterTaskListObserver();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        unregisterTaskListObserver();
        super.onBackPressed();
    }

    public void refreshButtonsAndInfo() {
        refreshButtonsAndInfo(null);
    }

    public void refreshButtonsAndInfo(final Runnable futureRunnable) {
        getDelegate().getHandler().post(new Runnable() {
            @Override
            public void run() {
                setTitle(getCaption());
                workflowConfiguration.apply(getTask(), getSubTask(), button1, button2, button3);
                topDetailsController.fill(getSubTask());
                bottomDetailsController.fill(getSubTask());
                if (futureRunnable != null) {
                    futureRunnable.run();
                }
            }
        });
    }

    public abstract SUBTASK getSubTask();

    public abstract TASK getTask();

    protected abstract String getCaption();

    protected abstract void setTaskAndSubtask();

    protected abstract void registerTaskListObserver();

    protected abstract void unregisterTaskListObserver();

    protected DetailRenderer[] getTopDetailsRenderers() {
        return new DetailRenderer[]{
                /* Example
                new TextRenderer<TextView, SUBTASK>(R.id.top_example1, new int[]{R.id.top_example1_row, R.id.top_example1_separator,}, false) {
                    @Override
                    public String extractText(SUBTASK source) {
                        return "extracted from source field";
                    }
                },
                new TextRenderer<TextView, SUBTASK>(R.id.top_example2, new int[]{R.id.top_example2_row}, false) {
                    @Override
                    public String extractText(SUBTASK source) {
                        return "extracted from source field";
                    }
                }
                */
        };
    }

    protected DetailRenderer[] getBottomDetailsRenderers() {
        return new DetailRenderer[]{
                /* Example
                new TextRenderer<TextView, SUBTASK>(R.id.bottom_example1, new int[]{R.id.bottom_example1_row, R.id.bottom_example1_separator,}, false) {
                    @Override
                    public String extractText(SUBTASK source) {
                        return "extracted from source field";
                    }
                },
                new TextRenderer<TextView, SUBTASK>(R.id.bottom_example2, new int[]{R.id.bottom_example2_row}, false) {
                    @Override
                    public String extractText(SUBTASK source) {
                        return "extracted from source field";
                    }
                }
                */
        };
    }


}
