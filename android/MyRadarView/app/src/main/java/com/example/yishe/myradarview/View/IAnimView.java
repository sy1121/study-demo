package com.example.yishe.myradarview.View;

/**
 * Created by yishe on 2017/9/13.
 */

public interface IAnimView {
    void SetVisibility(int visibility);
        // 开始播放动画
        void startAnimation();
        // 开始进入淡入淡出动画
    void fadeOutAnimation();
    // 立刻停止动画
    void stopAnimation();
    void recycle();
}
