package com.example.algoviz.algorithm;

import com.example.algoviz.model.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 앱에서 제공하는 모든 알고리즘을 등록·관리하는 저장소(Repository) 클래스.
 * <p>
 * 정적 초기화 블록에서 12개 알고리즘을 {@link LinkedHashMap}에 등록 순서대로 보관한다.
 * 각 알고리즘은 한국어·영어 이중 언어 데이터를 가지며,
 * {@link com.example.algoviz.model.Algorithm#getTitle()} 등 로케일 인식 getter로 조회된다.
 * </p>
 */
public class AlgorithmRegistry {

    private static final Map<String, Algorithm> ALGORITHMS = new LinkedHashMap<>();

    static {
        register("bubbleSort",
                "버블 정렬", "Bubble Sort",
                "정렬", "Sorting",
                "ALGO · 정렬", "ALGO · Sort",
                "인접한 두 원소를 반복 비교하며 큰 값을 뒤로 보내는 방식입니다.",
                "Repeatedly compares adjacent elements and moves the larger one backward.",
                "숫자 목록 (예: 7, 2, 9, 1, 4)", "Array of numbers (e.g. 7, 2, 9, 1, 4)",
                "7, 2, 9, 1, 4",
                Arrays.asList(
                        "def bubble_sort(arr):",
                        "    n = len(arr)",
                        "    for i in range(n):",
                        "        swapped = False",
                        "        for j in range(n - i - 1):",
                        "            if arr[j] > arr[j + 1]:",
                        "                arr[j], arr[j + 1] = arr[j + 1], arr[j]",
                        "                swapped = True",
                        "        if not swapped:",
                        "            break",
                        "    return arr"
                ),
                "sequence", "O(n)", "O(n²)", "O(n²)", "O(1)",
                "소규모 배열, 거의 정렬된 데이터, 교육 목적",
                "Small arrays, nearly sorted data, educational use");

        register("selectionSort",
                "선택 정렬", "Selection Sort",
                "정렬", "Sorting",
                "ALGO · 정렬", "ALGO · Sort",
                "남아 있는 구간에서 최솟값을 골라 앞쪽에 놓는 방식입니다.",
                "Finds the minimum in the remaining range and places it at the front.",
                "숫자 목록 (예: 7, 2, 9, 1, 4)", "Array of numbers (e.g. 7, 2, 9, 1, 4)",
                "7, 2, 9, 1, 4",
                Arrays.asList(
                        "def selection_sort(arr):",
                        "    n = len(arr)",
                        "    for i in range(n):",
                        "        min_index = i",
                        "        for j in range(i + 1, n):",
                        "            if arr[j] < arr[min_index]:",
                        "                min_index = j",
                        "        arr[i], arr[min_index] = arr[min_index], arr[i]",
                        "    return arr"
                ),
                "sequence", "O(n²)", "O(n²)", "O(n²)", "O(1)",
                "소규모 배열, 메모리 제약 환경",
                "Small arrays, memory-constrained environments");

        register("insertionSort",
                "삽입 정렬", "Insertion Sort",
                "정렬", "Sorting",
                "ALGO · 정렬", "ALGO · Sort",
                "이미 정렬된 구간에 새 값을 끼워 넣는 방식입니다.",
                "Inserts each new value into the correct position in the sorted section.",
                "숫자 목록 (예: 7, 2, 9, 1, 4)", "Array of numbers (e.g. 7, 2, 9, 1, 4)",
                "7, 2, 9, 1, 4",
                Arrays.asList(
                        "def insertion_sort(arr):",
                        "    for i in range(1, len(arr)):",
                        "        key = arr[i]",
                        "        j = i - 1",
                        "        while j >= 0 and arr[j] > key:",
                        "            arr[j + 1] = arr[j]",
                        "            j -= 1",
                        "        arr[j + 1] = key",
                        "    return arr"
                ),
                "sequence", "O(n)", "O(n²)", "O(n²)", "O(1)",
                "거의 정렬된 배열, 온라인 정렬",
                "Nearly sorted arrays, online sorting");

        register("quickSort",
                "퀵 정렬", "Quick Sort",
                "정렬", "Sorting",
                "ALGO · 정렬", "ALGO · Sort",
                "피벗을 기준으로 작은 값과 큰 값을 나누는 분할 방식입니다.",
                "Partitions elements around a pivot into smaller and larger groups recursively.",
                "숫자 목록 (예: 7, 2, 9, 1, 4, 6)", "Array of numbers (e.g. 7, 2, 9, 1, 4, 6)",
                "7, 2, 9, 1, 4, 6",
                Arrays.asList(
                        "def quick_sort(arr, left, right):",
                        "    if left >= right: return",
                        "    pivot = arr[right]",
                        "    i = left",
                        "    for j in range(left, right):",
                        "        if arr[j] <= pivot:",
                        "            arr[i], arr[j] = arr[j], arr[i]",
                        "            i += 1",
                        "    arr[i], arr[right] = arr[right], arr[i]",
                        "    quick_sort(arr, left, i - 1)",
                        "    quick_sort(arr, i + 1, right)"
                ),
                "sequence", "O(n log n)", "O(n log n)", "O(n²)", "O(log n)",
                "대용량 데이터, 일반적 정렬, 표준 라이브러리",
                "Large datasets, general-purpose sorting, standard libraries");

        register("mergeSort",
                "병합 정렬", "Merge Sort",
                "정렬", "Sorting",
                "ALGO · 정렬", "ALGO · Sort",
                "구간을 계속 반으로 나눈 뒤 정렬된 결과를 다시 합치는 방식입니다.",
                "Splits the array in half repeatedly, then merges sorted halves back together.",
                "숫자 목록 (예: 7, 2, 9, 1, 4, 6)", "Array of numbers (e.g. 7, 2, 9, 1, 4, 6)",
                "7, 2, 9, 1, 4, 6",
                Arrays.asList(
                        "def merge_sort(arr, left, right):",
                        "    if left >= right: return",
                        "    mid = (left + right) // 2",
                        "    merge_sort(arr, left, mid)",
                        "    merge_sort(arr, mid + 1, right)",
                        "    merged = []",
                        "    i, j = left, mid + 1",
                        "    while i <= mid and j <= right:",
                        "        if arr[i] <= arr[j]: merged.append(arr[i]); i += 1",
                        "        else: merged.append(arr[j]); j += 1",
                        "    merged += arr[i:mid+1] + arr[j:right+1]",
                        "    arr[left:right+1] = merged"
                ),
                "sequence", "O(n log n)", "O(n log n)", "O(n log n)", "O(n)",
                "대용량 데이터, 안정 정렬이 필요한 경우, 외부 정렬",
                "Large datasets, stable sort required, external sorting");

        register("linearSearch",
                "순차 탐색", "Linear Search",
                "탐색", "Search",
                "ALGO · 탐색", "ALGO · Search",
                "처음부터 끝까지 하나씩 확인하는 가장 단순한 탐색입니다.",
                "The simplest search: checks each element one by one from start to end.",
                "목록 | 찾을 값 (예: 4, 7, 1, 9 | 7)", "List | target (e.g. 4, 7, 1, 9 | 7)",
                "4, 7, 1, 9, 5 | 9",
                Arrays.asList(
                        "def linear_search(arr, target):",
                        "    for index, value in enumerate(arr):",
                        "        if value == target:",
                        "            return index",
                        "        # 불일치 → 다음으로",
                        "    return -1  # 없음"
                ),
                "search", "O(1)", "O(n)", "O(n)", "O(1)",
                "정렬되지 않은 배열, 소규모 데이터",
                "Unsorted arrays, small datasets");

        register("binarySearch",
                "이진 탐색", "Binary Search",
                "탐색", "Search",
                "ALGO · 탐색", "ALGO · Search",
                "정렬된 데이터에서 범위를 절반씩 줄이며 찾는 방식입니다.",
                "Finds the target in sorted data by halving the search range each step.",
                "목록 | 찾을 값 (예: 1, 3, 5, 7 | 5)", "List | target (e.g. 1, 3, 5, 7 | 5)",
                "7, 2, 9, 1, 4 | 4",
                Arrays.asList(
                        "def binary_search(arr, target):",
                        "    low, high = 0, len(arr) - 1",
                        "    while low <= high:",
                        "        mid = (low + high) // 2",
                        "        if arr[mid] == target:",
                        "            return mid",
                        "        elif arr[mid] < target:",
                        "            low = mid + 1",
                        "        else:",
                        "            high = mid - 1",
                        "    return -1"
                ),
                "search", "O(1)", "O(log n)", "O(log n)", "O(1)",
                "정렬된 배열, 사전 검색, DB 인덱스",
                "Sorted arrays, dictionary lookups, DB indexes");

        register("dfs",
                "DFS", "DFS",
                "그래프", "Graph",
                "ALGO · 그래프", "ALGO · Graph",
                "한 경로를 끝까지 내려간 뒤 되돌아오는 깊이 우선 탐색입니다.",
                "Explores as far down one path as possible before backtracking.",
                "간선 목록 | 시작 노드 (예: A-B, A-C, B-D | A)", "Edge list | start (e.g. A-B, A-C, B-D | A)",
                "A-B, A-C, B-D, D-E | A",
                Arrays.asList(
                        "def dfs(graph, start):",
                        "    stack = [start]",
                        "    visited = []",
                        "    while stack:",
                        "        node = stack.pop()",
                        "        if node in visited: continue",
                        "        visited.append(node)",
                        "        for next_node in reversed(graph[node]):",
                        "            if next_node not in visited:",
                        "                stack.append(next_node)",
                        "    return visited"
                ),
                "graph", "O(V+E)", "O(V+E)", "O(V+E)", "O(V)",
                "미로 탐색, 위상 정렬, 사이클 검출",
                "Maze solving, topological sort, cycle detection");

        register("bfs",
                "BFS", "BFS",
                "그래프", "Graph",
                "ALGO · 그래프", "ALGO · Graph",
                "가까운 노드부터 차례로 방문하는 너비 우선 탐색입니다.",
                "Visits all neighbors at the current level before exploring deeper.",
                "간선 목록 | 시작 노드 (예: A-B, A-C, B-D | A)", "Edge list | start (e.g. A-B, A-C, B-D | A)",
                "A-B, A-C, B-D, D-E | A",
                Arrays.asList(
                        "def bfs(graph, start):",
                        "    queue = deque([start])",
                        "    visited = [start]",
                        "    while queue:",
                        "        node = queue.popleft()",
                        "        for next_node in graph[node]:",
                        "            if next_node not in visited:",
                        "                visited.append(next_node)",
                        "                queue.append(next_node)",
                        "    return visited"
                ),
                "graph", "O(V+E)", "O(V+E)", "O(V+E)", "O(V)",
                "최단 경로, 레벨 탐색, 소셜 네트워크",
                "Shortest path, level traversal, social networks");

        register("preorder",
                "Preorder", "Preorder",
                "순회", "Traversal",
                "ALGO · 트리", "ALGO · Tree",
                "루트 → 왼쪽 → 오른쪽 순으로 방문하는 트리 순회입니다.",
                "Visits nodes in root → left → right order.",
                "레벨 순회 값 (예: A, B, C, D, E, null, F)", "Level-order values (e.g. A, B, C, D, E, null, F)",
                "A, B, C, D, E, null, F",
                Arrays.asList(
                        "def preorder(root):",
                        "    stack = [0]",
                        "    result = []",
                        "    while stack:",
                        "        idx = stack.pop()",
                        "        if idx >= len(values) or values[idx] is None: continue",
                        "        result.append(values[idx])",
                        "        stack.append(idx * 2 + 2)  # right",
                        "        stack.append(idx * 2 + 1)  # left",
                        "    return result"
                ),
                "tree", "O(n)", "O(n)", "O(n)", "O(h)",
                "트리 복사, 직렬화, 수식 트리 평가",
                "Tree copying, serialization, expression tree evaluation");

        register("inorder",
                "Inorder", "Inorder",
                "순회", "Traversal",
                "ALGO · 트리", "ALGO · Tree",
                "왼쪽 → 루트 → 오른쪽 순으로 방문하는 트리 순회입니다.",
                "Visits nodes in left → root → right order.",
                "레벨 순회 값 (예: A, B, C, D, E, null, F)", "Level-order values (e.g. A, B, C, D, E, null, F)",
                "A, B, C, D, E, null, F",
                Arrays.asList(
                        "def inorder(values):",
                        "    stack, result, idx = [], [], 0",
                        "    while stack or (idx < len(values)",
                        "                    and values[idx] is not None):",
                        "        while idx < len(values) and values[idx]:",
                        "            stack.append(idx)",
                        "            idx = idx * 2 + 1",
                        "        idx = stack.pop()",
                        "        result.append(values[idx])",
                        "        idx = idx * 2 + 2",
                        "    return result"
                ),
                "tree", "O(n)", "O(n)", "O(n)", "O(h)",
                "BST 정렬 출력, 중위 식 평가",
                "BST sorted output, infix expression evaluation");

        register("postorder",
                "Postorder", "Postorder",
                "순회", "Traversal",
                "ALGO · 트리", "ALGO · Tree",
                "왼쪽 → 오른쪽 → 루트 순으로 방문하는 트리 순회입니다.",
                "Visits nodes in left → right → root order.",
                "레벨 순회 값 (예: A, B, C, D, E, null, F)", "Level-order values (e.g. A, B, C, D, E, null, F)",
                "A, B, C, D, E, null, F",
                Arrays.asList(
                        "def postorder(values):",
                        "    stack = [0]",
                        "    result = []",
                        "    while stack:",
                        "        idx = stack.pop()",
                        "        if idx >= len(values) or values[idx] is None: continue",
                        "        result.append(values[idx])",
                        "        stack.append(idx * 2 + 1)  # left",
                        "        stack.append(idx * 2 + 2)  # right",
                        "    return result[::-1]"
                ),
                "tree", "O(n)", "O(n)", "O(n)", "O(h)",
                "트리 삭제, 후위 식 평가, 메모리 해제",
                "Tree deletion, postfix expression evaluation, memory deallocation");
    }

    private static void register(
            String key,
            String titleKr, String titleEn,
            String categoryKr, String categoryEn,
            String badgeKr, String badgeEn,
            String descriptionKr, String descriptionEn,
            String inputHintKr, String inputHintEn,
            String example,
            List<String> code, String renderer,
            String best, String avg, String worst, String space,
            String useCasesKr, String useCasesEn) {
        ALGORITHMS.put(key, new Algorithm(key,
                titleKr, titleEn, categoryKr, categoryEn, badgeKr, badgeEn,
                descriptionKr, descriptionEn, inputHintKr, inputHintEn,
                example, code, renderer,
                best, avg, worst, space,
                useCasesKr, useCasesEn));
    }

    /** 등록된 모든 알고리즘을 삽입 순서대로 반환한다. */
    public static List<Algorithm> getAll() {
        return new ArrayList<>(ALGORITHMS.values());
    }

    /**
     * 키에 해당하는 알고리즘을 반환한다.
     *
     * @param key 알고리즘 고유 식별자 (예: "bubbleSort")
     * @return 해당 알고리즘 또는 {@code null}
     */
    public static Algorithm get(String key) {
        return ALGORITHMS.get(key);
    }

    /**
     * 카테고리로 필터링한 알고리즘 목록을 반환한다.
     * <p>
     * {@code category}가 {@code null}이면 전체 목록을 반환한다.
     * {@link Algorithm#getCategory()}는 로케일 인식 getter이므로
     * 한국어·영어 모드 모두에서 탭 레이블과 정확히 일치한다.
     * </p>
     *
     * @param category 카테고리 문자열 또는 전체 조회를 위한 {@code null}
     * @return 필터링된 알고리즘 목록
     */
    public static List<Algorithm> getByCategory(String category) {
        List<Algorithm> result = new ArrayList<>();
        for (Algorithm a : ALGORITHMS.values()) {
            if (category == null || a.getCategory().equals(category)) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * 제목·설명·카테고리에서 키워드를 검색한다.
     * <p>
     * 대소문자 구분 없이 검색하며, 현재 로케일 기준의 텍스트와 비교한다.
     * </p>
     *
     * @param query 검색어 (빈 문자열이면 빈 목록 반환)
     * @return 검색어를 포함하는 알고리즘 목록
     */
    public static List<Algorithm> search(String query) {
        List<Algorithm> result = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Algorithm a : ALGORITHMS.values()) {
            if (a.getTitle().toLowerCase().contains(lower)
                    || a.getDescription().toLowerCase().contains(lower)
                    || a.getCategory().toLowerCase().contains(lower)) {
                result.add(a);
            }
        }
        return result;
    }

    /** 북마크된 알고리즘만 반환 */
    public static List<Algorithm> getBookmarked() {
        List<Algorithm> result = new ArrayList<>();
        for (Algorithm a : ALGORITHMS.values()) {
            if (a.isBookmarked()) result.add(a);
        }
        return result;
    }
}
