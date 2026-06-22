package com.example.algoviz.algorithm;

import com.example.algoviz.model.AlgorithmStep;
import com.example.algoviz.model.AlgorithmStep.NodeState;
import com.example.algoviz.model.AlgorithmStep.EdgeState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 알고리즘별 시각화 단계({@link com.example.algoviz.model.AlgorithmStep}) 목록을 생성하는 유틸리티 클래스.
 * <p>
 * 각 {@code buildXxx()} 정적 메서드는 입력 데이터를 받아 알고리즘의 실제 실행 순서대로
 * 스텝 목록을 반환하며, 각 스텝에 활성 코드 라인({@code activeLines})을 포함한다.
 * 시각화 렌더러({@link com.example.algoviz.view.VisualizationView})가 이 목록을 재생한다.
 * </p>
 */
public class StepBuilder {

    // ────────────────────────────────────────────────────────────
    //  Helper: 배열 스텝 생성
    // ────────────────────────────────────────────────────────────

    private static AlgorithmStep arrayStep(float[] arr, int[] compare, int[] swap,
                                            int[] sorted, String msg, int... lines) {
        AlgorithmStep s = new AlgorithmStep();
        s.setValues(Arrays.copyOf(arr, arr.length));
        s.setCompareIndices(compare);
        s.setSwapIndices(swap);
        s.setSortedIndices(sorted);
        s.setMessage(msg);
        s.setActiveLines(lines);
        return s;
    }

    private static int[] range(int from, int to) {
        int[] r = new int[to - from];
        for (int i = 0; i < r.length; i++) r[i] = from + i;
        return r;
    }

    private static float[] copy(float[] arr) {
        return Arrays.copyOf(arr, arr.length);
    }

    // ────────────────────────────────────────────────────────────
    //  버블 정렬
    // ────────────────────────────────────────────────────────────

