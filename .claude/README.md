# .claude

Project-specific configuration for Claude Code for the **Similar Products** service.

## Structure

```
.claude/
├── agents/                         Subagents (name + description frontmatter → the system prompt)
│   ├── spring-boot-dev.md            Reactive Spring Boot developer with full project context
│   ├── test-engineer.md              Writes/runs/diagnoses the automated test suite
│   └── performance-analyst.md        Interprets k6 results and proposes improvements
├── commands/                       Slash commands (user-triggered operational shortcuts)
│   ├── run-app.md    → /run-app      Build & start the app locally on port 5000
│   ├── test.md       → /test         Run the k6 load test
│   ├── unit-test.md  → /unit-test    Run the JUnit suite (mvn test; nothing needs to be running)
│   └── infra.md      → /infra        Start/stop/check the Docker infrastructure
├── skills/                         Skills (each is a DIRECTORY with a SKILL.md; model-invocable)
│   ├── check-endpoints/SKILL.md      Smoke-test the 5 scenarios and validate responses
│   ├── write-tests/SKILL.md          Conventions & pitfalls for adding tests
│   └── analyze-performance/SKILL.md  Run/interpret the load test and recommend fixes
├── hooks/                          Shell scripts wired from settings.json (JSON on stdin)
│   ├── report-infra-status.sh        SessionStart: reports whether mock + app are up
│   ├── guard-blocking-calls.sh       PreToolUse(Write|Edit): blocks .block()/Thread.sleep in main sources
│   └── verify-app-running.sh         PreToolUse(Bash): blocks the k6 test if the app is down
├── CLAUDE.md                       Project context, loaded automatically each session
├── README.md                       This file
└── settings.json                   Permissions + hook wiring
```

## Format notes (why it's laid out this way)

- **Skills are directories**, not flat files: `.claude/skills/<name>/SKILL.md`. The directory name is what
  becomes invocable. Frontmatter uses `name`, `description`, and `allowed-tools`.
- **Commands** are flat `.md` files under `commands/`; the filename becomes the `/command`. `run-app` is named
  to avoid colliding with Claude Code's built-in `run` skill.
- **Hooks** receive the tool call as **JSON on stdin** (there is no `$CLAUDE_TOOL_EXIT_CODE`). In `settings.json`,
  `matcher` filters by **tool name** only (regex, e.g. `Write|Edit`). There is **no field that filters on command
  content** — a `Bash` matcher fires on *every* command, so any command filter has to live inside the script
  (see `verify-app-running.sh`, which exits 0 for anything that isn't a k6 run). Exit code `2` blocks the action
  and sends stderr back as feedback.

## Quick reference

| You want to… | Do |
|--------------|-----|
| Start the app locally | `/run-app` |
| Start / stop Docker infra | `/infra` |
| Run the k6 load test | `/test` |
| Run the JUnit test suite | `/unit-test` |
| Smoke-test all 5 scenarios | ask for the **check-endpoints** skill |
| Analyze load-test performance | ask for the **analyze-performance** skill |
| Add or fix tests | ask for the **write-tests** skill, or the **test-engineer** agent |
| Implement / debug a feature | delegate to the **spring-boot-dev** agent |
