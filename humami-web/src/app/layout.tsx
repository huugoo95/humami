import "./globals.css";
import { Inter } from "next/font/google";
import type { Metadata } from "next";
import { cn } from "@/lib/utils";
import AppHeader from "@/components/AppHeader";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  metadataBase: new URL("https://humami.es"),
  title: {
    default: "Humami",
    template: "%s | Humami",
  },
  description: "Recetas y cocina con sabor auténtico.",
  openGraph: {
    type: "website",
    locale: "es_ES",
    url: "https://humami.es",
    siteName: "Humami",
    title: "Humami",
    description: "Recetas y cocina con sabor auténtico.",
  },
  twitter: {
    card: "summary_large_image",
    title: "Humami",
    description: "Recetas y cocina con sabor auténtico.",
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="es">
      <body
        className={cn(
          inter.className,
          "bg-humami-bg-light text-humami-text-base min-h-screen flex flex-col"
        )}
      >
        <AppHeader />

        <main className="container mx-auto flex-1 p-6">{children}</main>

        <footer className="bg-humami-accent-dark text-humami-bg text-center p-4 mt-8">
          &copy; {new Date().getFullYear()} Humami – Todos los derechos reservados.
        </footer>
      </body>
    </html>
  );
}
