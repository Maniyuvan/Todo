package com.example.maniy.todo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ActionMode actionMode;
    private Activity activity;
    private Context context;
    private List<Tasks> tasksList;

    public TaskAdapter(Context context, List<Tasks> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }



    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list,null);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder taskViewHolder, int i) {
        Tasks tasks = tasksList.get(i);
        taskViewHolder.task_title.setText(tasks.getTitle());
        taskViewHolder.task_date.setText(tasks.getDate());
        taskViewHolder.task_priority.setText(tasks.getPriority());

        taskViewHolder.task_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(actionMode != null)
                    return false;
                actionMode = activity.startActionMode(actionCallBack);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView task_title, task_date,task_priority;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_title = itemView.findViewById(R.id.task_name);
            task_date = itemView.findViewById(R.id.task_date);
            task_priority = itemView.findViewById(R.id.task_priority);
            task_title.setTypeface(Typeface.DEFAULT_BOLD);
            task_title.setTextColor(itemView.getResources().getColor(R.color.black));
//            if(task_priority.equals("Normal")){
//                task_priority.setBackgroundColor(itemView.getResources().getColor(R.color.normal));
//            }


        }
    }

    private ActionMode.Callback actionCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.main_menu,menu);
            actionMode.setTitle("selected");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.delete:
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    actionMode.finish();

                case R.id.done:
                    Toast.makeText(context,"Done",Toast.LENGTH_SHORT).show();
                    actionMode.finish();

                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };
}
