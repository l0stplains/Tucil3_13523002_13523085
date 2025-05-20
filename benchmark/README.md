# Benchmark
This directory is used for doing benchmarking on a custom test cases.

> [!NOTE]  
> If you are looking for the benchmarking results please refer to the [test result page](https://github.com/l0stplains/Tucil3_13523002_13523085/tree/main/test/)

## Benchmarking Mode <a name="benchmarking"></a>
Run automated benchmarks on all puzzle instances in a folder and export results to CSV.

1. **Download the JAR file** from the [releases page](https://github.com/l0stplains/Tucil3_13523002_13523085/releases/).
2. Open a terminal and navigate to the directory containing the downloaded JAR.
3. Run the following command to start the benchmarking:
```bash
java -jar tucil_3_stima.jar -b <input-folder> -o <output-file.csv>
```

| Flag | Description                               |
|:----:|:------------------------------------------|
| `-b` | Enables benchmarking, followed by folder path containing `.txt` puzzles. |
| `-o` | Specifies output CSV filename (must end in `.csv`). If omitted, benchmark summary prints to console. |

**Example**:
```bash
java -jar tucil_3_stima.jar -b puzzles/ -o benchmarks/results.csv
```

This produces a CSV with the following columns:
```
Filename, Strategy, Heuristic, Solution Depth, Nodes Expanded, Nodes Generated, Max Open Size, Duration (ms), Has Solution
```

## Presenting The Result
If you are willing to present the result in a Markdown or HTML format, please considering using this scipt.

> [!NOTE]  
> Even if the result is in Markdown, it uses HTML table inside of it.

1. Do the [benchmarking](#benchmarking).
2. Open a terminal and navigate to the directory containing the generated CSV.
3. Run the following command to do the conversion:
```bash
python .\csv_to_markdown.py  <csv-input-file>  <markdown-output-file>
```

|        Arguments         | Description                                             |
|:------------------------:|:--------------------------------------------------------|
|    `<csv-input-file>`    | Specifies input CSV filename (must end in `.csv`).      |
| `<markdown-output-file>` | Specifies output Markdown filename (must end in `.md`). |

**Example**:
```bash
python .\csv_to_markdown.py  benchmarks/results.csv  benchmarks/results.md
```
