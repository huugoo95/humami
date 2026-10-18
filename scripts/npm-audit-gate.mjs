#!/usr/bin/env node
// Fails on high/critical npm advisories unless an unexpired entry in the allowlist covers them.
// Usage (from an npm project directory): node npm-audit-gate.mjs <allowlist.json>
import { execFileSync } from "node:child_process";
import { readFileSync } from "node:fs";

const GATED_SEVERITIES = new Set(["high", "critical"]);

function runAudit() {
  try {
    return execFileSync("npm", ["audit", "--json"], { encoding: "utf8", maxBuffer: 64 * 1024 * 1024 });
  } catch (error) {
    // npm audit exits non-zero when it finds vulnerabilities; the JSON report is still on stdout.
    if (error.stdout) return error.stdout;
    throw error;
  }
}

function gatedAdvisories(report) {
  const advisories = new Map();
  for (const [packageName, vulnerability] of Object.entries(report.vulnerabilities ?? {})) {
    for (const via of vulnerability.via) {
      if (typeof via === "string" || !GATED_SEVERITIES.has(via.severity)) continue;
      const id = via.url.split("/").pop();
      const advisory = advisories.get(id) ?? { id, severity: via.severity, title: via.title, packages: new Set() };
      advisory.packages.add(packageName);
      advisories.set(id, advisory);
    }
  }
  return [...advisories.values()];
}

const allowlistPath = process.argv[2];
if (!allowlistPath) {
  console.error("Usage: node npm-audit-gate.mjs <allowlist.json>");
  process.exit(2);
}

const today = new Date().toISOString().slice(0, 10);
const allowlist = new Map(JSON.parse(readFileSync(allowlistPath, "utf8")).map((entry) => [entry.id, entry]));
const report = JSON.parse(runAudit());
if (report.error) {
  console.error(`npm audit failed: ${report.error.summary ?? JSON.stringify(report.error)}`);
  process.exit(2);
}

let failed = false;
for (const advisory of gatedAdvisories(report)) {
  const entry = allowlist.get(advisory.id);
  const label = `${advisory.severity} ${advisory.id} ${advisory.title} (${[...advisory.packages].join(", ")})`;
  if (!entry) {
    console.error(`FAIL ${label}`);
    failed = true;
  } else if (entry.expires < today) {
    console.error(`FAIL ${label}: allowlist entry expired on ${entry.expires}`);
    failed = true;
  } else {
    console.log(`ALLOWED until ${entry.expires} ${label}: ${entry.reason}`);
  }
}

if (failed) process.exit(1);
console.log("No unallowlisted high or critical advisories.");
