package codetail.widget.green;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Those widgets temporary are not allowed to define in XML
 *
 * This widgets create 2 panes on the screen, on
 */
@SuppressLint("ViewConstructor")
public class DualPaneLayout extends ViewGroup{
    /**
     * Number of panes, kn + 1
     */
    public static final int K = 2;

    private int firstPaneWeight;
    private int secondPaneWeight;

    public DualPaneLayout(Context context, Bundle options) {
        super(context);
    }

    public void setRatio(int fWeight, int sWeight){
        firstPaneWeight = fWeight;
        secondPaneWeight = sWeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int length = getChildCount() / K;
        final int offsetRight = getResources().getDimensionPixelOffset(R.dimen.configuration_widget_padding);
        int heightUsed = getPaddingTop();
        for(int index = 0; index < length; index++){
            View child1 = getChildAt(K * index);
            View child2 = getChildAt(K * index + 1);

            LayoutParams params = (LayoutParams) child1.getLayoutParams();
            if(params.heading){
                measureChildWithMargins(child1, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            }else {
                int propW = width / (firstPaneWeight + secondPaneWeight);
                measureChildWithMargins(child1, widthMeasureSpec,
                        (propW * secondPaneWeight), heightMeasureSpec, heightUsed);
                measureChildWithMargins(child2, widthMeasureSpec,
                        (propW * firstPaneWeight) + offsetRight, heightMeasureSpec, heightUsed);
            }

            heightUsed += Math.max(child1.getMeasuredHeight(), child2.getMeasuredHeight());
        }
        heightUsed += getPaddingBottom();
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(heightUsed, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int length = getChildCount() / K;
        final int offsetRight = getResources().getDimensionPixelOffset(R.dimen.configuration_widget_padding);
        int contentTop = getPaddingTop();
        for(int index = 0; index < length; index++){
            View child1 = getChildAt(K * index);
            View child2 = getChildAt(K * index + 1);

            LayoutParams params = (LayoutParams) child1.getLayoutParams();
            if(params.heading){
                child1.layout(0, contentTop, child1.getMeasuredWidth(),
                        contentTop + child1.getMeasuredHeight());
            }else {
                int c1width = child1.getMeasuredWidth();
                child1.layout(0, contentTop, c1width, contentTop + child1.getMeasuredHeight());
                c1width += offsetRight;
                child2.layout(c1width, contentTop, c1width + child2.getMeasuredWidth(),
                        contentTop + child2.getMeasuredHeight());
            }

            contentTop += Math.max(child1.getMeasuredHeight(), child2.getMeasuredHeight());
        }
    }

    /**
     * Returns rows count in the layout
     */
    public int getIndexesCount(){
        return getChildCount() / K;
    }

    /**
     * if {@code index} equals {@link #getIndexesCount()}, it means that it wants
     * add new row and we will inflate them, otherwise do nothing
     *
     * Note, newly inflated views are preventing {@link #requestLayout()}
     * so after batch setting you need to invoke it
     *
     * @param index index of element to rebind
     * @param id layout id to inflate
     */
    public void inflateIfNeeded(int index, @LayoutRes int... id){
        if(id.length > K){
            throw new IllegalArgumentException("Cannot inflate more than " + K + " views");
        }

        if(index == getIndexesCount()){
            LayoutInflater factory = LayoutInflater.from(getContext());
            for(int i = 0; i < K; i++){
                View child = factory.inflate(id[i], this, false);
                addViewInLayout(child, getChildCount(), child.getLayoutParams(), true);
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    static class LayoutParams extends MarginLayoutParams{

        boolean heading = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
