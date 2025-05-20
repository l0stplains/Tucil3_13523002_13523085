#!/usr/bin/env python3
import argparse
import pandas as pd

def generate_html_table(df):
    strategies = ["UCS", "GBFS", "AStar", "BeamSearch"]
    heuristics = ["ZeroHeuristic", "DistanceHeuristic", "BlockingHeuristic", "RecursiveBlockingHeuristic"]
    metrics = [
        ("Solution Depth", "Solution Depth"),
        ("Nodes Expanded",  "Nodes Expanded"),
        ("Nodes Generated", "Nodes Generated"),
        ("Max Open Size",   "Max Open Size"),
        ("Duration (ms)",   "Duration (ms)")
    ]

    html = ['<table>', '  <thead>']
    html.append('    <tr>')
    html.append('      <th rowspan="3">Filename</th>')
    for strat in strategies:
        html.append(f'      <th colspan="{len(heuristics) * len(metrics)}">{strat}</th>')
    html.append('    </tr>')

    html.append('    <tr>')
    for _ in strategies:
        for heu in heuristics:
            html.append(f'      <th colspan="{len(metrics)}">{heu}</th>')
    html.append('    </tr>')

    html.append('    <tr>')
    for _ in strategies:
        for _ in heuristics:
            for _, mlabel in metrics:
                html.append(f'      <th>{mlabel}</th>')
    html.append('    </tr>')
    html.append('  </thead>')
    html.append('  <tbody>')

    for filename, group in df.groupby("Filename", sort=True):
        html.append('    <tr>')
        html.append(f'      <td>{filename}</td>')
        for strat in strategies:
            strat_block = group[group["Strategy"] == strat]
            for heu in heuristics:
                cell = strat_block[strat_block["Heuristic"] == heu]
                if not cell.empty:
                    row = cell.iloc[0]
                    for csv_col, _ in metrics:
                        html.append(f'      <td>{row[csv_col]}</td>')
                else:
                    for _ in metrics:
                        html.append('      <td></td>')
        html.append('    </tr>')

    html.append('  </tbody>')
    html.append('</table>')
    return "\n".join(html)


def generate_summary_markdown(df):
    summary = df.groupby(["Strategy","Heuristic"]).agg(
        avg_nodes_expanded= ("Nodes Expanded", "mean"),
        avg_nodes_generated= ("Nodes Generated", "mean"),
        avg_duration      = ("Duration (ms)",   "mean"),
    ).reset_index()

    md = []
    md.append("## Summary of Results\n")
    md.append("| Strategy | Heuristic                  | Avg. Nodes Expanded | Avg. Nodes Generated | Avg. Duration (ms) |")
    md.append("|----------|----------------------------|-------------:|--------------------:|-------------------:|")
    for _, r in summary.iterrows():
        md.append(
            f"| {r['Strategy']} | {r['Heuristic']} | {r['avg_nodes_expanded']:.0f} "
            f"| {r['avg_nodes_generated']:.0f} | {r['avg_duration']:.0f} |"
        )
    return "\n".join(md)


def main():
    parser = argparse.ArgumentParser(description="CSV → HTML table + Markdown summary")
    parser.add_argument("input_csv",  help="your input CSV file")
    parser.add_argument("output_md",  help="where to write the combined Markdown")
    args = parser.parse_args()

    df = pd.read_csv(args.input_csv, decimal=",")
    with open(args.output_md, "w", encoding="utf-8") as f:
        f.write("## Results\n")
        f.write(generate_html_table(df))
        f.write("\n\n")
        f.write(generate_summary_markdown(df))

    print(f"✅ Written full table + summary to {args.output_md}")


if __name__ == "__main__":
    main()
