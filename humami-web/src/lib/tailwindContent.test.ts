import { readdirSync } from "fs";
import { join } from "path";
import config from "../../tailwind.config";

const SRC_DIR = join(__dirname, "..");

function containsComponents(directory: string): boolean {
  return readdirSync(directory, { withFileTypes: true }).some((entry) =>
    entry.isDirectory() ? containsComponents(join(directory, entry.name)) : entry.name.endsWith(".tsx"),
  );
}

describe("tailwind content globs", () => {
  const globs = config.content as string[];
  const componentDirectories = readdirSync(SRC_DIR, { withFileTypes: true })
    .filter((entry) => entry.isDirectory() && containsComponents(join(SRC_DIR, entry.name)))
    .map((entry) => entry.name);

  it.each(componentDirectories)("scans src/%s so its utility classes are generated", (directory) => {
    expect(globs.some((glob) => glob.startsWith(`./src/${directory}/**`))).toBe(true);
  });
});
