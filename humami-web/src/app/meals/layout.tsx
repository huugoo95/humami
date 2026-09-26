import type { Metadata } from "next";
import { mealsMetadata } from "@/lib/brandMetadata";

export const metadata: Metadata = mealsMetadata;

export default function MealsLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return children;
}
