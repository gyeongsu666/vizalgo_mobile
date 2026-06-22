package com.example.algoviz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.algoviz.R;
import com.example.algoviz.model.AlgorithmStep;
import com.example.algoviz.model.AlgorithmStep.NodeState;
import com.example.algoviz.model.AlgorithmStep.EdgeState;

import java.util.List;

public class VisualizationView extends View {

    // ── Colors (init에서 리소스로부터 로딩) ──────────────────────────
    private int colorDefault, colorCompare, colorSwap, colorSorted;
    private int colorFound, colorVisited, colorFrontier, colorCurrent;
    private int colorHighlight, colorHighlightBorder;
    private int colorText, colorBg, colorEdge, colorEdgeTrav;
    private int colorSearchRange, colorLowHigh, colorMid;
    private int colorIndexText, colorSearchText, colorSearchCellInactive;

    private final Paint barPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);

    // dp/sp 스케일 (init에서 설정)
    private float dp;
    private float sp;

    private AlgorithmStep currentStep;
    private String renderer = "sequence";

    // 그래프/트리 가상 캔버스 크기
    private static final float VIRTUAL_W = 640f;
    private static final float VIRTUAL_H = 400f;

    public VisualizationView(Context context) {
        super(context); init(context);
    }
    public VisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs); init(context);
    }
    public VisualizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init(context);
    }

    private void init(Context context) {
        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                context.getResources().getDisplayMetrics());
        sp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1,
                context.getResources().getDisplayMetrics());

        // 리소스에서 색상 로딩
        colorDefault            = ContextCompat.getColor(context, R.color.vis_default);
        colorCompare            = ContextCompat.getColor(context, R.color.vis_compare);
        colorSwap               = ContextCompat.getColor(context, R.color.vis_swap);
        colorSorted             = ContextCompat.getColor(context, R.color.vis_sorted);
        colorFound              = ContextCompat.getColor(context, R.color.vis_found);
        colorVisited            = ContextCompat.getColor(context, R.color.vis_visited);
        colorFrontier           = ContextCompat.getColor(context, R.color.vis_frontier);
        colorCurrent            = ContextCompat.getColor(context, R.color.vis_current);
        colorHighlight          = ContextCompat.getColor(context, R.color.vis_highlight);
        colorHighlightBorder    = ContextCompat.getColor(context, R.color.vis_highlight_border);
        colorText               = ContextCompat.getColor(context, R.color.vis_text);
        colorBg                 = ContextCompat.getColor(context, R.color.vis_bg);
        colorEdge               = ContextCompat.getColor(context, R.color.vis_edge);
        colorEdgeTrav           = ContextCompat.getColor(context, R.color.vis_edge_traversed);
        colorSearchRange        = ContextCompat.getColor(context, R.color.vis_search_range);
        colorLowHigh            = ContextCompat.getColor(context, R.color.vis_low_high);
        colorMid                = ContextCompat.getColor(context, R.color.vis_mid);
        colorIndexText          = ContextCompat.getColor(context, R.color.vis_index_text);
        colorSearchText         = ContextCompat.getColor(context, R.color.vis_search_text);
        colorSearchCellInactive = ContextCompat.getColor(context, R.color.vis_search_cell_inactive);

        textPaint.setColor(colorText);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        edgePaint.setStrokeWidth(2.5f * dp);
        edgePaint.setStyle(Paint.Style.STROKE);

        bgPaint.setColor(colorBg);
    }

    public void setStep(AlgorithmStep step, String renderer) {
        this.currentStep = step;
        this.renderer = renderer;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        if (currentStep == null) return;

        switch (renderer) {
            case "sequence": drawSequence(canvas); break;
            case "search":   drawSearch(canvas);   break;
            case "graph":    drawGraph(canvas);    break;
            case "tree":     drawTree(canvas);     break;
        }
    }

    // ────────────────────────────────────────────────────────────
    //  배열 막대 렌더러 (sequence)
    // ────────────────────────────────────────────────────────────

    private void drawSequence(Canvas canvas) {
        float[] values = currentStep.getValues();
        if (values == null || values.length == 0) return;

        int[] compare = currentStep.getCompareIndices();
        int[] swap    = currentStep.getSwapIndices();
        int[] sorted  = currentStep.getSortedIndices();

        float max = 0;
        for (float v : values) if (v > max) max = v;
        if (max == 0) max = 1;

        int n = values.length;
        float padding = 10 * dp;
        float totalW  = getWidth() - padding * 2;
        float barW    = totalW / n - 4 * dp;
        float maxH    = getHeight() - 20 * dp;

        for (int i = 0; i < n; i++) {
            int color = barColor(i, compare, swap, sorted);
            barPaint.setColor(color);

            float h      = (values[i] / max) * maxH;
            float left   = padding + i * (barW + 4 * dp);
            float top    = getHeight() - h - 16 * dp;
            float right  = left + barW;
            float bottom = getHeight() - 16 * dp;

            canvas.drawRoundRect(new RectF(left, top, right, bottom), 4 * dp, 4 * dp, barPaint);

            // 값 레이블
            float tSize = Math.max(8 * sp, Math.min(13 * sp, barW * 0.5f));
            textPaint.setTextSize(tSize);
            textPaint.setColor(colorText);
            String label = fmt(values[i]);
            float tw = textPaint.measureText(label);
            if (barW > 10 * dp) {
                canvas.drawText(label, left + (barW - tw) / 2, bottom - 4 * dp, textPaint);
            }

            // 인덱스
            textPaint.setTextSize(10 * sp);
            textPaint.setColor(colorIndexText);
            canvas.drawText(String.valueOf(i),
                    left + barW / 2 - textPaint.measureText(String.valueOf(i)) / 2,
                    getHeight() - 3 * dp, textPaint);
        }
    }

    private int barColor(int i, int[] compare, int[] swap, int[] sorted) {
        if (swap    != null) for (int s : swap)    if (s == i) return colorSwap;
        if (compare != null) for (int c : compare) if (c == i) return colorCompare;
        if (sorted  != null) for (int s : sorted)  if (s == i) return colorSorted;
        return colorDefault;
    }

    // ────────────────────────────────────────────────────────────
    //  탐색 렌더러 (search)
    // ────────────────────────────────────────────────────────────

    private void drawSearch(Canvas canvas) {
        float[] values = currentStep.getValues();
        if (values == null || values.length == 0) return;

        int found = currentStep.getFoundIndex();
        int lo    = currentStep.getLow();
        int hi    = currentStep.getHigh();
        int mid   = currentStep.getMid();

        int n = values.length;
        float cellW = (float)(getWidth() - 24 * dp) / n;
        float cellH = 14 * dp;
        float top   = getHeight() / 2f - cellH / 2 - 8 * dp;

        for (int i = 0; i < n; i++) {
            float left = 12 * dp + i * cellW;
            RectF rect = new RectF(left, top, left + cellW - 3 * dp, top + cellH);

            if      (i == found && found >= 0)                  barPaint.setColor(colorFound);
            else if (i == mid)                                   barPaint.setColor(colorMid);
            else if (lo >= 0 && hi >= 0 && i >= lo && i <= hi)  barPaint.setColor(colorSearchRange);
            else                                                 barPaint.setColor(colorSearchCellInactive);
            canvas.drawRoundRect(rect, 3 * dp, 3 * dp, barPaint);

            textPaint.setTextSize(8 * sp);
            textPaint.setColor((i == found || i == mid) ? colorText : colorSearchText);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            String label = fmt(values[i]);
            float tw = textPaint.measureText(label);
            canvas.drawText(label,
                    left + (cellW - 3 * dp - tw) / 2,
                    top + cellH / 2 + 3 * sp, textPaint);

            textPaint.setTextSize(7 * sp);
            textPaint.setColor(colorIndexText);
            textPaint.setTypeface(Typeface.DEFAULT);
            canvas.drawText(String.valueOf(i),
                    left + (cellW - 3 * dp) / 2 - textPaint.measureText(String.valueOf(i)) / 2,
                    top + cellH + 7 * dp, textPaint);
        }

        drawPointer(canvas, lo,  "lo",  colorLowHigh, top, cellW);
        drawPointer(canvas, hi,  "hi",  colorLowHigh, top, cellW);
        if (mid >= 0) drawPointer(canvas, mid, "mid", colorMid, top - 7 * dp, cellW);
    }

    private void drawPointer(Canvas canvas, int idx, String label, int color,
                              float top, float cellW) {
        if (idx < 0) return;
        float cx = 12 * dp + idx * cellW + (cellW - 3 * dp) / 2;
        textPaint.setColor(color);
        textPaint.setTextSize(6 * sp);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(label, cx - textPaint.measureText(label) / 2, top - 3 * dp, textPaint);
    }

    // ────────────────────────────────────────────────────────────
    //  그래프 렌더러 (graph)
    // ────────────────────────────────────────────────────────────

    private void drawGraph(Canvas canvas) {
        List<NodeState> nodes = currentStep.getNodes();
        List<EdgeState> edges = currentStep.getEdges();
        if (nodes == null || nodes.isEmpty()) return;

        float scaleX = getWidth()  / VIRTUAL_W;
        float scaleY = getHeight() / VIRTUAL_H;
        float scale  = Math.min(scaleX, scaleY) * 0.92f;

        float offsetX = (getWidth()  - VIRTUAL_W * scale) / 2f;
        float offsetY = (getHeight() - VIRTUAL_H * scale) / 2f;

        // 간선
        for (EdgeState e : edges) {
            NodeState from = findNode(nodes, e.from);
            NodeState to   = findNode(nodes, e.to);
            if (from == null || to == null) continue;
            edgePaint.setColor(e.traversed ? colorEdgeTrav : colorEdge);
            edgePaint.setStrokeWidth(e.active ? 4 * dp : 2.5f * dp);
            canvas.drawLine(
                    from.x * scale + offsetX, from.y * scale + offsetY,
                    to.x   * scale + offsetX, to.y   * scale + offsetY,
                    edgePaint);
        }

        // 노드 반지름: 노드 수에 따라 동적 조절
        int nodeCount = nodes.size();
        float r = Math.min(22 * dp, getWidth() * 0.5f / (nodeCount + 1));
        r = Math.max(r, 14 * dp);

        float labelSp = Math.min(14 * sp, r * 0.55f);

        for (NodeState n : nodes) {
            float cx = n.x * scale + offsetX;
            float cy = n.y * scale + offsetY;

            // 채우기
            nodePaint.setStyle(Paint.Style.FILL);
            if      (n.current)  nodePaint.setColor(colorCurrent);
            else if (n.visited)  nodePaint.setColor(colorVisited);
            else if (n.frontier) nodePaint.setColor(colorFrontier);
            else                 nodePaint.setColor(colorDefault);
            canvas.drawCircle(cx, cy, r, nodePaint);

            // 테두리
            nodePaint.setStyle(Paint.Style.STROKE);
            nodePaint.setStrokeWidth(1.5f * dp);
            nodePaint.setColor(colorText);
            canvas.drawCircle(cx, cy, r, nodePaint);

            // 레이블
            textPaint.setTextSize(labelSp);
            textPaint.setColor(colorText);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            float tw = textPaint.measureText(n.label);
            canvas.drawText(n.label, cx - tw / 2, cy + labelSp * 0.38f, textPaint);

            // 방문 순서
            if (n.order != null) {
                textPaint.setTextSize(9 * sp);
                textPaint.setColor(colorText);
                canvas.drawText(String.valueOf(n.order), cx + r - 8 * dp, cy - r + 8 * dp, textPaint);
            }
        }
    }

    // ────────────────────────────────────────────────────────────
    //  트리 렌더러 (tree)
    // ────────────────────────────────────────────────────────────

    private void drawTree(Canvas canvas) {
        List<NodeState> nodes = currentStep.getNodes();
        List<EdgeState> edges = currentStep.getEdges();
        if (nodes == null || nodes.isEmpty()) return;

        float scaleX = getWidth()  / VIRTUAL_W;
        float scaleY = getHeight() / VIRTUAL_H;
        float scale  = Math.min(scaleX, scaleY) * 0.92f;

        float offsetX = (getWidth()  - VIRTUAL_W * scale) / 2f;
        float offsetY = (getHeight() - VIRTUAL_H * scale) / 2f;

        // 간선
        edgePaint.setColor(colorEdge);
        edgePaint.setStrokeWidth(2 * dp);
        edgePaint.setStyle(Paint.Style.STROKE);
        for (EdgeState e : edges) {
            NodeState from = findNode(nodes, e.from);
            NodeState to   = findNode(nodes, e.to);
            if (from == null || to == null) continue;
            canvas.drawLine(
                    from.x * scale + offsetX, from.y * scale + offsetY,
                    to.x   * scale + offsetX, to.y   * scale + offsetY,
                    edgePaint);
        }

        // 노드 반지름
        int nodeCount = nodes.size();
        float r = Math.min(20 * dp, getWidth() * 0.5f / (nodeCount + 1));
        r = Math.max(r, 13 * dp);

        float labelSp = Math.min(14 * sp, r * 0.60f);

        for (NodeState n : nodes) {
            float cx = n.x * scale + offsetX;
            float cy = n.y * scale + offsetY;

            nodePaint.setStyle(Paint.Style.FILL);
            nodePaint.setColor(n.highlight ? colorHighlight : colorDefault);
            canvas.drawCircle(cx, cy, r, nodePaint);

            // 현재 노드 강조 테두리
            if (n.highlight) {
                nodePaint.setStyle(Paint.Style.STROKE);
                nodePaint.setStrokeWidth(2.5f * dp);
                nodePaint.setColor(colorHighlightBorder);
                canvas.drawCircle(cx, cy, r + 2 * dp, nodePaint);
            }

            textPaint.setTextSize(labelSp);
            textPaint.setColor(colorText);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            float tw = textPaint.measureText(n.label);
            canvas.drawText(n.label, cx - tw / 2, cy + labelSp * 0.38f, textPaint);
        }
    }

    // ────────────────────────────────────────────────────────────

    private NodeState findNode(List<NodeState> nodes, String id) {
        for (NodeState n : nodes) if (n.id.equals(id)) return n;
        return null;
    }

    private String fmt(float v) {
        if (v == (int) v) return String.valueOf((int) v);
        return String.valueOf(v);
    }
}
