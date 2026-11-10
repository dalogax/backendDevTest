"""Validate the supplied k6 JSON output and summarize responses by scenario."""

import argparse
from collections import Counter, defaultdict
import json
from pathlib import Path
import statistics


def percentile(values, fraction):
    ordered = sorted(values)
    position = (len(ordered) - 1) * fraction
    lower = int(position)
    upper = min(lower + 1, len(ordered) - 1)
    return ordered[lower] + (ordered[upper] - ordered[lower]) * (position - lower)


def summarize(path):
    statuses = Counter()
    errors = Counter()
    scenarios = defaultdict(list)
    scenario_statuses = defaultdict(Counter)
    for line in path.open(encoding="utf-8"):
        record = json.loads(line)
        if record.get("type") != "Point" or record.get("metric") != "http_req_duration":
            continue
        data = record["data"]
        tags = data.get("tags", {})
        statuses[str(tags.get("status", "unknown"))] += 1
        if tags.get("error"):
            errors[tags["error"]] += 1
        scenario = tags.get("scenario", tags.get("url", "unknown"))
        scenarios[scenario].append(data["value"])
        scenario_statuses[scenario][str(tags.get("status", "unknown"))] += 1
    return {
        "requests": sum(statuses.values()),
        "statuses": dict(statuses),
        "unexpected_responses": sum(count for status, count in statuses.items() if status != "200"),
        "transport_errors": dict(errors),
        "scenarios": {
            name: {
                "requests": len(values),
                "statuses": dict(scenario_statuses[name]),
                "median_ms": round(statistics.median(values), 3),
                "p95_ms": round(percentile(values, 0.95), 3),
                "max_ms": round(max(values), 3),
            }
            for name, values in sorted(scenarios.items())
        },
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("results", type=Path)
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    report = summarize(args.results)
    text = json.dumps(report, indent=2) + "\n"
    if args.output:
        args.output.write_text(text, encoding="utf-8")
    print(text, end="")
    return 0 if report["requests"] and not report["unexpected_responses"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
