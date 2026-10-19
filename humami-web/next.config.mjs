/** @type {import("next").NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      // Public CDN for images (spec 034).
      { protocol: "https", hostname: "img.humami.es", pathname: "/**" },
      // Presigned fallback when the backend runs without a public base URL; remove after the rollback window.
      { protocol: "https", hostname: "humami-media-eu.s3.eu-west-3.amazonaws.com", pathname: "/**" },
    ],
    // Image keys contain a UUID, so their content never changes: let browsers keep them for 30 days.
    minimumCacheTTL: 60 * 60 * 24 * 30,
    // The optimizer cache lives in the container's writable layer on the shared host disk: bound it.
    maximumDiskCacheSize: 256 * 1024 * 1024,
  },
};

export default nextConfig;
