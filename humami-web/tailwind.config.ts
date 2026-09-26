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
        "humami-bg": {
          light:   "#F7F4EE",
          DEFAULT: "#ffffff",
        },
        "humami-text": {
          base:    "#2E2E2E",
          heading: "#1A1A1A",
        },
        "humami-accent": {
          DEFAULT: "#7B2640", 
          dark:    "#5F1E30",
        },
        "humami-gold": "#D4AF7F",
        gray: {
          200: "#e5e5e5",
          500: "#a0a0a0",
        },
      },
      fontFamily: {
        heading: ["var(--font-playfair)", "Georgia", "serif"],
        body: ["var(--font-inter)", "Arial", "sans-serif"],
      },
    },
  },
  plugins: [],
};

export default config;
