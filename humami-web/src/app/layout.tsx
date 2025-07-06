"use client";

import "./globals.css";
import { Inter } from "next/font/google";
import { cn } from "@/lib/utils";
import Link from "next/link";
import { useState } from "react";

const inter = Inter({ subsets: ["latin"] });

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [open, setOpen] = useState(false);

  return (
    <html lang="es">
      <body
        className={cn(
          inter.className,
          "bg-humami-bg-light text-humami-text-base min-h-screen flex flex-col"
        )}
      >
        <header className="bg-humami-accent-dark text-humami-bg">
          <div className="container mx-auto flex items-center justify-between px-4 py-2">
            {/* Logo */}
            <Link
              href="/"
              className="text-2xl font-semibold tracking-wide hover:text-humami-bg-light transition-colors"
            >
              Humami
            </Link>

            {/* Botón móvil */}
            <button
              className="md:hidden p-1 hover:text-humami-bg-light transition-colors"
              onClick={() => setOpen((v) => !v)}
              aria-label={open ? "Cerrar menú" : "Abrir menú"}
            >
              {open ? (
                // Ícono “X”
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              ) : (
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 8h16M4 16h16" />
                </svg>
              )}
            </button>

            {/* Navegación escritorio */}
            <nav className="hidden md:flex space-x-6">
              <Link href="/" className="hover:text-humami-bg-light transition-colors">
                Inicio
              </Link>
              <Link href="/our-story" className="hover:text-humami-bg-light transition-colors">
                Sobre Nosotros
              </Link>
              <Link href="/meals" className="hover:text-humami-bg-light transition-colors">
                Comidas
              </Link>
            </nav>
          </div>

          {/* Nav móvil desplegable */}
          {open && (
            <nav className="md:hidden bg-humami-accent-dark">
              <ul className="flex flex-col space-y-2 px-4 pb-4">
                {[
                  { href: "/", label: "Inicio" },
                  { href: "/our-story", label: "Sobre Nosotros" },
                  { href: "/meals", label: "Comidas" },
                ].map(({ href, label }) => (
                  <li key={href}>
                    <Link
                      href={href}
                      onClick={() => setOpen(false)}
                      className="block py-1 hover:text-humami-bg-light transition-colors"
                    >
                      {label}
                    </Link>
                  </li>
                ))}
              </ul>
            </nav>
          )}
        </header>

        <main className="container mx-auto flex-1 p-6">{children}</main>

        <footer className="bg-humami-accent-dark text-humami-bg text-center p-4 mt-8">
          &copy; {new Date().getFullYear()} Humami – Todos los derechos reservados.
        </footer>
      </body>
    </html>
  );
}