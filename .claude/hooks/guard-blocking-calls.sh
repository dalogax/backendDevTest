#!/bin/sh
# PreToolUse hook (matcher: Write|Edit).
# Blocks edits that introduce blocking calls into the reactive main sources.
# The whole service depends on a non-blocking WebFlux chain, so .block()/Thread.sleep
# in src/main would silently ruin performance under load.
#
# Hooks receive the tool call as JSON on stdin. We grep the raw payload to stay
# dependency-free (no jq required on Windows/Git Bash).

payload=$(cat)

# Only inspect Java files under src/main.
echo "$payload" | grep -Eq '"file_path"[^,]*src[\\/]+main[\\/].*\.java' || exit 0

# Look for blocking primitives in the content being written/edited.
if echo "$payload" | grep -Eq '\.block\(|\.blockFirst\(|\.blockLast\(|\.toFuture\(\)\.get\(|Thread\.sleep'; then
  echo "Blocking call detected in a reactive main source (.block()/Thread.sleep). Keep the WebFlux pipeline non-blocking — use operators like flatMap/delayElement instead. If this is truly intentional, make the edit outside src/main or explain why." >&2
  exit 2
fi

exit 0
