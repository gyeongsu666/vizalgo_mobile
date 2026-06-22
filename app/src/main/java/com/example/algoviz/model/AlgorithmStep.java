package com.example.algoviz.model;

import java.util.List;

/**
 * 알고리즘 시각화의 단일 실행 단계를 나타내는 모델 클래스.
 * <p>
 * 알고리즘 유형에 따라 사용되는 필드가 다르다.
 * <ul>
 *   <li><b>정렬 계열</b> : values, compareIndices, swapIndices, sortedIndices</li>
 *   <li><b>탐색 계열</b> : values, low, high, mid, foundIndex, target</li>
 *   <li><b>그래프/트리 계열</b> : nodes, edges, frontierChips, visitedChips</li>
 * </ul>
 * {@code message}와 {@code activeLines}(코드 하이라이트 줄 번호)는 모든 유형에 공통이다.
 * </p>
 */
public class AlgorithmStep {
    // 배열/정렬 계열
    private float[] values;
    private int[] compareIndices;
    private int[] swapIndices;
    private int[] sortedIndices;

    // 탐색 계열
    private int foundIndex = -1;
    private int low = -1;
    private int high = -1;
    private int mid = -1;
    private float target = Float.NaN;

    // 그래프/트리 계열
    private List<NodeState> nodes;
    private List<EdgeState> edges;
    private List<String> frontierChips;
    private String frontierLabel;
    private List<String> visitedChips;

    // 공통
    private String message;
    private int[] activeLines;

    // ──────────────────────────────────────────────────────────────

    public static class NodeState {
        public final String id;
        public final String label;
        public final float x;
        public final float y;
        public final boolean current;
        public final boolean visited;
        public final boolean frontier;
        public final boolean highlight;
        public final Integer order;

        public NodeState(String id, String label, float x, float y,
                         boolean current, boolean visited, boolean frontier,
                         boolean highlight, Integer order) {
            this.id = id;
            this.label = label;
            this.x = x;
            this.y = y;
            this.current = current;
            this.visited = visited;
            this.frontier = frontier;
            this.highlight = highlight;
            this.order = order;
        }
    }

    public static class EdgeState {
        public final String from;
        public final String to;
        public final boolean active;
        public final boolean traversed;

        public EdgeState(String from, String to, boolean active, boolean traversed) {
            this.from = from;
            this.to = to;
            this.active = active;
            this.traversed = traversed;
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Getters / Setters

    public float[] getValues() { return values; }
    public void setValues(float[] values) { this.values = values; }

    public int[] getCompareIndices() { return compareIndices; }
    public void setCompareIndices(int[] compareIndices) { this.compareIndices = compareIndices; }

    public int[] getSwapIndices() { return swapIndices; }
    public void setSwapIndices(int[] swapIndices) { this.swapIndices = swapIndices; }

    public int[] getSortedIndices() { return sortedIndices; }
    public void setSortedIndices(int[] sortedIndices) { this.sortedIndices = sortedIndices; }

    public int getFoundIndex() { return foundIndex; }
    public void setFoundIndex(int foundIndex) { this.foundIndex = foundIndex; }

    public int getLow() { return low; }
    public void setLow(int low) { this.low = low; }

    public int getHigh() { return high; }
    public void setHigh(int high) { this.high = high; }

    public int getMid() { return mid; }
    public void setMid(int mid) { this.mid = mid; }

    public float getTarget() { return target; }
    public void setTarget(float target) { this.target = target; }

    public List<NodeState> getNodes() { return nodes; }
    public void setNodes(List<NodeState> nodes) { this.nodes = nodes; }

    public List<EdgeState> getEdges() { return edges; }
    public void setEdges(List<EdgeState> edges) { this.edges = edges; }

    public List<String> getFrontierChips() { return frontierChips; }
    public void setFrontierChips(List<String> frontierChips) { this.frontierChips = frontierChips; }

    public String getFrontierLabel() { return frontierLabel; }
    public void setFrontierLabel(String frontierLabel) { this.frontierLabel = frontierLabel; }

    public List<String> getVisitedChips() { return visitedChips; }
    public void setVisitedChips(List<String> visitedChips) { this.visitedChips = visitedChips; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int[] getActiveLines() { return activeLines; }
    public void setActiveLines(int[] activeLines) { this.activeLines = activeLines; }
}