    /**
     * 버블 정렬 시각화 단계를 생성한다.
     * <p>
     * 인접 두 원소를 반복 비교하며, 교환이 발생하지 않으면 조기 종료한다(최적화).
     * 각 단계에 비교/교환/정렬 완료 인덱스와 활성 코드 라인을 포함한다.
     * </p>
     *
     * @param input 정렬할 원본 배열 (내부에서 복사하므로 원본은 수정되지 않음)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildBubbleSort(float[] input) {
        float[] arr = copy(input);
        int n = arr.length;
        List<AlgorithmStep> steps = new ArrayList<>();
        steps.add(arrayStep(arr, null, null, null, "초기 배열 상태입니다.", 1, 2));

        for (int i = 0; i < n; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                int[] sorted = range(n - i, n);
                steps.add(arrayStep(arr, new int[]{j, j + 1}, null, sorted,
                        arr[j] + "와 " + arr[j + 1] + "를 비교합니다.", 5, 6));
                if (arr[j] > arr[j + 1]) {
                    float tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                    swapped = true;
                    steps.add(arrayStep(arr, null, new int[]{j, j + 1}, sorted,
                            "두 값을 교환했습니다.", 7, 8));
                }
            }
            if (!swapped) {
                steps.add(arrayStep(arr, null, null, range(0, n),
                        "교환이 없어 조기 종료합니다.", 9, 10));
                break;
            }
        }
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  선택 정렬
    // ────────────────────────────────────────────────────────────

    /**
     * 선택 정렬 시각화 단계를 생성한다.
     * <p>
     * 정렬되지 않은 구간에서 최솟값을 선택해 맨 앞 원소와 교환하는 과정을 기록한다.
     * </p>
     *
     * @param input 정렬할 원본 배열 (내부에서 복사하므로 원본은 수정되지 않음)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildSelectionSort(float[] input) {
        float[] arr = copy(input);
        int n = arr.length;
        List<AlgorithmStep> steps = new ArrayList<>();
        steps.add(arrayStep(arr, null, null, null, "초기 배열 상태입니다.", 1));

        for (int i = 0; i < n; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                steps.add(arrayStep(arr, new int[]{minIdx, j}, null, range(0, i),
                        arr[minIdx] + "와 " + arr[j] + "를 비교합니다.", 5, 6));
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                    steps.add(arrayStep(arr, new int[]{minIdx}, null, range(0, i),
                            "최솟값 후보: " + arr[minIdx], 7));
                }
            }
            if (i != minIdx) {
                float tmp = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = tmp;
            }
            steps.add(arrayStep(arr, null, i != minIdx ? new int[]{i, minIdx} : new int[]{},
                    range(0, i + 1), i + "번 위치에 최솟값을 놓았습니다.", 8));
        }
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  삽입 정렬
    // ────────────────────────────────────────────────────────────

    /**
     * 삽입 정렬 시각화 단계를 생성한다.
     * <p>
     * 미정렬 구간의 원소를 정렬된 구간의 적절한 위치에 삽입하는 과정을 기록한다.
     * </p>
     *
     * @param input 정렬할 원본 배열 (내부에서 복사)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildInsertionSort(float[] input) {
        float[] arr = copy(input);
        List<AlgorithmStep> steps = new ArrayList<>();
        steps.add(arrayStep(arr, null, null, new int[]{0}, "첫 번째 원소를 정렬된 구간으로 봅니다.", 2));

        for (int i = 1; i < arr.length; i++) {
            float key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                steps.add(arrayStep(arr, null, new int[]{j, j + 1}, range(0, i + 1),
                        arr[j + 1] + "를 오른쪽으로 이동합니다.", 5, 6));
                j--;
            }
            arr[j + 1] = key;
            steps.add(arrayStep(arr, new int[]{j + 1}, null, range(0, i + 1),
                    key + "를 " + (j + 1) + "번 위치에 삽입했습니다.", 8));
        }
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  퀵 정렬
    // ────────────────────────────────────────────────────────────

    /**
     * 퀵 정렬 시각화 단계를 생성한다.
     * <p>
     * 피벗을 기준으로 분할 정복하며 재귀 호출({@code quickSort})로 각 단계를 수집한다.
     * </p>
     *
     * @param input 정렬할 원본 배열 (내부에서 복사)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildQuickSort(float[] input) {
        float[] arr = copy(input);
        List<AlgorithmStep> steps = new ArrayList<>();
        steps.add(arrayStep(arr, null, null, null, "초기 배열 상태입니다.", 1));
        quickSort(arr, 0, arr.length - 1, steps);
        steps.add(arrayStep(arr, null, null, range(0, arr.length),
                "퀵 정렬이 완료되었습니다.", 9));
        return steps;
    }

    private static void quickSort(float[] arr, int left, int right, List<AlgorithmStep> steps) {
        if (left >= right) return;
        float pivot = arr[right];
        int i = left;
        steps.add(arrayStep(arr, new int[]{right}, null, null,
                "피벗 " + pivot + "를 선택합니다.", 3));
        for (int j = left; j < right; j++) {
            steps.add(arrayStep(arr, new int[]{j, right}, null, null,
                    arr[j] + "와 피벗 " + pivot + "를 비교합니다.", 4));
            if (arr[j] <= pivot) {
                float tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
                steps.add(arrayStep(arr, null, new int[]{i, j}, null,
                        arr[i] + "를 피벗 왼쪽으로 보냅니다.", 5));
                i++;
            }
        }
        float tmp = arr[i]; arr[i] = arr[right]; arr[right] = tmp;
        steps.add(arrayStep(arr, null, new int[]{i, right}, null,
                "피벗 " + arr[i] + "를 제자리로 이동합니다.", 3));
        quickSort(arr, left, i - 1, steps);
        quickSort(arr, i + 1, right, steps);
    }

    // ────────────────────────────────────────────────────────────
    //  병합 정렬
    // ────────────────────────────────────────────────────────────

    /**
     * 병합 정렬 시각화 단계를 생성한다.
     * <p>
     * 구간을 반으로 나누고 재귀 병합하며, 각 병합 완료 시 정렬된 구간을 표시한다.
     * </p>
     *
     * @param input 정렬할 원본 배열 (내부에서 복사)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildMergeSort(float[] input) {
        float[] arr = copy(input);
        List<AlgorithmStep> steps = new ArrayList<>();
        steps.add(arrayStep(arr, null, null, null, "초기 배열 상태입니다.", 1));
        mergeSort(arr, 0, arr.length - 1, steps);
        steps.add(arrayStep(arr, null, null, range(0, arr.length),
                "병합 정렬이 완료되었습니다.", 14));
        return steps;
    }

    private static void mergeSort(float[] arr, int left, int right, List<AlgorithmStep> steps) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        steps.add(arrayStep(arr, range(left, right + 1), null, null,
                left + "~" + right + " 구간을 둘로 나눕니다.", 3, 4, 5));
        mergeSort(arr, left, mid, steps);
        mergeSort(arr, mid + 1, right, steps);

        float[] merged = new float[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) merged[k++] = arr[i++];
            else merged[k++] = arr[j++];
        }
        while (i <= mid) merged[k++] = arr[i++];
        while (j <= right) merged[k++] = arr[j++];
        System.arraycopy(merged, 0, arr, left, merged.length);
        steps.add(arrayStep(arr, null, range(left, right + 1), null,
                left + "~" + right + " 구간을 병합했습니다.", 7, 8, 9, 10, 11, 12, 13));
    }

    // ────────────────────────────────────────────────────────────
    //  순차 탐색
    // ────────────────────────────────────────────────────────────

    /**
     * 순차 탐색(선형 탐색) 시각화 단계를 생성한다.
     * <p>
     * 배열을 앞에서부터 순서대로 확인하며, 목표값 발견 시 즉시 종료한다.
     * </p>
     *
     * @param values 탐색 대상 배열
     * @param target 찾을 목표값
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildLinearSearch(float[] values, float target) {
        List<AlgorithmStep> steps = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            AlgorithmStep s = arrayStep(values, new int[]{i}, null, null,
                    i + "번 인덱스 " + values[i] + "을 확인합니다.", 2, 3);
            s.setTarget(target);
            if (values[i] == target) {
                s.setFoundIndex(i);
                steps.add(s);
                AlgorithmStep found = arrayStep(values, null, null, null,
                        target + "을(를) 찾았습니다! (인덱스 " + i + ")", 4, 5);
                found.setFoundIndex(i);
                found.setTarget(target);
                steps.add(found);
                return steps;
            }
            steps.add(s);
        }
        AlgorithmStep notFound = arrayStep(values, null, null, null,
                target + "은(는) 목록에 없습니다.", 6);
        notFound.setTarget(target);
        steps.add(notFound);
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  이진 탐색
    // ────────────────────────────────────────────────────────────

    /**
     * 이진 탐색 시각화 단계를 생성한다.
     * <p>
     * 입력 배열을 정렬한 뒤 low·mid·high 포인터를 이동하며 탐색 과정을 기록한다.
     * 각 단계에 포인터 위치가 포함되어 렌더러가 색상으로 구분 표시한다.
     * </p>
     *
     * @param input  탐색 대상 배열 (내부에서 복사 후 정렬)
     * @param target 찾을 목표값
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildBinarySearch(float[] input, float target) {
        float[] values = copy(input);
        Arrays.sort(values);
        List<AlgorithmStep> steps = new ArrayList<>();
        int lo = 0, hi = values.length - 1;

        AlgorithmStep init = new AlgorithmStep();
        init.setValues(copy(values));
        init.setLow(lo); init.setHigh(hi); init.setMid(-1);
        init.setTarget(target);
        init.setMessage("정렬된 배열에서 탐색 시작합니다.");
        init.setActiveLines(new int[]{2});
        steps.add(init);

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            AlgorithmStep s = new AlgorithmStep();
            s.setValues(copy(values));
            s.setLow(lo); s.setHigh(hi); s.setMid(mid);
            s.setTarget(target);
            s.setMessage("중간값 " + values[mid] + "를 봅니다.");
            s.setActiveLines(new int[]{4});
            steps.add(s);

            if (values[mid] == target) {
                AlgorithmStep found = new AlgorithmStep();
                found.setValues(copy(values));
                found.setLow(lo); found.setHigh(hi); found.setMid(mid);
                found.setFoundIndex(mid); found.setTarget(target);
                found.setMessage(target + "을(를) 찾았습니다!");
                found.setActiveLines(new int[]{5, 6});
                steps.add(found);
                return steps;
            } else if (values[mid] < target) {
                lo = mid + 1;
                AlgorithmStep ns = new AlgorithmStep();
                ns.setValues(copy(values));
                ns.setLow(lo); ns.setHigh(hi); ns.setMid(mid); ns.setTarget(target);
                ns.setMessage("오른쪽 절반만 남깁니다.");
                ns.setActiveLines(new int[]{7, 8});
                steps.add(ns);
            } else {
                hi = mid - 1;
                AlgorithmStep ns = new AlgorithmStep();
                ns.setValues(copy(values));
                ns.setLow(lo); ns.setHigh(hi); ns.setMid(mid); ns.setTarget(target);
                ns.setMessage("왼쪽 절반만 남깁니다.");
                ns.setActiveLines(new int[]{9, 10});
                steps.add(ns);
            }
        }
        AlgorithmStep notFound = new AlgorithmStep();
        notFound.setValues(copy(values));
        notFound.setLow(lo); notFound.setHigh(hi); notFound.setTarget(target);
        notFound.setMessage(target + "은(는) 목록에 없습니다.");
        notFound.setActiveLines(new int[]{11});
        steps.add(notFound);
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  그래프 공통 유틸
    // ────────────────────────────────────────────────────────────

    public static class Edge {
        public final String from, to;
        public Edge(String from, String to) { this.from = from; this.to = to; }
    }

    private static Map<String, List<String>> buildAdjacency(List<Edge> edges) {
        Map<String, List<String>> adj = new LinkedHashMap<>();
        for (Edge e : edges) {
            adj.computeIfAbsent(e.from, k -> new ArrayList<>()).add(e.to);
            adj.computeIfAbsent(e.to, k -> new ArrayList<>()).add(e.from);
        }
        for (List<String> neighbors : adj.values()) Collections.sort(neighbors);
        return adj;
    }

    private static AlgorithmStep graphStep(List<Edge> edges, String start, String current,
                                            List<String> visited, List<String> frontier,
                                            String frontierLabel, List<String> visitedChips,
                                            String msg, int... lines) {
        // BFS 레이아웃으로 노드 위치 계산
        Map<String, List<String>> adj = buildAdjacency(edges);
        List<String> allLabels = new ArrayList<>(adj.keySet());
        Map<String, int[]> level = bfsLevel(allLabels, adj, start != null ? start : (allLabels.isEmpty() ? "" : allLabels.get(0)));

        List<NodeState> nodes = new ArrayList<>();
        for (String label : allLabels) {
            int[] pos = level.getOrDefault(label, new int[]{0, 0});
            int depth = pos[0], idx = pos[1], total = pos[2];
            float x = 580f / (total + 1) * (idx + 1) + 30;
            float y = 60 + depth * 90;
            nodes.add(new NodeState(label, label, x, y,
                    label.equals(current),
                    visited != null && visited.contains(label),
                    frontier != null && frontier.contains(label),
                    false, null));
        }

        List<EdgeState> edgeStates = new ArrayList<>();
        for (Edge e : edges) {
            edgeStates.add(new EdgeState(e.from, e.to,
                    false,
                    visited != null && visited.contains(e.from) && visited.contains(e.to)));
        }

        AlgorithmStep s = new AlgorithmStep();
        s.setNodes(nodes);
        s.setEdges(edgeStates);
        s.setFrontierChips(frontier != null ? new ArrayList<>(frontier) : new ArrayList<>());
        s.setFrontierLabel(frontierLabel);
        s.setVisitedChips(visitedChips != null ? new ArrayList<>(visitedChips) : new ArrayList<>());
        s.setMessage(msg);
        s.setActiveLines(lines);
        return s;
    }

    private static Map<String, int[]> bfsLevel(List<String> allLabels,
                                                 Map<String, List<String>> adj, String root) {
        Map<String, int[]> result = new LinkedHashMap<>();
        Map<Integer, List<String>> depthMap = new LinkedHashMap<>();
        Deque<String> q = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        if (!allLabels.contains(root)) root = allLabels.isEmpty() ? "" : allLabels.get(0);
        q.add(root); seen.add(root);
        Map<String, Integer> depthOf = new LinkedHashMap<>();
        depthOf.put(root, 0);
        while (!q.isEmpty()) {
            String node = q.poll();
            int d = depthOf.get(node);
            depthMap.computeIfAbsent(d, k -> new ArrayList<>()).add(node);
            for (String nb : adj.getOrDefault(node, Collections.emptyList())) {
                if (!seen.contains(nb)) { seen.add(nb); depthOf.put(nb, d + 1); q.add(nb); }
            }
        }
        for (Map.Entry<Integer, List<String>> entry : depthMap.entrySet()) {
            int depth = entry.getKey();
            List<String> row = entry.getValue();
            for (int i = 0; i < row.size(); i++) {
                result.put(row.get(i), new int[]{depth, i, row.size()});
            }
        }
        return result;
    }

    // ────────────────────────────────────────────────────────────
    //  DFS
    // ────────────────────────────────────────────────────────────

    /**
     * 깊이 우선 탐색(DFS) 시각화 단계를 생성한다.
     * <p>
     * 스택 기반으로 구현하며, 각 단계에 스택 상태·방문 순서·현재 노드를 포함한다.
     * </p>
     *
     * @param edges 그래프 간선 목록
     * @param start 탐색 시작 노드 레이블
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildDFS(List<Edge> edges, String start) {
        Map<String, List<String>> adj = buildAdjacency(edges);
        List<AlgorithmStep> steps = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        List<String> visited = new ArrayList<>();
        stack.push(start);

        steps.add(graphStep(edges, start, null, visited, new ArrayList<>(stack),
                "스택", visited, "스택에 시작 노드 " + start + "를 넣습니다.", 2, 3));

        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (visited.contains(node)) {
                steps.add(graphStep(edges, start, node, visited, new ArrayList<>(stack),
                        "스택", visited, node + "는 이미 방문 — 건너뜁니다.", 5, 6));
                continue;
            }
            visited.add(node);
            steps.add(graphStep(edges, start, node, visited, new ArrayList<>(stack),
                    "스택", visited, node + "를 방문합니다. 순서: " + String.join(" → ", visited), 7));

            List<String> neighbors = new ArrayList<>(adj.getOrDefault(node, Collections.emptyList()));
            Collections.reverse(neighbors);
            for (String nb : neighbors) {
                if (!visited.contains(nb)) {
                    stack.push(nb);
                }
            }
        }
        steps.add(graphStep(edges, start, null, visited, Collections.emptyList(),
                "스택", visited, "탐색 완료. 방문 순서: " + String.join(" → ", visited), 9));
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  BFS
    // ────────────────────────────────────────────────────────────

    /**
     * 너비 우선 탐색(BFS) 시각화 단계를 생성한다.
     * <p>
     * 큐 기반으로 구현하며, 각 단계에 큐 상태·방문 순서·현재 노드를 포함한다.
     * </p>
     *
     * @param edges 그래프 간선 목록
     * @param start 탐색 시작 노드 레이블
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildBFS(List<Edge> edges, String start) {
        Map<String, List<String>> adj = buildAdjacency(edges);
        List<AlgorithmStep> steps = new ArrayList<>();
        Deque<String> queue = new ArrayDeque<>();
        List<String> visited = new ArrayList<>();
        queue.add(start);
        visited.add(start);

        steps.add(graphStep(edges, start, start, visited, new ArrayList<>(queue),
                "큐", visited, start + "에서 시작합니다.", 2, 3));

        while (!queue.isEmpty()) {
            String node = queue.poll();
            steps.add(graphStep(edges, start, node, visited, new ArrayList<>(queue),
                    "큐", visited, node + "를 꺼내 이웃을 확인합니다.", 5));
            for (String nb : adj.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(nb)) {
                    visited.add(nb);
                    queue.add(nb);
                    steps.add(graphStep(edges, start, node, visited, new ArrayList<>(queue),
                            "큐", visited, nb + "를 큐에 추가했습니다.", 7, 8));
                }
            }
        }
        steps.add(graphStep(edges, start, null, visited, Collections.emptyList(),
                "큐", visited, "탐색 완료. 방문 순서: " + String.join(" → ", visited), 10));
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  이진 트리 공통 유틸 (레벨 순회 배열 기반)
    // ────────────────────────────────────────────────────────────

    private static AlgorithmStep treeStep(String[] values, List<Integer> highlight,
                                           String msg, int... lines) {
        List<NodeState> nodes = new ArrayList<>();
        List<EdgeState> edges = new ArrayList<>();
        Set<Integer> nonNull = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) nonNull.add(i);
        }
        float width = 640;
        float vGap = 90;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) continue;
            int level = (int) (Math.log(i + 1) / Math.log(2));
            int posInLevel = i - ((1 << level) - 1);
            int nodesInLevel = 1 << level;
            float x = width / (nodesInLevel + 1) * (posInLevel + 1);
            float y = 56 + level * vGap;
            boolean hi = highlight != null && highlight.contains(i);
            nodes.add(new NodeState(String.valueOf(i), values[i], x, y, hi, false, false, hi, null));
            if (i > 0) {
                int parent = (i - 1) / 2;
                if (nonNull.contains(parent)) {
                    edges.add(new EdgeState(String.valueOf(parent), String.valueOf(i), false, false));
                }
            }
        }
        AlgorithmStep s = new AlgorithmStep();
        s.setNodes(nodes);
        s.setEdges(edges);
        s.setMessage(msg);
        s.setActiveLines(lines);
        return s;
    }

    // ────────────────────────────────────────────────────────────
    //  Preorder 순회
    // ────────────────────────────────────────────────────────────

    /**
     * 이진 트리 전위 순회(Preorder) 시각화 단계를 생성한다.
     * <p>
     * 레벨 순회 배열({@code values})을 받아 루트→왼쪽→오른쪽 순으로 방문한다.
     * 스택을 사용해 반복적(iterative)으로 구현한다.
     * </p>
     *
     * @param values 레벨 순서로 나열된 트리 노드 레이블 배열 (빈 노드는 null)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildPreorder(String[] values) {
        List<AlgorithmStep> steps = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        steps.add(treeStep(values, Collections.emptyList(), "스택에 루트(인덱스 0)를 넣고 시작합니다.", 2));

        while (!stack.isEmpty()) {
            int idx = stack.pop();
            if (idx >= values.length || values[idx] == null) continue;
            result.add(idx);
            List<String> resultLabels = new ArrayList<>();
            for (int i : result) resultLabels.add(values[i]);
            steps.add(treeStep(values, Collections.singletonList(idx),
                    values[idx] + "를 방문합니다. 순서: " + String.join(" > ", resultLabels), 5));

            int right = idx * 2 + 2, left = idx * 2 + 1;
            if (right < values.length && values[right] != null) stack.push(right);
            if (left < values.length && values[left] != null) stack.push(left);
        }
        List<String> final_ = new ArrayList<>();
        for (int i : result) final_.add(values[i]);
        steps.add(treeStep(values, Collections.emptyList(), "순회 완료. 결과: " + String.join(" > ", final_), 8));
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  Inorder 순회
    // ────────────────────────────────────────────────────────────

    /**
     * 이진 트리 중위 순회(Inorder) 시각화 단계를 생성한다.
     * <p>
     * 왼쪽→루트→오른쪽 순으로 방문한다. BST에 적용하면 오름차순 방문 순서를 얻는다.
     * </p>
     *
     * @param values 레벨 순서로 나열된 트리 노드 레이블 배열 (빈 노드는 null)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildInorder(String[] values) {
        List<AlgorithmStep> steps = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        int idx = 0;
        steps.add(treeStep(values, Collections.emptyList(), "빈 스택과 idx=0(루트)으로 시작합니다.", 2, 3, 4));

        while (!stack.isEmpty() || (idx < values.length && values[idx] != null)) {
            while (idx < values.length && values[idx] != null) {
                stack.push(idx);
                steps.add(treeStep(values, Collections.singletonList(idx),
                        values[idx] + "를 스택에 쌓고 왼쪽으로 내려갑니다.", 6, 7));
                idx = idx * 2 + 1;
            }
            if (stack.isEmpty()) break;
            idx = stack.pop();
            result.add(idx);
            List<String> resultLabels = new ArrayList<>();
            for (int i : result) resultLabels.add(values[i]);
            steps.add(treeStep(values, Collections.singletonList(idx),
                    values[idx] + "를 방문합니다. 순서: " + String.join(" > ", resultLabels), 8, 9));
            idx = idx * 2 + 2;
        }
        List<String> final_ = new ArrayList<>();
        for (int i : result) final_.add(values[i]);
        steps.add(treeStep(values, Collections.emptyList(), "순회 완료. 결과: " + String.join(" > ", final_), 11));
        return steps;
    }

    // ────────────────────────────────────────────────────────────
    //  Postorder 순회
    // ────────────────────────────────────────────────────────────

    /**
     * 이진 트리 후위 순회(Postorder) 시각화 단계를 생성한다.
     * <p>
     * 왼쪽→오른쪽→루트 순으로 방문한다. 두 스택을 사용해 반복적으로 구현한다.
     * </p>
     *
     * @param values 레벨 순서로 나열된 트리 노드 레이블 배열 (빈 노드는 null)
     * @return 단계별 {@link AlgorithmStep} 목록
     */
    public static List<AlgorithmStep> buildPostorder(String[] values) {
        List<AlgorithmStep> steps = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        steps.add(treeStep(values, Collections.emptyList(), "스택에 루트를 넣고 시작합니다. 결과는 마지막에 뒤집습니다.", 2));

        while (!stack.isEmpty()) {
            int idx = stack.pop();
            if (idx >= values.length || values[idx] == null) continue;
            result.add(0, idx); // prepend (역순)
            steps.add(treeStep(values, Collections.singletonList(idx),
                    values[idx] + "를 역순 버퍼에 추가합니다.", 5));
            int left = idx * 2 + 1, right = idx * 2 + 2;
            if (left < values.length && values[left] != null) stack.push(left);
            if (right < values.length && values[right] != null) stack.push(right);
        }
        List<String> final_ = new ArrayList<>();
        for (int i : result) final_.add(values[i]);
        steps.add(treeStep(values, Collections.emptyList(), "순회 완료. 결과: " + String.join(" > ", final_), 8));
        return steps;
    }
}
