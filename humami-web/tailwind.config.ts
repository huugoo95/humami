// tailwind.config.ts
import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        // tu paleta antigua
        charcoal:      "#1A1A1A",
        ivory:         "#F7F4EE",
        copper:        "#B76E41",
        olive:         "#3C4B3D",
        "night-blue":  "#22303C",
        "cream-pale":  "#F0EDE6",
        "soft-burgundy": "#4F2A36",
        "soft-gold":   "#D4AF7F",

        "humami-bg": {
          light:   "#faf8f5",
          DEFAULT: "#ffffff",
        },
        "humami-text": {
          base:    "#2e2e2e",
          heading: "#1a1a1a",
        },
        "humami-accent": {
          DEFAULT: "#7B2640", 
          dark:    "#5F1E30",
        },
        gray: {
          200: "#e5e5e5",
          500: "#a0a0a0",
        },
      },
      fontFamily: {
        heading: ["Playfair Display", "serif"],
        body:    ["Inter", "sans-serif"],
      },
    },
  },
  plugins: [],
};

export default config;