import type { MetadataRoute } from "next";

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: "Humami",
    short_name: "Humami",
    description: "Recetas y cocina con sabor auténtico.",
    lang: "es",
    start_url: "/",
    display: "standalone",
    background_color: "#F7F4EE",
    theme_color: "#5F1E30",
    icons: [
      { src: "/brand/humami-symbol-192.png", sizes: "192x192", type: "image/png" },
      { src: "/brand/humami-symbol-512.png", sizes: "512x512", type: "image/png" },
    ],
  };
}
