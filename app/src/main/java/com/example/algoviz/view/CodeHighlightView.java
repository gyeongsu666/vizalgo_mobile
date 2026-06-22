package com.example.algoviz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.algoviz.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeHighlightView extends View {

    // ── Colors (init에서 리소스로부터 로딩) ──────────────────────────
    private int colorBg, colorLineHl, colorLineNum, colorLineNumActive;
    private int colorCode, colorActiveCode;

    private float lineHeight;
    private float paddingLeft;
    private float lineNumWidth;
    private float paddingRight;

    private final Paint bgPaint   = new Paint();
    private final Paint hlPaint   = new Paint();
    private final Paint numPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint codePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<String> codeLines;
    private final Set<Integer> activeLineSet = new HashSet<>();

    public CodeHighlightView(Context context) {
        super(context); init(context);
    }
    public CodeHighlightView(Context context, AttributeSet attrs) {
        super(context, attrs); init(context);
    }
    public CodeHighlightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init(context);
    }

    private float sp(Context ctx, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                ctx.getResources().getDisplayMetrics());
    }

    private float dp(Context ctx, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ctx.getResources().getDisplayMetrics());
    }

    private void init(Context context) {
        lineHeight   = sp(context, 26f);
        paddingLeft  = dp(context, 12f);
        lineNumWidth = dp(context, 48f);
        paddingRight = dp(context, 16f);

        // 리소스에서 색상 로딩
        colorBg           = ContextCompat.getColor(context, R.color.code_bg);
        colorLineHl       = ContextCompat.getColor(context, R.color.code_line_hl);
        colorLineNum      = ContextCompat.getColor(context, R.color.code_line_num);
        colorLineNumActive= ContextCompat.getColor(context, R.color.code_line_num_active);
        colorCode         = ContextCompat.getColor(context, R.color.code_text);
        colorActiveCode   = ContextCompat.getColor(context, R.color.code_text_active);

        bgPaint.setColor(colorBg);
        hlPaint.setColor(colorLineHl);

        numPaint.setTextSize(sp(context, 13f));
        numPaint.setColor(colorLineNum);
        numPaint.setTypeface(Typeface.MONOSPACE);

        codePaint.setTextSize(sp(context, 15f));
        codePaint.setTypeface(Typeface.MONOSPACE);
    }

    public void setCode(List<String> lines) {
        this.codeLines = lines;
        requestLayout();
        invalidate();
    }

    public void setActiveLines(int[] activeLines) {
        activeLineSet.clear();
        if (activeLines != null) {
            for (int l : activeLines) activeLineSet.add(l);
        }
        invalidate();
    }

    /** 가장 긴 줄의 픽셀 너비 계산 */
    private float maxContentWidth() {
        if (codeLines == null || codeLines.isEmpty()) return 400;
        float max = 0;
        for (String line : codeLines) {
            float w = paddingLeft + lineNumWidth + codePaint.measureText(line) + paddingRight;
            if (w > max) max = w;
        }
        return max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int lines    = codeLines != null ? codeLines.size() : 0;
        int desiredW = (int) maxContentWidth();
        int desiredH = (int) (lines * lineHeight + lineHeight);
        setMeasuredDimension(
                resolveSize(desiredW, widthMeasureSpec),
                resolveSize(desiredH, heightMeasureSpec)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        if (codeLines == null) return;

        float baseline = lineHeight * 0.72f;

        for (int i = 0; i < codeLines.size(); i++) {
            float y = i * lineHeight;
            boolean active = activeLineSet.contains(i + 1);

            if (active) {
                canvas.drawRect(0, y, getWidth(), y + lineHeight, hlPaint);
            }

            numPaint.setColor(active ? colorLineNumActive : colorLineNum);
            canvas.drawText(String.valueOf(i + 1),
                    paddingLeft, y + baseline, numPaint);

            codePaint.setColor(active ? colorActiveCode : colorCode);
            canvas.drawText(codeLines.get(i),
                    paddingLeft + lineNumWidth, y + baseline, codePaint);
        }
    }
}
