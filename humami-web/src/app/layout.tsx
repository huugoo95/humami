import "./globals.css";
import { Inter, Playfair_Display } from "next/font/google";
import type { Metadata } from "next";
import Image from "next/image";
import Link from "next/link";
import { cn } from "@/lib/utils";
import AppHeader from "@/components/AppHeader";
import { BRAND_SOCIAL_IMAGE } from "@/lib/brandMetadata";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
  display: "swap",
});

const playfair = Playfair_Display({
  subsets: ["latin"],
  variable: "--font-playfair",
  display: "swap",
});

export const metadata: Metadata = {
  metadataBase: new URL("https://humami.es"),
  title: {
    default: "Humami",
    template: "%s | Humami",
  },
  description: "Recetas y cocina con sabor auténtico.",
  alternates: {
    canonical: "/",
  },
  icons: {
    icon: [
      { url: "/brand/favicon.svg", type: "image/svg+xml" },
      { url: "/brand/humami-favicon-32.png", sizes: "32x32", type: "image/png" },
    ],
    shortcut: "/brand/favicon.ico",
    apple: [{ url: "/brand/humami-favicon-180.png", sizes: "180x180", type: "image/png" }],
  },
  manifest: "/manifest.webmanifest",
  openGraph: {
    type: "website",
    locale: "es_ES",
    url: "https://humami.es",
    siteName: "Humami",
    title: "Humami",
    description: "Recetas y cocina con sabor auténtico.",
    images: [{
      url: BRAND_SOCIAL_IMAGE,
      width: 1200,
      height: 630,
      type: "image/jpeg",
      alt: "Humami — Recetas y cocina con sabor auténtico",
    }],
  },
  twitter: {
    card: "summary_large_image",
    title: "Humami",
    description: "Recetas y cocina con sabor auténtico.",
    images: [BRAND_SOCIAL_IMAGE],
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="es" className={cn(inter.variable, playfair.variable)}>
      <body
        className={cn(
          "font-body bg-humami-bg-light text-humami-text-base min-h-screen flex flex-col"
        )}
      >
        <AppHeader />

        <main className="container mx-auto flex-1 p-6">{children}</main>

        <footer className="mt-8 bg-humami-accent-dark px-4 py-6 text-center text-humami-bg">
          <Link href="/" className="inline-flex transition-opacity hover:opacity-85" aria-label="Humami">
            <Image
              src="/brand/humami-wordmark-reverse.svg"
              alt=""
              width={754}
              height={204}
              className="h-7 w-auto"
              unoptimized
            />
          </Link>
          <p className="mt-3 text-sm">&copy; {new Date().getFullYear()} Humami · Todos los derechos reservados.</p>
        </footer>
      </body>
    </html>
  );
}
