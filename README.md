----------

# Algorithm Design and Analysis Report

Name: Divyam Dubey

Course: COT5405 Analysis of Algorithms 

Assignment: Dynamic Programming Implementation

## Part 1: Weighted Approximate Common Substring

### 1.1 Algorithm Design

Optimization Function:

Let $S_1$ and $S_2$ be the two input strings. We define a 2D array $DP[i][j]$ representing the maximum score of a common substring ending exactly at index $i$ of $S_1$ and index $j$ of $S_2$.

Bellman Equation (Recurrence Relation):

The value of any cell $(i, j)$ depends only on the diagonal predecessor $(i-1, j-1)$, as gaps are not allowed.

$$DP[i][j] = \max \begin{cases} 0 \\ DP[i-1][j-1] + v(S_1[i], S_2[j]) \end{cases}$$

Where $v(a, b)$ is the weight $W_a$ if characters match, or the negative penalty $-\delta$ if they mismatch.

Justification for Correctness:

The problem exhibits Optimal Substructure. The best substring ending at $(i, j)$ is either the extension of the best substring ending at $(i-1, j-1)$ or a new substring starting at $(i, j)$ (represented by the reset to 0). By computing this for all $1 \le i \le M$ and $1 \le j \le N$, we explore all possible substring endings. The global maximum in the $DP$ table represents the score of the optimal substring.

**Optimal Solution Extraction:**

1.  During table construction, track the coordinates $(end_I, end_J)$ where the global maximum score occurs.
    
2.  Start at $(end_I, end_J)$ and backtrack diagonally to $(i-1, j-1)$.
    
3.  Add the character $S_1[i]$ to a buffer.
    
4.  Continue strictly diagonally until $DP[i][j] == 0$ (indicating the start of the substring).
    
5.  Reverse the buffer to obtain the string.
    

### 1.2 Complexity Analysis

-   **Time Complexity:** $O(M \times N)$
    
    -   **Justification:** We iterate through a nested loop of size $M$ (rows) and $N$ (columns). Inside the loop, we perform constant-time arithmetic operations ($O(1)$).
        
-   **Space Complexity:** $O(M \times N)$
    
    -   **Justification:** We require a 2D array of size $(M+1) \times (N+1)$ to store the scores for solution extraction.
        

### 1.3 Experimental Analysis

**Scenario 1: Constant Weight ($W=1$), High Penalty ($\delta=10$)**

-   **Observation:** The output typically results in an empty string ("None") or a very short substring (e.g., length 1 or 2).
    
-   **Analysis:** With a penalty of 10 and a reward of 1, a single mismatch requires 11 preceding consecutive matches to maintain a positive score ($11 - 10 > 0$). In uniform random data, the probability of 11 consecutive matches is statistically negligible. Therefore, the algorithm correctly "resets" to 0 frequently, preventing long substrings from forming.
    

**Scenario 2: Frequency-based Weights, Variable Penalty**

-   **Observation:**
    
    -   At **low penalties** ($\delta \approx 0.1$), the algorithm finds very long substrings with high scores. The low penalty allows the algorithm to "bridge" gaps between matching sections.
        
    -   At **high penalties** ($\delta \approx 12.7$), the algorithm finds very short substrings consisting only of high-value characters (e.g., 'E', 'T').
        
-   **Analysis:** This confirms the algorithm's sensitivity to the penalty parameter. As $\delta$ increases, the "cost" of extending a substring across a mismatch outweighs the benefit, forcing the solution to become more conservative and local.
    
### 1.4 Experimental Output:

The following screenshot demonstrates the program execution using Random Seed **42**. 

* **Scenario 1:** Shows the impact of high penalty (resulting in a short or empty substring). 
*  **Scenario 2:** Shows the transition from long approximate matches to short, high-value matches (ending in "TEN") as penalty increases. 

#### **Figure 1**: Console output for Weighted Common Substring using seed *42*

