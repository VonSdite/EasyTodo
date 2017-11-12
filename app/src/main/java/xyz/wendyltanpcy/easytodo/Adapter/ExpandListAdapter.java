package xyz.wendyltanpcy.easytodo.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

/**
 * Created by Wendy on 2017/11/12.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {

    public String[] groupStrings = {"事件分类"};
    public String[][] childStrings = {
            {"生活", "工作", "紧急", "私人"},
    };

    private TodoEvent mEvent;

    private List<ChildViewHolder> hdList = new ArrayList<>();

    public void setEvent(TodoEvent event){
        mEvent = event;
    }


    //        获取分组的个数
    @Override
    public int getGroupCount() {
        return groupStrings.length;
    }

    //        获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return childStrings[groupPosition].length;
    }

    //        获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return groupStrings[groupPosition];
    }

    //        获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childStrings[groupPosition][childPosition];
    }

    //        获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //        获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //        分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }
    //        获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_list_label_view, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = convertView.findViewById(R.id.label_expand_group);
            groupViewHolder.category = convertView.findViewById(R.id.label_category);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groupStrings[groupPosition]);
        String labelString=null;

        if (mEvent.getEventCategory()==0)
            labelString = " 暂时无分类！";
        else{
            switch (mEvent.getEventCategory()){
                case 1:
                    labelString = "生活";
                    break;
                case 2:
                    labelString = "工作";
                    break;
                case 3:
                    labelString = "紧急";
                    break;

                case 4:
                    labelString = "私人";
                    break;
            }
        }
        groupViewHolder.category.setText(labelString);

        return convertView;
    }




    //        获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_list_item_view, parent, false);
            childViewHolder = new ChildViewHolder();

            childViewHolder.tvTitle = convertView.findViewById(R.id.label_expand_item);
            childViewHolder.isChoosen = convertView.findViewById(R.id.label_item_is_current_check);

            //init the view when first enter the expandlist
            if (mEvent.getEventCategory()==childPosition+1)
                childViewHolder.isChoosen.setVisibility(View.VISIBLE);

            else{
                childViewHolder.isChoosen.setVisibility(View.INVISIBLE);
            }


            //setting the listener for storing category
            childViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEvent.setEventCategory(childPosition+1);
                    childViewHolder.isChoosen.setVisibility(View.VISIBLE);
                    //make sure that only one category is checked
                    for (ChildViewHolder holder:hdList){
                        if (!holder.equals(childViewHolder)){
                            holder.isChoosen.setVisibility(View.INVISIBLE);
                        }
                    }
                    mEvent.save();
                    Log.i("TAG","position:"+(childPosition+1));
                }
            });

            convertView.setTag(childViewHolder);
            hdList.add(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(childStrings[groupPosition][childPosition]);
        return convertView;
    }

    //        指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tvTitle;
        TextView category;
    }
    static class ChildViewHolder {
        TextView tvTitle;
        ImageView isChoosen;
    }

}
