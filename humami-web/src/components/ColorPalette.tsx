import React from "react";

const colors = [
  { name: "humamiBg-light",    hex: "#faf8f5" },
  { name: "humamiBg",          hex: "#ffffff" },
  { name: "humamiText-base",   hex: "#2e2e2e" },
  { name: "humamiText-heading",hex: "#1a1a1a" },
  { name: "humamiAccent",      hex: "#7B2640" },
  { name: "humamiAccent-dark", hex: "#5F1E30" },
  { name: "gray-200",          hex: "#e5e5e5" },
  { name: "gray-500",          hex: "#a0a0a0" },
];

export function ColorPalette() {
  return (
    <div className="p-6 grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
      {colors.map(c => (
        <div key={c.name} className="flex flex-col items-center">
          <div
            className="w-20 h-20 rounded-lg border"
            style={{ backgroundColor: c.hex }}
          />
          <span className="mt-2 text-sm font-mono">{c.name}</span>
          <span className="text-xs">{c.hex}</span>
        </div>
      ))}
    </div>
  );
}