![Result Analysis](https://github.com/divyam2207/Optima-Tabula/blob/main/img/Problem1_Results.png)


### 1.5 Implementation & Experimentation Guide

The solution is implemented in a self-contained Java classes (`Problem1.java`). To ensure the experimental results analyzed in  **Part 1.3**  are fully reproducible, the program includes a user prompt for a  `Random Seed`.


-   **File Name:**  `Problem1.java`
    
-   **Compilation & Execution:**
    
    Bash
    
    ```
    javac Problem1.java
    java -cp . Problem1
    ```
    
-   **Execution Steps:**
    
    1.  Run the program.
        
    2.  The console will prompt:  `Enter random seed (Use 42 to match Report):`
        
    3.  **To Reproduce Report Results:**  Enter seed  **42**.
        
        -   This ensures the random string generation matches the analysis where Scenario 1 yields the substring “AR” (or similar short string) and Scenario 2 yields “TEN” in the final experiment.

----------

## Part 2: Largest Zero Sub-matrix

### 2.1 Algorithm Design

Optimization Function:

Let $B$ be the $M \times N$ boolean matrix. We define $DP[i][j]$ as the side length of the largest square sub-matrix of all zeros whose bottom-right corner is at $(i, j)$.

Recurrence Relation:

A square of size $k$ ending at $(i, j)$ can only be formed if the three neighboring blocks (Top, Left, Top-Left) support a square of size $k-1$.

$$DP[i][j] = \begin{cases} 0 & \text{if } B[i][j] \neq 0 \\ \min(DP[i-1][j], DP[i][j-1], DP[i-1][j-1]) + 1 & \text{if } B[i][j] == 0 \end{cases}$$

Justification for Correctness:

If $B[i][j]$ is 0, it extends the smallest of the squares ending at its neighbors. If any neighbor is smaller, it limits the square size at the current position (e.g., if the top neighbor is size 2 but the left is size 5, we can only form a $3 \times 3$ square). This covers all possible square formations.

Implementation Note (Memory Optimization):

The assignment hints at using a byte to store values. Since the maximum square size cannot exceed the matrix dimensions (1000), and $1000 \gg 127$ is rare in random data (probability of a $128 \times 128$ block of zeros is near zero), byte storage is sufficient and reduces memory consumption by 75% compared to int.

### 2.2 Complexity Analysis

-   **Time Complexity:** $O(M \times N)$
    
    -   **Justification:** We perform a single pass over the matrix, doing constant-time comparisons (min of 3 values) at each cell.
        
-   **Space Complexity:** $O(M \times N)$
    
    -   **Justification:** We store a DP table of identical dimensions to the input matrix.
        

### 2.3 Experimental Analysis

We simulated datasets of sizes $10\times10$ up to $1000\times1000$.

**Dataset Size**:

-   **Small ($10\times10$)**
    
    -   **Observed Time Trend:** Negligible (< 1ms)
        
    -   **Observed Memory Trend:** Minimal overhead
        
-   **Large ($1000\times1000$)**
    
    -   **Observed Time Trend:** Linear increase ($O(MN)$)
        
    -   **Observed Memory Trend:** Significant increase

Analysis:

The running time graph (Time vs. Total Elements) is linear, consistent with the $O(M \times N)$ complexity. The memory usage also scales linearly with the number of elements. The use of byte[][] ensured that even for the largest dataset ($10^6$ elements), the heap space remained efficient ($\approx 1$ MB for the array), preventing OutOfMemory errors.

### 2.4 Experimental Output

The following screenshots demonstrate the program execution.

**Figure 2a: Verification Run (Seed 123)**
This output validates the "Optimal Solution Extraction" requirement by identifying the specific coordinates of the largest square in a fixed $15 \times 15$ matrix.

![Insert Screenshot of Part A Output Here]

**Figure 2b: Performance Experiments (Seed 42)**
This output shows the Time and Memory metrics for the 5 increasing dataset sizes.

![PartA](https://github.com/divyam2207/Optima-Tabula/blob/main/img/Problem2_Results_A.png)
![PartB](https://github.com/divyam2207/Optima-Tabula/blob/main/img/Problem2_Results_B.png)


### 2.5 Implementation & Experimentation Guide

The solution is implemented in a self-contained Java class (`Problem2.java`). To ensure the experimental results analyzed in  **Part 2.3** are fully reproducible, the program includes a user prompt for a `Random Seed`.
            

#### Problem 2: Largest Zero Sub-matrix

-   **File Name:** `Problem2.java`
    
-   **Compilation & Execution:**
    
    Bash
    
    ```
    javac Problem2.java
    java -cp . Problem2
    ```
    
-   **Execution Steps:**
    
    1.  Run the program.
        
    2.  The console will prompt for a random seed.
        
    3.  **To Reproduce Part A (Verification):** Enter seed **123**.
        
        -   This generates the specific $15 \times 15$ matrix shown in the report to verify the optimal solution extraction logic (printing coordinates).
            
    4.  **To Reproduce Part B (Performance Experiments):** Enter seed **42**.
        
        -   This generates the 5 datasets used to construct the Time/Memory performance table.
       ---
       
