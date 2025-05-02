import type { NextConfig } from "next";

// Asegúrate de que process.env esté disponible
const imageDomain = process.env.NEXT_PUBLIC_IMAGE_DOMAIN || 'localhost'

const nextConfig: NextConfig = {
  images: {
    domains: [imageDomain],
  },
}
export default nextConfig;
