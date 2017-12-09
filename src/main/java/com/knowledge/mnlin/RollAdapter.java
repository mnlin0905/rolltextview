package com.knowledge.mnlin;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.knowledge.mnlin.rolltextview.R;

import java.util.List;
import java.util.Locale;

/**
 * function : 信息滚动布局的适配器
 *
 * @author ACChain
 * @date 2017/12/7
 */
public class RollAdapter<T extends Object> extends RecyclerView.Adapter<RollAdapter.ViewHolder> {
    private Context context;
    private List list;
    private AdapterView.OnItemClickListener onItemClickListener;
    private boolean orderVisible;
    private String jump;
    private boolean jumpVisible;

    @LayoutRes
    private int layoutResource= R.layout.item_roll_text_view;
    private int direction;
    private int appearCount;

    public RollAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    /**
     * 负责为item创建视图
     */
    @Override
    public RollAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RollAdapter.ViewHolder(LayoutInflater.from(context).inflate(layoutResource, parent, false));
    }

    /**
     * 负责将数据绑定到item的视图上
     */
    @Override
    public void onBindViewHolder(final RollAdapter.ViewHolder holder, int position) {
        holder.mTvCenter.setText(list.get(position).toString());
        holder.mTvLeft.setVisibility(orderVisible ? View.VISIBLE : View.GONE);
        holder.mTvRight.setVisibility(jumpVisible ? View.VISIBLE : View.GONE);
        if (orderVisible) {
            holder.mTvLeft.setText(String.format(Locale.CHINA, "%d.", getCorrectPosition(position) + 1));
        }
        if (jumpVisible) {
            holder.mTvRight.setText(jump);
        }
    }

    /**
     * 返回int类型的最大值，默认一直滚动（直到int最大值的次数）
     */
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    /**
     * 设置序号是否可见
     */
    RollAdapter<T> setOrderVisible(boolean orderVisible) {
        this.orderVisible = orderVisible;
        return this;
    }

    /**
     * 设置文本最后的内容，供点击跳转
     */
    RollAdapter<T> setEndText(String jump, boolean jumpVisible) {
        this.jump = jump;
        this.jumpVisible = jumpVisible;
        return this;
    }

    /**
     * 设置点击事件
     */
    RollAdapter<T> setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    /**
     * 设置layout文件，可自定义，但必须有三个变量存在
     */
    void setLayoutResource(int layoutResource) {
        this.layoutResource = layoutResource;
    }

    /**
     * 从recyclerview获取必要的参数信息
     */
    void setDirectionAndAppearCount(int direction, int appearCount) {
        this.direction = direction;
        this.appearCount = appearCount;
    }

    /**
     * 获取正确的position
     */
    private int getCorrectPosition(int position) {
        if (direction == 0 || direction == 3) {
            position = position % (list.size() - appearCount);
        } else {
            if (position < appearCount) {
                position = list.size() - 2 * appearCount + position;
            } else {
                position = position - appearCount;
            }
        }
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView mTvLeft;
        AppCompatTextView mTvCenter;
        AppCompatTextView mTvRight;

        private ViewHolder(View itemView) {
            super(itemView);
            mTvLeft = itemView.findViewById(R.id.tv_left);
            mTvCenter = itemView.findViewById(R.id.tv_center);
            mTvRight = itemView.findViewById(R.id.tv_right);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getCorrectPosition(getAdapterPosition());
                    onItemClickListener.onItemClick(null, itemView, position, position);
                }
            });
        }
    }
}