import { execFileSync } from "child_process";
import { readFileSync } from "fs";
import { join } from "path";
import { IMAGE_SIZES } from "./imageSizes";

const WEB_DIR = join(__dirname, "..", "..");
const THIRTY_DAYS_IN_SECONDS = 30 * 24 * 60 * 60;

function loadNextConfig() {
  const output = execFileSync(
    process.execPath,
    ["--input-type=module", "-e", "const { default: config } = await import('./next.config.mjs'); console.log(JSON.stringify(config));"],
    { cwd: WEB_DIR, encoding: "utf8" },
  );
  return JSON.parse(output);
}

describe("image delivery", () => {
  const images = loadNextConfig().images;

  it("lets browsers cache optimized images for at least 30 days", () => {
    expect(images.minimumCacheTTL).toBeGreaterThanOrEqual(THIRTY_DAYS_IN_SECONDS);
  });

  it("bounds the optimizer disk cache", () => {
    expect(images.maximumDiskCacheSize).toBeGreaterThan(0);
    expect(images.maximumDiskCacheSize).toBeLessThanOrEqual(512 * 1024 * 1024);
  });

  it("allows only the HTTPS image CDN and media bucket through remotePatterns", () => {
    expect(images.domains).toBeUndefined();
    expect(images.remotePatterns).toEqual([
      { protocol: "https", hostname: "img.humami.es", pathname: "/**" },
      { protocol: "https", hostname: "humami-media-eu.s3.eu-west-3.amazonaws.com", pathname: "/**" },
    ]);
  });

  it("lets the runtime user write the optimizer cache", () => {
    const runner = readFileSync(join(WEB_DIR, "Dockerfile"), "utf8").split(/^FROM .* AS runner$/m)[1];
    expect(runner).toMatch(/COPY --from=builder --chown=node:node \/app\/\.next \.\/\.next/);
  });

  it("declares sizes for every layout that renders remote images", () => {
    expect(Object.keys(IMAGE_SIZES).sort()).toEqual(["blogCover", "mealCard", "mealDetail"]);
    Object.values(IMAGE_SIZES).forEach((sizes) => {
      const entries = sizes.split(/,\s*(?![^()]*\))/);
      expect(entries.slice(0, -1).every((entry) => /^\(min-width: \d+px\) \d+px$/.test(entry))).toBe(true);
      expect(entries[entries.length - 1]).toMatch(/^calc\(100vw - \d+px\)$/);
    });
  });
});
